package back;

import front.Controller;
import javafx.application.Platform;
import javafx.scene.control.Tab;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Translator {
    public static volatile UserList users = new UserList();
    public static volatile HashMap<User, MessageList> dialogs = new HashMap<>();
    public static volatile MessageList localChat = new MessageList();

    private User iAm;
    private Controller controller;

    public Translator(){
    }

    public Translator(User iAm, Controller controller) {
        this.iAm = iAm;
        this.controller=controller;
    }

    public void convert(byte[] data, InetAddress ipAddress){
        String str = new String(data);
        str = filterString(str);
        String[] arr = str.split("\\|");

        if(arr.length == 2){//Message instance
            try {
                Message m = Message.valueOf(str);
                User u = users.findByIP(ipAddress);
                if(u==null){
                    return;
                }
                m.setUser(u);
                if(!m.isBroadcast()){
                    if(!dialogs.containsKey(u)){
                        MessageList messageList = new MessageList();
                        messageList.add(m);
                        dialogs.put(u, messageList);
                        //update file
                        FileWorker.updateDialog(dialogs, u);
                        //open tab
                    } else {
                        dialogs.get(u).add(m);
                        FileWorker.updateDialog(dialogs, u);
                        //update file
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.openTab(u,false);
                        }
                    });
                } else
                    localChat.add(m);
                controller.updateMessages();
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        controller.updateMessages();
//                    }
//                });
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        else if(arr.length == 3){//User instance
            User u = User.valueOf(str);
            if(!Arrays.equals(u.getMac(), iAm.getMac())) {
                u.setIpAddress(ipAddress.getAddress());
                u.setTime(System.currentTimeMillis());
                if(!dialogs.containsKey(u)) FileWorker.loadDialog(u);
                users.add(u);
//                if (dialogs.containsKey(u))
//                    if (u.getNickName().equals(dialogs.get(u).getMessages().get(0).getUser().getNickName()))
//                        dialogs.get(u).setNewUser(u);
            }
            //Main.controller.updateList();
        }else{//Unknown instance
            System.err.print("Unknown instance!\n");
        }
    }

    private String filterString(String str){
        String emptySymbol = ""+(char)0;
        return str.replaceAll(emptySymbol, "");
    }
}

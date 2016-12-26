package back;

import front.Controller;
import javafx.application.Platform;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimerTask;

public class UserSenderTimerTask extends TimerTask{
    private User user;
    private ClientUDP client;
    private Controller controller;

    public UserSenderTimerTask(User user, Controller controller, ClientUDP client){
        this.user = user;
        this.controller = controller;
        this.client=client;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            if(Translator.users.isUniqueNick(user.getNickName())){
                if(user!=null) client.send(user.toByte(), InetAddress.getByName("255.255.255.255"));
            }else{
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.showChange();
                        controller.showAlert(false);
                    }
                });
                //this.cancel();
            }
        } catch (UnknownHostException e) {
            //e.printStackTrace();
        }
    }
}
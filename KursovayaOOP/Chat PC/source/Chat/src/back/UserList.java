package back;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class UserList {
    private ArrayList<User> users;

    public UserList(){
        users = new ArrayList<>();
    }

    public UserList(ArrayList<User> users) {
        this.users = users;
    }

    public void add(User user){
        for(int i = 0; i<users.size(); i++){
            if(users.get(i).equals(user)){
                users.set(i, user);
                return;
            }
        }
        users.add(user);
        if(FileWorker.isDialogExists(user)) {
            System.out.println("size - "+FileWorker.loadDialog(user).getMessages().size());
            Translator.dialogs.put(user, FileWorker.loadDialog(user));
        }
    }

    public User findByName(String nick){
        for (User user: users)
            if (user.getNickName().equals(nick))
                return user;
        return null;
    }

    public User findByIP(InetAddress inetAddress){
        byte[] ip = inetAddress.getAddress();
        for(int i =0; i< users.size(); i++){
            User u = users.get(i);
            if(Arrays.equals(u.getIpAddress(), ip)){
                return u;
            }
        }
        return null;
    }

    public boolean isUniqueNick(String nick){
        for(User u: users){
            if(u.getNickName().equals(nick)){
                return false;
            }
        }
        return true;
    }

    public User findByStrMac(String mac){
        for(int i = 0; i<users.size(); i++){
            String strMac = Arrays.toString(users.get(i).getMac());
            if(strMac.equals(mac)){
                return users.get(i);
            }
        }
        return null;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}

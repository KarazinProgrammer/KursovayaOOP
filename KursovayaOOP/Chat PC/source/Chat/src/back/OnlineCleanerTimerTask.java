package back;

import front.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;


public class OnlineCleanerTimerTask extends TimerTask {
    ArrayList<User> users;
    Controller controller;

    public OnlineCleanerTimerTask(ArrayList<User> users, Controller controller) {
        this.controller=controller;
        this.users = users;
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        if(users.size()>0) {
            for (int i = 0; i < users.size(); i++) {
                if ((currentTime - users.get(i).getTime()) >= 3000) {
                    Translator.dialogs.remove(users.get(i));
                    users.remove(i);
                }
            }
            try {
                controller.updateList();
            } catch (IllegalStateException e){}
        }
    }
}

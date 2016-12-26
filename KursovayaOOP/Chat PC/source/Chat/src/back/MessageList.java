package back;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Created by root on 07.12.16.
 */

public class MessageList implements Serializable{
    private ArrayList<Message> messages;

    public MessageList() {
        messages = new ArrayList<>();
    }

    public MessageList(ArrayList<Message> messages) {
        this.messages = messages;
    }



    public void add(Message message){
        messages.add(message);
    }

    public void setNewUser(User user){
        for(Message msg: messages){
            msg.setUser(user);
        }
    }

    public void clean(){
        messages.removeAll(messages);
    }

    public void removeExcess(){     //удаляет лишние, если сообщений в списке больше 100
        if (messages.size()>100)
            messages.removeIf(new Predicate<Message>() {
                @Override
                public boolean test(Message message) {
                    return (messages.indexOf(message)>=100);
                }
            });
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}

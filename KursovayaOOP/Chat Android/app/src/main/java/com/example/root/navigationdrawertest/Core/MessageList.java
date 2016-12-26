package com.example.root.navigationdrawertest.Core;

import java.util.ArrayList;

/**
 * Created by root on 07.12.16.
 */

public class MessageList {
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

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}

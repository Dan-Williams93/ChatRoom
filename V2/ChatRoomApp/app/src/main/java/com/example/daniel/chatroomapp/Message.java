package com.example.daniel.chatroomapp;

/**
 * Created by Daniel on 17/12/2016.
 */

public class Message {

    private String usersId;
    private String message;
    private String sentAt;
    private String name;

    public Message(String usersId, String message, String sentAt, String name) {
        this.usersId = usersId;
        this.message = message;
        this.sentAt = sentAt;
        this.name = name;
    }

    public String getUsersId() {
        return usersId;
    }

    public String getMessage() {
        return message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public String getName() { return name; }
}

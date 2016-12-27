package com.example.daniel.chatroomapp;

/**
 * Created by Daniel on 17/12/2016.
 */

public class PrivateChat {

    private String strChatID;
    private String strChatName;
    private String strRecipientID;
    private String strChatMessage;
    private String strChatImageURL;

    public PrivateChat(String strChatID, String strChatName, String strRecipientID, String strChatMessage, String strChatImageURL) {
        this.strChatID = strChatID;
        this.strChatName = strChatName;
        this.strRecipientID = strRecipientID;
        this.strChatMessage = strChatMessage;
        this.strChatImageURL = strChatImageURL;
    }

    public String getChatId() {
        return strChatID;
    }

    public String getChatName() {
        return strChatName;
    }

    public String getRecipientID() {
        return strRecipientID;
    }

    public String getChatMessage() { return strChatMessage; }

    public String getChatImageURL() { return strChatImageURL; }
}

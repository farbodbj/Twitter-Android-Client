package com.twitter.common.Models.Messages.Textuals;


public class Direct extends Textual {
    public final static int MAX_MESSAGE_LENGTH = 10000;
    private long chatId;
    private String messageText;
    private boolean isReceived;

    public Direct() {
    }

    public Direct(int senderId, String sentAt) {
        super(senderId, sentAt);

    }

    public Direct(int senderId, String sentAt, long chatId, String messageText, boolean isReceived) {
        super(senderId, sentAt);
        this.chatId = chatId;
        this.messageText = messageText;
        this.isReceived = isReceived;
    }
    public long getChatId() {return chatId;}

    public String getMessageText() {return messageText;}

    public boolean getIsReceived(){return isReceived;}

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }
}

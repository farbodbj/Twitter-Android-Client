package com.twitter.common.Models.Messages;



import com.twitter.common.Models.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    public static final String STANDARD_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(STANDARD_TIME_FORMAT);
    private User sender;
    private LocalDateTime sentAt; //TODO: do you need to change the type?

    public Message() {
    }

    public Message(int senderId, String sentAt) {
        this.sender = new User(senderId);//should be removed
        this.sentAt = setFormattedSentAt(sentAt);
    }

    public Message(User sender, String sentAt) {
        this.sender = sender;
        this.sentAt = setFormattedSentAt(sentAt);
    }

    public User getSender() {
        return sender;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public String getFormattedSentAt() {
        return LocalDateTime.now().format(formatter);
    }

    public LocalDateTime setFormattedSentAt(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}

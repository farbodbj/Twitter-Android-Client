package com.twitter.common.Models.Messages.Textuals;


import com.twitter.common.Models.Messages.Message;
import com.twitter.common.Models.User;

public abstract class Textual extends Message {
    public Textual() {
    }

    public Textual(int senderId, String sentAt) {
        super(senderId, sentAt);
    }

    public Textual(User sender, String sentAt) {
        super(sender, sentAt);
    }
}

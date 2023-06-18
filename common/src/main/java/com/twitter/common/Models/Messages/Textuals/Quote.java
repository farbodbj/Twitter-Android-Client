package com.twitter.common.Models.Messages.Textuals;


import com.twitter.common.Models.User;

public class Quote extends Tweet {
    private Tweet quoted;

    public Quote() {
    }

    public Quote(int senderId, String sentAt, String text, int favCount, int retweetCount, int mentionCount, Tweet quoted) {
        super(senderId, sentAt, text, favCount, retweetCount, mentionCount);
        this.quoted = quoted;
    }

    public Quote(User user, Tweet tweet) {
        setSender(user);
        setQuoted(tweet);
    }

    public void setQuoted(Tweet quoted) {
        this.quoted = quoted;
    }

    public Tweet getQuoted() {
        return quoted;
    }
}

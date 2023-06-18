package com.twitter.common.Models.Messages.Textuals;

import com.twitter.common.Models.User;

public class Mention extends Tweet {
    private Tweet mentionedTo;

    public Mention() {
    }

    public Mention(int senderId, String sentAt, String text, int favCount, int retweetCount, int mentionCount) {
        super(senderId, sentAt, text, favCount, retweetCount, mentionCount);
    }

    public Mention(User user, Tweet tweet) {
        setSender(user);
        setMentionedTo(tweet);
    }

    public Tweet getMentionedTo() {
        return mentionedTo;
    }

    public void setMentionedTo(Tweet mentionedTo) {
        this.mentionedTo = mentionedTo;
    }
}

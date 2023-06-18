package com.twitter.common.Models.Messages.Textuals;

public class Retweet extends Tweet {
    private Tweet retweeted;

    public Retweet() {
    }

    public Retweet(long retweetedId, int senderId, String sentAt, Tweet retweeted) {
        super(senderId, sentAt);
        this.retweeted = retweeted;
        retweeted.setTweetId(retweetedId);
    }

    public Tweet getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Tweet retweeted) {
        this.retweeted = retweeted;
    }
}

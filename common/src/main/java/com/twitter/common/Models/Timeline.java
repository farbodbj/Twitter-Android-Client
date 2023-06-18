package com.twitter.common.Models;


import com.twitter.common.Models.Messages.Textuals.Tweet;

import java.util.ArrayList;
import java.util.List;

public class Timeline {//TODO: it can implement Collections, might be a good idea for some situations
    private final List<Tweet> timelineTweets = new ArrayList<>();
    private int forUser;

    public void showAll() {
        timelineTweets.forEach(System.out::println);
    }

    public List<Tweet> getTimelineTweets() {
        return timelineTweets;
    }

    public void addAll(List<Tweet> tweets) {
        timelineTweets.addAll(tweets);
    }

    public int getForUser() {
        return forUser;
    }

    public void setForUser(int forUser) {
        this.forUser = forUser;
    }
}

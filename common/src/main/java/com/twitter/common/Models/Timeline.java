package com.twitter.common.Models;


import com.twitter.common.Models.Messages.Textuals.Tweet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Timeline extends ArrayList<Tweet> {//TODO: it can implement Collections, might be a good idea for some situations
    private final List<Tweet> timelineTweets = new ArrayList<>();
    private int forUser;

    public List<Tweet> getTimelineTweets() {
        return timelineTweets;
    }

    @Override
    public boolean addAll(Collection<? extends Tweet> c) {
        return timelineTweets.addAll(c);
    }

    @Override
    public Tweet get(int index) {
        return timelineTweets.get(index);
    }

    @Override
    public int size() {
        return timelineTweets.size();
    }

    public int getForUser() {
        return forUser;
    }

    public void setForUser(int forUser) {
        this.forUser = forUser;
    }
}

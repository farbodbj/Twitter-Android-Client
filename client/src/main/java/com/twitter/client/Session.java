package com.twitter.client;

import com.twitter.common.Models.User;

public class Session {
    private User sessionUser;
    private static Session instance;

    public static Session getInstance() {
        if(instance != null)
            return instance;

        instance = new Session();
        return instance;
    }

    private Session() {

    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }
}

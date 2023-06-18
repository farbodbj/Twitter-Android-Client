package com.twitter.client;

import com.twitter.common.Models.User;

public class Session {
    private User sessionUser;

    public Session() {
    }

    public Session(User sessionUser) {
        this.sessionUser = sessionUser;
    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }
}

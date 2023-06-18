package com.twitter.common.Models;


import com.twitter.common.Models.Messages.Textuals.Direct;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private final List<Direct> messages;
    private final long chatId;
    private final User user1;
    private final User user2;

    public Chat(int userId1, int userId2) {
        this.messages = new ArrayList<>();
        this.user1 = new User(userId1);
        this.user2 = new User(userId2);
        this.chatId = (long) user1.getUserId() << 32 | (long)user2.getUserId();
    }

    public void addDM(Direct dm) {
        messages.add(dm);
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public long getChatId() {
        return chatId;
    }
}

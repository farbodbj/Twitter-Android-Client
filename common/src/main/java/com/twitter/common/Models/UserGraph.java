package com.twitter.common.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserGraph extends ArrayList<User>  {
    List<User> users;

    public UserGraph(Collection<User> users) {
        this.users = new ArrayList<>();
        this.users.addAll(users);
    }

    @Override
    public User get(int index) {
        return users.get(index);
    }

    @Override
    public int size() {
        return users.size();
    }
}

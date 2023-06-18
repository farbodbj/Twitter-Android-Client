package com.twitter.server.Database;

public interface Insertable<T> {
    boolean insert(T toAdd);
}

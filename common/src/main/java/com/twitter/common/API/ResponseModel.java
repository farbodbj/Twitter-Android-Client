package com.twitter.common.API;

public class ResponseModel<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private final T content;

    public ResponseModel(int status, boolean success, String message, T content) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T get() {
        return content;
    }
}

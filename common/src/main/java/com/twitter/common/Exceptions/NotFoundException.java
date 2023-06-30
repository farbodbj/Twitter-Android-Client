package com.twitter.common.Exceptions;

public class NotFoundException extends HandledException {
    public final static String MESSAGE = "record not found.";

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {super(MESSAGE);}
}

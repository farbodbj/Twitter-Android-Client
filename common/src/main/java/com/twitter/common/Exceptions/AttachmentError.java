package com.twitter.common.Exceptions;

public class AttachmentError extends HandledException {
    private final static String MESSAGE = "adding attachment failed";
    public AttachmentError() {
        super(MESSAGE);
    }

    public AttachmentError(String message) {
        super(message);
    }
}

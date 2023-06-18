package com.twitter.common.Exceptions;

public abstract class SignUpExceptions extends HandledException {
    public SignUpExceptions(String message) {
        super(message);
    }
}

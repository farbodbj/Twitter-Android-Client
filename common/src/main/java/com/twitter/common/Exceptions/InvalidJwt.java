package com.twitter.common.Exceptions;

public class InvalidJwt extends HandledException {
    private final static String MESSAGE = "wrong username or password";
    public InvalidJwt() {super(MESSAGE);}
}

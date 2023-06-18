package com.twitter.common.Exceptions;

public class WrongCredentials extends SignInExceptions {
    private final static String message = "wrong username or password";
    public WrongCredentials() {super(message);}
}

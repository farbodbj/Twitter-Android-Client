package com.twitter.common.Exceptions;

public class IllegalUserAction extends HandledException {
    public final static String MESSAGE = "Illegal action requested by user";
    public IllegalUserAction() {super(MESSAGE);}
}

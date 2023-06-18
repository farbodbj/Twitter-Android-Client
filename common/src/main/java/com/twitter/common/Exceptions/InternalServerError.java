package com.twitter.common.Exceptions;

public class InternalServerError extends HandledException {
    public final static String MESSAGE = "internal server error.";
    public InternalServerError() {super(MESSAGE);}
}

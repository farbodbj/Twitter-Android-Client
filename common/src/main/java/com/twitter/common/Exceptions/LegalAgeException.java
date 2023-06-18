package com.twitter.common.Exceptions;

public class LegalAgeException extends SignUpExceptions {
    public final static String MESSAGE = "sorry you are not 18 yet.";
    public LegalAgeException() {super(MESSAGE);}
}

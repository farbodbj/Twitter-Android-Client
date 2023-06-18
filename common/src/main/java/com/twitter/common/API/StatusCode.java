package com.twitter.common.API;

//this class is not meant to be instantiated, therefor the constructor is private
final public class StatusCode {
    public static final int SUCCESS = 200;
    public static final int UNKNOWN_ERROR = 500;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_ALLOWED = 405;
    public static final int BAD_REQUEST = 400;
    public static final int DUPLICATE_RECORD = 409;
    private StatusCode(){}
}

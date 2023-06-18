package com.twitter.common.API;

final public class ResponseMessage {
    public final static String METHOD_NOT_ALLOWED = "405 Method Not Allowed";
    public final static String PARSE_ERROR = "Error when parsing an http request.";
    public final static String BAD_REQUEST = "400 bad request.";
    public final static String INTERNAL_SERVER_ERROR = "internal server error.";

    private ResponseMessage() {}
}

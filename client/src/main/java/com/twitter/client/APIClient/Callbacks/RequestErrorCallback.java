package com.twitter.client.APIClient.Callbacks;

public interface RequestErrorCallback extends Callback{
    void onRequestError(Exception exception) ;
}
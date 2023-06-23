package com.twitter.client.APIClient.Callbacks;

public interface ErrorCallback extends Callback{
    void onError(Exception error);
}

package com.twitter.client.APIClient.Callbacks;

@FunctionalInterface
public interface ErrorCallback extends Callback{
    void onError(Exception error);
}

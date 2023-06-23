package com.twitter.client.APIClient.Callbacks;

public interface SuccessCallback<T> extends Callback{
    void onSuccess(T result);
}

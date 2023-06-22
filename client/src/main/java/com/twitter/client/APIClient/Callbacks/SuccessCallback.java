package com.twitter.client.APIClient.Callbacks;

public interface SuccessCallback<T> {
    void onSuccess(T result);
}

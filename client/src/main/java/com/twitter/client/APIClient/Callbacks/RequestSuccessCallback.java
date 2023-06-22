package com.twitter.client.APIClient.Callbacks;

import com.twitter.common.API.ResponseModel;

public interface RequestSuccessCallback<T> {
    void onRequestSuccess(ResponseModel<T> responseModel);

}

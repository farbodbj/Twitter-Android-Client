package com.twitter.client.APIClient.Callbacks;

import com.twitter.common.API.ResponseModel;

public interface RequestSuccessCallback<T> extends Callback{
    void onRequestSuccess(ResponseModel<T> responseModel);

}

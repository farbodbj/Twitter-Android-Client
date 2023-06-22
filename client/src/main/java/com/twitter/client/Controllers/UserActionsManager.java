package com.twitter.client.Controllers;

import com.twitter.client.APIClient.Callbacks.ErrorCallback;
import com.twitter.client.APIClient.Callbacks.SuccessCallback;
import com.twitter.client.APIClient.ClientHttpUtils;
import com.twitter.client.APIClient.Callbacks.RequestErrorCallback;
import com.twitter.client.APIClient.Callbacks.RequestSuccessCallback;
import com.twitter.client.Session;
import com.sun.net.httpserver.Headers;
import com.twitter.common.API.API;
import com.twitter.common.API.ResponseModel;
import com.twitter.common.Exceptions.*;
import com.twitter.common.Models.Messages.Textuals.Direct;
import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.common.Models.Messages.Textuals.Quote;
import com.twitter.common.Models.Messages.Textuals.Retweet;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Timeline;
import com.twitter.common.Models.User;
import com.twitter.common.Utils.JwtUtils;

import java.util.HashMap;

import java.util.Map;


import static com.twitter.common.API.StatusCode.*;

//TODO: client should not have server as a dependency, solve this problem
public class UserActionsManager {
    private static Session session;
    private static UserActionsManager instance;

    private UserActionsManager() {
        session = new Session();
    }

    public static UserActionsManager getInstance() {
        if(instance != null)
            return instance;
        
        instance = new UserActionsManager();
        return instance;
    }

    public void signUp(User user, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        ClientHttpUtils.post(
            API.SIGN_UP,
            user,
            Object.class,
            error -> {
            },
            responseModel -> {
                if (!responseModel.isSuccess()) {
                    try {
                        switch (responseModel.getStatus()) {
                            case NOT_ALLOWED ->
                                    onException.onError(new LegalAgeException());

                            case DUPLICATE_RECORD ->
                                    onException.onError(new DuplicateRecordException(responseModel.getMessage()));

                            default ->
                                    onException.onError(new IllegalStateException("Unexpected value: " + responseModel.getStatus()));
                        }
                    } catch (IllegalStateException ignore) {
                        onSuccess.onSuccess(false);
                    }
                } else {
                    onSuccess.onSuccess(true);
                }
            });
    }

    public void signIn(String username, String passwordHash, SuccessCallback<User> onSuccess, ErrorCallback onException) throws HandledException {
        Map<String, String> query = new HashMap<>();
        query.put("username", username);
        query.put("passwordHash", passwordHash);

        ClientHttpUtils.get(
                API.SIGN_IN,
                query,
                User.class,
                error -> {

                },
                responseModel -> {
                    if(!responseModel.isSuccess()) {
                        onSuccess.onSuccess(null);
                        try {
                            switch(responseModel.getStatus()) {
                                case UNAUTHORIZED ->
                                        onException.onError(new WrongCredentials());

                                case UNKNOWN_ERROR ->
                                        onException.onError(new InternalServerError());

                                default ->
                                        onException.onError(new IllegalStateException("Unexpected value: " + responseModel.getStatus()));
                            }
                        } catch (IllegalStateException e) {onException.onError(e);}
                    } else {
                        session.setSessionUser(responseModel.get());
                        onSuccess.onSuccess(responseModel.get());
                    }
                });

    }


    //TODO: you may or may not need to change input type of this method to String
    public boolean follow(int followerId, int followedId) throws HandledException {
        return updateFollowStatus(API.FOLLOW, followerId, followedId);
    }

    public boolean unfollow(int followerId, int followedId) throws HandledException {
        return updateFollowStatus(API.UNFOLLOW, followerId, followedId);
    }


    public boolean tweet(Tweet tweet) throws HandledException {
        return postRequest(API.TWEET, tweet);
    }

    public  boolean quote(Quote quote) throws HandledException{
        return postRequest(API.QUOTE, quote);
    }

    public  boolean retweet(Retweet retweet) throws HandledException {
        return postRequest(API.RETWEET, retweet);
    }

    public  boolean mention(Mention mention) throws HandledException {
        return postRequest(API.MENTION, mention);
    }

    public  boolean block(int blockerId, int blockedId) throws HandledException {
        return updateBlockStatus(API.BLOCK, blockerId, blockedId);
    }

    public  boolean unblock(int blockerId, int blockedId) throws HandledException {
        return updateBlockStatus(API.UNBLOCK, blockerId, blockedId);
    }

    public Timeline getTimeline(int userId, int MAX_COUNT) throws HandledException {
        Headers headers = JwtUtils.getJwtHeader(session.getSessionUser().getUserId());

        Map<String, String> query = new HashMap<>();
        query.put("userId", String.valueOf(userId));
        query.put("max", String.valueOf(MAX_COUNT));

        //ResponseModel<Timeline> timeline = ClientHttpUtils.get(API.GET_TIMELINE, query, headers, Timeline.class,  error->{});

        //return timeline.get();
        return null;
    }


    private  boolean updateFollowStatus(String APIEndpoint, int followerId, int followedId) throws HandledException {
        Map<String, String> followParams = new HashMap<>();
        followParams.put("followed", String.valueOf(followedId));
        followParams.put("follower", String.valueOf(followerId));

        return updateStatus(APIEndpoint, followParams, Object.class);
    }

    private  boolean updateBlockStatus(String APIEndpoint, int blockerId, int blockedId) throws HandledException {
        Map<String, String> blockParams = new HashMap<>();
        blockParams.put("blocked", String.valueOf(blockedId));
        blockParams.put("blocker", String.valueOf(blockerId));

        return updateStatus(APIEndpoint, blockParams, Object.class);
    }

    public boolean postRequest(String endpoint, Tweet requestObject) throws HandledException {
//        ResponseModel<Object> response = ClientHttpUtils.postSerialized(
//                endpoint,
//                requestObject,
//                JwtUtils.getJwtHeader(session.getSessionUser().getUserId()),
//                Object.class,
//                error->{
//                    System.out.println(error.getMessage());
//                });
//
//        if(!response.isSuccess()) {
//            try {
//                switch(response.getStatus()) {
//                    case UNAUTHORIZED -> throw new InternalServerError();
//                    default -> throw new IllegalStateException("Unexpected value: " + response.getStatus());
//                }
//            } catch (IllegalStateException ignore) {}
//            return false;
//        }
        return true;
    }

    private  <T> boolean updateStatus(String APIEndpoint, Map<String, String> params, Class<T> tClass) throws HandledException {
//        Headers headers = JwtUtils.getJwtHeader(session.getSessionUser().getUserId());
//
//        ResponseModel<T> response = ClientHttpUtils.post(
//                APIEndpoint,
//                params,
//                headers,
//                tClass,
//                error->{
//
//                }
//        );
//
//        if(!response.isSuccess()) {
//            try {
//                switch(response.getStatus()) {
//                    case UNKNOWN_ERROR -> throw new InternalServerError();
//                    case NOT_ALLOWED -> throw new IllegalUserAction();
//                    case UNAUTHORIZED -> throw new HandledException("permission denied");
//                    default -> throw new IllegalStateException("Unexpected value: " + response.getStatus());
//                }
//            } catch (IllegalStateException ignore) {}
//            return false;
//        }

        return true;
    }


}

package com.twitter.client.Controllers;

import com.twitter.client.APIClient.Callbacks.ErrorCallback;
import com.twitter.client.APIClient.Callbacks.SuccessCallback;
import com.twitter.client.APIClient.ClientHttpUtils;
import com.twitter.client.APIClient.Callbacks.RequestErrorCallback;
import com.twitter.client.Session;
import com.sun.net.httpserver.Headers;
import com.twitter.common.API.API;
import com.twitter.common.Exceptions.*;
import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.common.Models.Messages.Textuals.Quote;
import com.twitter.common.Models.Messages.Textuals.Retweet;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Timeline;
import com.twitter.common.Models.User;
import com.twitter.common.Models.UserGraph;
import com.twitter.common.Utils.JwtUtils;

import java.util.HashMap;

import java.util.List;
import java.util.Map;


import static com.twitter.common.API.StatusCode.*;

//TODO: client should not have server as a dependency, solve this problem
public class UserActionsManager {
    private static UserActionsManager instance;

    private UserActionsManager() {
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
    null,
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

        ClientHttpUtils.getSerialized(
                API.SIGN_IN,
                query,
        null,
                User.class,
                error -> {
                    //TODO: place error handling logic here
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
                        Session.getInstance().setSessionUser(responseModel.get());
                        onSuccess.onSuccess(responseModel.get());
                    }
                });

    }

    public void usernameExists(String username, SuccessCallback<Boolean> onSuccess, RequestErrorCallback onException) {
        Map<String, String> query = new HashMap<>();
        query.put("username", username);

        ClientHttpUtils.get(
                API.CHECK_USERNAME,
                query,
        null,
                Boolean.class,
                error->{},
                responseModel -> onSuccess.onSuccess(responseModel.get()));
    }

    public void emailExists(String email, SuccessCallback<Boolean> onSuccess, RequestErrorCallback onException) {
        Map<String, String> query = new HashMap<>();
        query.put("email", email);

        ClientHttpUtils.get(
                API.CHECK_EMAIL,
                query,
        null,
                Boolean.class,
                error->{},
                responseModel -> onSuccess.onSuccess(responseModel.get()));
    }

    public void follow(int followerId, int followedId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        updateFollowStatus(API.FOLLOW, followerId, followedId, onSuccess, onException);
    }

    public void unfollow(int followerId, int followedId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        updateFollowStatus(API.UNFOLLOW, followerId, followedId, onSuccess, onException);
    }

    public void tweet(Tweet tweet,  SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        postRequest(API.TWEET, tweet, onSuccess, onException);
    }

    public void quote(Quote quote  ,SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        postRequest(API.QUOTE, quote, onSuccess, onException);
    }

    public void retweet(Retweet retweet,  SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        postRequest(API.RETWEET, retweet, onSuccess, onException);
    }

    public void mention(Mention mention,  SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        postRequest(API.MENTION, mention, onSuccess, onException);
    }

    public void block(int blockerId, int blockedId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        updateBlockStatus(API.BLOCK, blockerId, blockedId, onSuccess, onException);
    }

    public void unblock(int blockerId, int blockedId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        updateBlockStatus(API.UNBLOCK, blockerId, blockedId, onSuccess, onException);
    }

    public void like(int likerId, long tweetId,  SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        updateLikeStatus(API.LIKE, likerId, tweetId, onSuccess, onException);
    }

    public void unlike(int likerId, long tweetId,  SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        updateLikeStatus(API.UNLIKE, likerId, tweetId, onSuccess, onException);
    }

    public void getTimeline(int userId, int MAX_COUNT, SuccessCallback<Timeline> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());

        Map<String, String> query = new HashMap<>();
        query.put("userId", String.valueOf(userId));
        query.put("max", String.valueOf(MAX_COUNT));

        ClientHttpUtils.getSerialized(
                API.GET_TIMELINE,
                query,
                headers,
                Timeline.class,
                error->{
                    //Error handling logic
                },
                responseModel -> onSuccess.onSuccess(responseModel.get())
        );
    }

    public void getUser(int userId, SuccessCallback<User> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());

        Map<String, String> query = new HashMap<>();
        query.put("userId", String.valueOf(userId));

        ClientHttpUtils.getSerialized(
                API.GET_PROFILE,
                query,
                headers,
                User.class,
                onException::onError,
                responseModel -> {
                    onSuccess.onSuccess(responseModel.get());
                });
    }

    public void setNewProfilePic(int userId, Image newProfilePic, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());
        headers.put("userId", List.of(String.valueOf(userId)));

        ClientHttpUtils.postSerialized(
                API.SET_PROFILE_PIC,
                newProfilePic,
                headers,
                Boolean.class,
                error -> {
                    //Error handling logic
                },
                responseModel->{
                    onSuccess.onSuccess(responseModel.get());
                });
    }

    public void setNewHeaderPic(int userId, Image newHeaderPic, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());
        Map<String, String> query = new HashMap<>();
        headers.put("userId", List.of(String.valueOf(userId)));

        ClientHttpUtils.postSerialized(
                API.SET_HEADER,
                newHeaderPic,
                headers,
                Boolean.class,
                error -> {
                    //Error handling logic
                },
                responseModel->{
                    onSuccess.onSuccess(responseModel.get());
                });

    }

    public void setNewAccountName(int userId, String newAccountName, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        setUserTextualAttribHelper(
                API.SET_DISPLAY_NAME,
                userId,
                "displayName",
                newAccountName,
                onSuccess,
                onException);
    }

    public void setNewLocation(int userId, String newLocation, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        setUserTextualAttribHelper(
                API.SET_LOCATION,
                userId,
                "location",
                newLocation,
                onSuccess,
                onException);
    }

    public void setNewBio(int userId, String newBio, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        setUserTextualAttribHelper(
                API.SET_BIO,
                userId,
                "bio",
                newBio,
                onSuccess,
                onException);
    }

    public void getFollowings(int userId, SuccessCallback<List<User>> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());
        Map<String, String> query = new HashMap<>();
        query.put("userId", String.valueOf(userId));

        ClientHttpUtils.getSerialized(
                API.GET_FOLLOWINGS,
                query,
                headers,
                UserGraph.class,
                error->{

                },
                responseModel -> onSuccess.onSuccess(responseModel.get())
        );
    }


    public void getFollowingsCount(int userId, SuccessCallback<Integer> onSuccess, ErrorCallback onException) {
        getCount(API.GET_FOLLOWINGS_COUNT, userId, onSuccess, onException);
    }


    public void getFollowers(int userId, SuccessCallback<List<User>> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());

        Map<String, String> query = new HashMap<>();
        query.put("userId", String.valueOf(userId));

        ClientHttpUtils.getSerialized(
                API.GET_FOLLOWERS,
                query,
                headers,
                UserGraph.class,
                error->{

                },
                responseModel -> onSuccess.onSuccess(responseModel.get())
        );
    }

    public void getFollowersCount(int userId, SuccessCallback<Integer> onSuccess, ErrorCallback onException) {
        getCount(API.GET_FOLLOWERS_COUNT, userId, onSuccess, onException);
    }

    public void searchForUser(String searchTerm, SuccessCallback<UserGraph> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());
        Map<String, String> query = new HashMap<>();
        query.put("search_term", searchTerm);

        ClientHttpUtils.getSerialized(
                API.SEARCH_USERS,
                query,
                headers,
                UserGraph.class,
                error->{

                },
                responseModel -> {
                    onSuccess.onSuccess(responseModel.get());
                }
        );

    }



    private void getCount(String apiEndpoint, int userId, SuccessCallback<Integer> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());

        Map<String, String> query = new HashMap<>();
        query.put("userId", String.valueOf(userId));

        ClientHttpUtils.get(
                apiEndpoint,
                query,
                headers,
                Integer.class,
                onException::onError,
                responseModel -> onSuccess.onSuccess(responseModel.get())
        );
    }

    private void setUserTextualAttribHelper(String path, int userId, String attribName, String attribValue, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());
        headers.put("userId", List.of(String.valueOf(userId)));
        headers.put(attribName, List.of(attribValue));

        ClientHttpUtils.post(
                path,
                null,
                headers,
                Boolean.class,
                exception -> {

                },
                responseModel -> {
                    onSuccess.onSuccess(responseModel.get());
                }
        );
    }


    private void updateFollowStatus(String APIEndpoint, int followerId, int followedId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Map<String, String> followParams = new HashMap<>();
        followParams.put("followed", String.valueOf(followedId));
        followParams.put("follower", String.valueOf(followerId));

        updateStatus(APIEndpoint, followParams, HashMap.class, onSuccess, onException);
    }

    private void updateBlockStatus(String APIEndpoint, int blockerId, int blockedId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Map<String, String> blockParams = new HashMap<>();
        blockParams.put("blocked", String.valueOf(blockedId));
        blockParams.put("blocker", String.valueOf(blockerId));

        updateStatus(APIEndpoint, blockParams, HashMap.class, onSuccess, onException);
    }

    private void updateLikeStatus(String APIEndpoint, int likerId, long tweetId, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Map<String, String> likeParams = new HashMap<>();
        likeParams.put("userId", String.valueOf(likerId));
        likeParams.put("tweetId", String.valueOf(tweetId));

        updateStatus(APIEndpoint, likeParams, HashMap.class, onSuccess, onException);

    }

    private void postRequest(String endpoint, Tweet requestObject, SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        ClientHttpUtils.postSerialized(
                endpoint,
                requestObject,
                JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId()),
                Integer.class,
                error->{
                    //Error handling logic
                },
                responseModel -> {
                    if(!responseModel.isSuccess()) {
                        try {
                            onException.onError(
                                    switch(responseModel.getStatus()) {
                                        case UNAUTHORIZED ->
                                                new InternalServerError();

                                        default ->
                                                new IllegalStateException("Unexpected value: " + responseModel.getStatus());
                                    });
                        } catch (IllegalStateException ignore) {}
                    }

                    onSuccess.onSuccess(responseModel.getStatus() == SUCCESS);

                });
    }

    private  <T> void updateStatus(String APIEndpoint, Map<String, String> params, Class<T> responseContentType , SuccessCallback<Boolean> onSuccess, ErrorCallback onException) {
        Headers headers = JwtUtils.getJwtHeader(Session.getInstance().getSessionUser().getUserId());

        ClientHttpUtils.post(
                APIEndpoint,
                params,
                headers,
                responseContentType,
                error->{
                    //Error handling logic
                },
                responseModel -> {
                    if(!responseModel.isSuccess()) {
                        try {
                            onException.onError(
                                switch(responseModel.getStatus()) {
                                    case DUPLICATE_RECORD ->
                                        new DuplicateRecordException("duplicate record");

                                    case NOT_FOUND ->
                                        new NotFoundException();

                                    case UNKNOWN_ERROR ->
                                            new InternalServerError();

                                    case NOT_ALLOWED ->
                                            new IllegalUserAction();

                                    case UNAUTHORIZED ->
                                            new HandledException("permission denied");

                                    default ->
                                            new IllegalStateException("Unexpected value: " + responseModel.getStatus());

                            });
                        } catch (IllegalStateException ignore) {}

                    }
                    onSuccess.onSuccess( responseModel.getStatus() == SUCCESS);
                });
    }


}

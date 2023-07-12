package com.twitter.common.API;

public class API {
    public static final int PORT = 6985;

    public final static String SERVER_IP = "192.168.1.141";
    public static final String BASE_URL = "http://" + SERVER_IP + ":" + PORT;

    public static final String AUTH_ENDPOINT = "/auth";
    public static final String SIGN_UP = AUTH_ENDPOINT + "/sign-up";
    public static final String SIGN_IN = AUTH_ENDPOINT + "/sign-in";

    public static final String FOLLOW_ENDPOINT = "/follow";
    public static final String FOLLOW = FOLLOW_ENDPOINT + "/add"; //adds a follow relationship
    public static final String UNFOLLOW = FOLLOW_ENDPOINT + "/remove"; //removes a follow relationship

    public static final String TWEET_ENDPOINT = "/tweet";
    public static final String TWEET = TWEET_ENDPOINT + "/new"; //creates a new tweet

    public static final String QUOTE = TWEET_ENDPOINT + "/quote"; //quotes a tweet and creates a new one
    public static final String RETWEET = TWEET_ENDPOINT + "/retweet"; //retweets a tweet
    public static final String MENTION = TWEET_ENDPOINT + "/mention"; //mentions a user in a tweet
    public static final String LIKE = TWEET_ENDPOINT + "/like"; //likes a tweet
    public static final String UNLIKE = TWEET_ENDPOINT + "/unlike"; //unlikes a tweet


    public static final String USER_ENDPOINT = "/user";
    public static final String GET_PROFILE = USER_ENDPOINT + "/profile"; //returns user's profile information
    public static final String SEARCH_USERS = "/search-users"; //searches users
    public static final String SEARCH_TWEETS = "/search-tweets"; //searches tweets
    public static final String GET_TIMELINE = USER_ENDPOINT + "/timeline"; //returns user's timeline
    public static final String BLOCK = USER_ENDPOINT + "/block"; //blocks a user
    public static final String UNBLOCK = USER_ENDPOINT + "/unblock"; //unblocks a user
    public static final String SET_PROFILE_PIC = USER_ENDPOINT + "/set-profile"; //updates user's profile
    public static final String SET_HEADER = USER_ENDPOINT + "/set-header"; //updates user's header
    public static final String SET_USERNAME = USER_ENDPOINT + "/set-username"; //updates user's username
    public static final String SET_DISPLAY_NAME = USER_ENDPOINT + "/set-display-name";
    public static final String SET_BIO = USER_ENDPOINT + "/set-bio"; //updates user's bio
    public static final String SET_LOCATION = USER_ENDPOINT + "/set-display-location";
    public static final String GET_FOLLOWERS = USER_ENDPOINT + "/get-followers";
    public static final String GET_FOLLOWERS_COUNT = USER_ENDPOINT + "/get-followers-count";
    public static final String GET_FOLLOWINGS = USER_ENDPOINT + "/get-followings";
    public static final String GET_FOLLOWINGS_COUNT = USER_ENDPOINT + "/get-followings-count";


    public final static String MESSAGE_ENDPOINT = "/message";
    public final static String SEND_TEXT_MESSAGE = MESSAGE_ENDPOINT + "/send-text";
    public final static String SEND_IMAGE =  MESSAGE_ENDPOINT + "/send-image";
    public final static String SEND_VIDEO =  MESSAGE_ENDPOINT + "/send-video";
    public final static String UPDATE_CHAT =  MESSAGE_ENDPOINT + "/update-chat";

    public static final String CHECK_EXISTENCE_ENDPOINT = "/check-existence";
    public static final String CHECK_USERNAME = CHECK_EXISTENCE_ENDPOINT + "/username"; //checks for duplicate email
    public static final String CHECK_EMAIL = CHECK_EXISTENCE_ENDPOINT + "/email"; //checks for duplicate username
}

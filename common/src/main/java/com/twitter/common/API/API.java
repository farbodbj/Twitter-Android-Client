package com.twitter.common.API;

public class API {
    public static final int PORT = 6985;
    public static final String BASE_URL = "http://192.168.1.141:" + PORT;

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

    public static final String MESSAGE = "/message";
    public static final String DIRECT = "/message";


    public static final String USER_ENDPOINT = "/user";
    public static final String GET_PROFILE = USER_ENDPOINT + "/profile"; //returns user's profile information
    public static final String SEARCH_TWEETS = "/search"; //searches tweets
    public static final String GET_TIMELINE = USER_ENDPOINT + "/timeline"; //returns user's timeline
    public static final String GET_USER = USER_ENDPOINT + "/show"; //shows someone's profile
    public static final String BLOCK = USER_ENDPOINT + "/block"; //blocks a user
    public static final String UNBLOCK = USER_ENDPOINT + "/unblock"; //unblocks a user
    public static final String SET_PROFILE = USER_ENDPOINT + "/set"; //updates user's profile
    public static final String SET_BIO = USER_ENDPOINT + "/set-bio"; //updates user's bio
    public static final String SET_HEADER = USER_ENDPOINT + "/set-header"; //updates user's header
    public static final String SET_USERNAME = USER_ENDPOINT + "/set-username"; //updates user's username

    public static final String CHECK_EXISTENCE_ENDPOINT = "/check-existence";
    public static final String CHECK_USERNAME = CHECK_EXISTENCE_ENDPOINT + "/username"; //checks for duplicate email
    public static final String CHECK_EMAIL = CHECK_EXISTENCE_ENDPOINT + "/email"; //checks for duplicate username
}

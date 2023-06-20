package com.twitter.client.Controllers;

import com.twitter.common.Exceptions.HandledException;
import com.twitter.common.Models.User;

import java.util.ArrayList;
import java.util.Date;


public class ClientUnitTest {
    private static final UserActionsManager manager = UserActionsManager.getInstance();
    public static void main(String[] args) throws HandledException {

        User target = userGen(6);

        target = manager.signIn(target.getUsername(),target.getPasswordHash());
        System.out.println(target);
        System.out.println("login successful");

//        Tweet originalTweet = new Tweet(88, "2023-06-06 09:30:00", "Just had the best coffee ever ️", 10, 5, 2);
//        Quote quote1 = new Quote(89, "2023-06-06 09:35:00", "I agree! ", 3, 1, 1, originalTweet);
//        Tweet originalTweet2 = new Tweet(88, "2023-06-05 15:20:00", "Going on a hike today ️", 20, 10, 4);
//        Quote quote2 = new Quote(89, "2023-06-05 16:00:00", "Have fun! Take lots of pictures ", 5, 2, 1, originalTweet2);

    }

    private static void unfollowTest(int followerId, int followedId) throws HandledException {
        if(manager.unfollow(followerId,followedId))
            System.out.println(followerId + "successfully unfollowed" + followedId);
        else
            System.out.println("nashod");
    }

    private static User userGen(int i) {
        return new User(
                "User "+i,
                "user"+i,
                "passwordHash"+i,
                "user"+i+"@example.com",
                new Date(new Date().getTime() - 20*365*24*3600000L));
    }

}

/*
    INSERT INTO tweets (tweetId, senderId, Tweettext, attachment_1, attachment_2, attachment_3, attachment_4, favCount, retweetCount, mentionCount, sentAt, parentTweet, tweetType) VALUES
    (1, 80, 'Just had the best coffee ever!', null, null, null, null, 10, 5, 0, '2023-06-05 01:00:00', null, 'Tweet'),
    (2, 81, 'Excited to start my new job tomorrow!', null, null, null, null, 20, 15, 0, '2023-06-04 09:30:00', null, 'Tweet'),
    (3, 81, 'Can''t wait for the weekend!', null, null, null, null, 12, 8, 0, '2023-06-03 18:00:00', null, 'Tweet'),
    (4, 82, 'What a beautiful day!', null, null, null, null, 30, 25, 0, '2023-06-02 12:00:00', null, 'Tweet'),
    (5, 82, 'Just finished a great workout!', null, null, null, null, 15, 10, 0, '2023-06-01 07:45:00', null, 'Tweet'),
    (6, 85, '@alice Have you tried that new restaurant yet?', null, null, null, null, 5, 2, 1, '2023-05-31 15:20:00', null, 'Mention'),
    (7, 89, 'RT @bob: Check out this amazing article!', null, null, null, null, 50, 30, 0, '2023-05-30 10:00:00', null, 'Retweet'),
    (8, 81, 'Just saw the latest movie release. It was awesome!', null, null, null, null, 18, 12, 0, '2023-05-29 20:15:00', null, 'Tweet'),
    (9, 80, 'This is a quote from a famous person.', null, null, null, null, 8, 4, 0, '2023-05-28 14:30:00', null, 'Quote'),
    (10, 87, 'Happy birthday to my best friend!', null, null, null, null, 22, 18, 0, '2023-05-27 11:00:00', null, 'Tweet');
*/

/*INSERT INTO tweets (tweetId, senderId, Tweettext, attachment_1, attachment_2, attachment_3, attachment_4, favCount, retweetCount, mentionCount, sentAt, parentTweet, tweetType)
VALUES
    (14, 83, 'Just saw an amazing movie!', 'https://example.com/img1.jpg', NULL, NULL, NULL, 10, 5, 2, '2023-06-09 12:30:00', 1, 'Quote'),
    (15, 86, 'Congratulations to my friend on winning the marathon!', NULL, NULL, NULL, NULL, 20, 10, 3, '2023-06-10 15:45:00', 2, 'Quote'),
    (16, 88, 'Had a great time at the beach today', 'https://example.com/img1.jpg', 'https://example.com/img2.jpg', NULL, NULL, 15, 7, 1, '2023-06-03 11:00:00', 10, 'Quote'),
    (17, 81, '@user1 Thanks for the invitation!', NULL, NULL, NULL, NULL, 5, 0, 1, '2023-06-11 09:00:00', 4, 'Mention'),
    (18, 87, '@user2 Did you see this?', NULL, NULL, NULL, NULL, 8, 2, 2, '2023-06-12 14:00:00', 3, 'Mention'),
    (19, 89, '@user3 What do you think about this?', 'https://example.com/img1.jpg', NULL, NULL, NULL, 3, 1, 5, '2023-06-13 16:30:00', 6, 'Mention');
*/


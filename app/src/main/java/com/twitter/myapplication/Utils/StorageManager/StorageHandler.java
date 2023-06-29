package com.twitter.myapplication.Utils.StorageManager;

import android.content.Context;
import android.net.Uri;

import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.common.Models.Timeline;
import com.twitter.common.Models.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class StorageHandler {
    private final static String USER_SESSION_FILE_NAME = "current_user";
    private final static String USER_PROFILE_PIC = USER_SESSION_FILE_NAME + "_profile_picture";
    private final static String CURRENT_TIMELINE_FILE_NAME = "current_timeline";
    private StorageHandler(){}

    //TODO: move and fix writeObjectToFile and readObjectFromFile
    public static void saveCurrentUserData(Context context, User currentUser) {
        Image profilePic = currentUser.getProfilePic();
        if(profilePic != null && profilePic.getFileBytes().length != 0) {
            StorageAccessor.saveToFiles(context, USER_PROFILE_PIC, profilePic);
        }
        StorageAccessor.saveObjectToCache(context, USER_SESSION_FILE_NAME, currentUser);
    }

    public static User getCurrentUserData(Context context) {
        return (User) StorageAccessor.readObjectFromCache(context, USER_SESSION_FILE_NAME);
    }

    public static List<Uri> saveTweetAttachments(Context context, Tweet tweet) {
        int i=0;
        List<Uri> attachmentUris = new ArrayList<>();
        for (Visual attachment: tweet.getAttachments()) {
            attachmentUris.add(
                    StorageAccessor.saveToFiles(
                            context,
                            setTweetAttachmentName(i++, tweet),
                            attachment)
            );
        }
        return attachmentUris;
    }

    public static void saveTimeline(Context context, Timeline timeline) {
        StorageAccessor.saveObjectToCache(context, CURRENT_TIMELINE_FILE_NAME, timeline);
    }

    public static Timeline loadTimeline(Context context) {
        return (Timeline) StorageAccessor.readObjectFromCache(context, CURRENT_TIMELINE_FILE_NAME);
    }

    private static String setTweetAttachmentName(int position, Tweet tweet) {
        return tweet.getTweetId() + "_" + position;
    }

    public static String getExtensionFromUri(Uri uri) {
        return uri.toString().substring(uri.toString().lastIndexOf(".") + 1);
    }
}

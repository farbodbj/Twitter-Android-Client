package com.twitter.common.Models.Messages.Textuals;



import com.twitter.common.Exceptions.AttachmentError;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Messages.Visuals.Video;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.common.Models.User;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class Tweet extends Textual {
    public final static int MAX_TWEET_LENGTH = 280;
    public final static int MAX_IMG_HEIGHT = 900;
    public final static int MAX_IMG_WIDTH = 1600;
    public final static int MAX_ATTACHMENT_COUNT = 4;
    public final static double ASPECT_RATIO = (double) MAX_IMG_WIDTH / MAX_IMG_HEIGHT;
    private long tweetId;
    private String text;
    private List<Visual> attachments = new LinkedList<>();
    private int favCount;
    private int retweetCount;
    private int mentionCount;

    public Tweet() {
    }

    public Tweet(int senderId, String sentAt) {
        super(senderId, sentAt);
    }

    public Tweet(long tweetId, int senderId, String sentAt) {
        super(senderId, sentAt);
        this.tweetId = tweetId;
    }

    public Tweet(int senderId, String sentAt, String text, int favCount, int retweetCount, int mentionCount) {
        super(senderId, sentAt);
        this.text = text;
        this.favCount = favCount;
        this.retweetCount = retweetCount;
        this.mentionCount = mentionCount;
    }

    public long getTweetId() {return tweetId;}

    public String getText() {
        return text;
    }

    public List<Visual> getAttachments() {
        return attachments;
    }

    public int getFavCount() {
        return favCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getMentionCount() {
        return mentionCount;
    }

    public void setAttachments(List<Visual> attachments) {
        this.attachments = attachments;
    }

    public void addImageAttachment(String path) throws AttachmentError {
        try {
            File file = new File(path);
            addImageAttachment(new Image(file));
        } catch (IOException e) {
            throw new AttachmentError();
        }
    }

    public void addImageAttachment(Image image) throws AttachmentError {
        if(attachments.size() < MAX_ATTACHMENT_COUNT)
            attachments.add(image);
        else
            throw new AttachmentError("Too many attachments");
    }

    public void addVideoAttachment(String path) throws AttachmentError {
        try {
            File file = new File(path);
            addVideoAttachment(new Video(file));
        } catch (IOException e) {
            throw new AttachmentError();
        }
    }

    public void addVideoAttachment(Video video) throws AttachmentError{
        if(attachments.size() < MAX_ATTACHMENT_COUNT)
            attachments.add(video);
        else
            throw new AttachmentError("Too many attachments");
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setMentionCount(int mentionCount) {
        this.mentionCount = mentionCount;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "tweetId=" + tweetId +
                ", text='" + text + '\'' +
                ", attachments=" + attachments +
                ", favCount=" + favCount +
                ", retweetCount=" + retweetCount +
                ", mentionCount=" + mentionCount +
                '}';
    }
}

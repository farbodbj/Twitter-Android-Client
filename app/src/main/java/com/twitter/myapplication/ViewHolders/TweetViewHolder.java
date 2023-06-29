package com.twitter.myapplication.ViewHolders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Retweet;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.User;
import com.twitter.myapplication.Adapters.AttachmentAdapter;
import com.twitter.myapplication.R;
import com.twitter.myapplication.Utils.StorageManager.StorageHandler;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    public interface OnClickListener {
        void onMentionButtonClicked(Tweet parentTweet);
        void onFavButtonClicked();
        void onRetweetButtonClicked();
        void onQuoteButtonClicked(Tweet parentTweet);
    }

    OnClickListener onClickListener;

    private Tweet tweet;
    ShapeableImageView profilePicture;
    MaterialTextView authorDisplayName;
    MaterialTextView authorUserName;

    MaterialTextView tweetText;
    RecyclerView tweetAttachmentsView;

    MaterialTextView mentionCount;
    MaterialTextView retweetCount;
    MaterialTextView favCount;

    ImageButton mentionButton;
    ImageButton retweetButton;
    ImageButton favButton;
    ImageButton quoteButton;

    public TweetViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
        super(itemView);
        this.onClickListener = onClickListener;
        initializeUiComponents();
    }

    private void initializeUiComponents() {
        profilePicture = itemView.findViewById(R.id.profile_picture);
        authorDisplayName = itemView.findViewById(R.id.tweet_author_display_name);
        authorUserName = itemView.findViewById(R.id.tweet_author_username);

        tweetText = itemView.findViewById(R.id.tweet_text);
        tweetAttachmentsView = itemView.findViewById(R.id.tweet_attachments);

        mentionCount = itemView.findViewById(R.id.mention_count);
        retweetCount = itemView.findViewById(R.id.retweet_count);
        favCount = itemView.findViewById(R.id.fav_count);

        mentionButton = itemView.findViewById(R.id.mention_button);
        retweetButton = itemView.findViewById(R.id.retweet_button);
        favButton = itemView.findViewById(R.id.fav_button);
        quoteButton = itemView.findViewById(R.id.quote_button);


        setMentionButton();
        setFavButton();
        setRetweetButton();
        setQuoteButton();


    }

    public void bind(Tweet tweet) {
        this.tweet = tweet;

        //TODO: set image for profile picture
        authorDisplayName.setText(tweet.getSender().getDisplayName());
        authorUserName.setText(tweet.getSender().getUsername());
        tweetText.setText(tweet.getText());
        mentionCount.setText(String.valueOf(tweet.getMentionCount()));
        retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        favCount.setText(String.valueOf(tweet.getFavCount()));
        setTweetAttachmentsView();
    }

    private void setMentionButton(){
        mentionButton.setOnClickListener(v -> onClickListener.onMentionButtonClicked(tweet));
    }

    private void setRetweetButton() {
        retweetButton.setOnClickListener(v ->
                UserActionsManager.getInstance().retweet(
                        createRetweet(),
                        result -> {
                            if (result) {
                                retweetButton.setColorFilter(ContextCompat.getColor(v.getContext(), R.color.success_green));
                            }
                        },
                        error -> {
                            //Error handling logic
                        }
                ));
    }

    private void setFavButton() {
        favButton.setOnClickListener(v -> {
            // Toggle the selected state of the button
            favButton.setSelected(!favButton.isSelected());

            if (favButton.isSelected()) {
                // Perform the like action
                UserActionsManager.getInstance().like(
                        Session.getInstance().getSessionUser().getUserId(),
                        tweet.getTweetId(),
                        result -> {
                            if (result) {
                                favButton.setImageResource(R.drawable.like_selected);
                            }
                        },
                        error -> {
                            System.out.println("hi");
                            //Error handling logic
                        }
                );
            } else {
                // Perform the unlike action
                UserActionsManager.getInstance().unlike(
                        Session.getInstance().getSessionUser().getUserId(),
                        tweet.getTweetId(),
                        result -> {
                            if (result) {
                                favButton.setImageResource(R.drawable.like);
                            }
                        },
                        error -> {
                            System.out.println("hi");
                            //Error handling logic
                        }
                );
            }
        });
    }


    private void setQuoteButton() {
        quoteButton.setOnClickListener(v->onClickListener.onQuoteButtonClicked(tweet));
    }


    private Retweet createRetweet() {
        Retweet retweet = new Retweet();
        retweet.setRetweeted(tweet);
        User retweeter = new User();
        retweeter.setUserId(Session.getInstance().getSessionUser().getUserId());
        retweet.setSender(retweeter);

        return retweet;
    }

    private void setTweetAttachmentsView() {
        tweetAttachmentsView.setVisibility(View.VISIBLE);
        AttachmentAdapter attachmentAdapter = new AttachmentAdapter(
                StorageHandler.saveTweetAttachments(
                        itemView.getContext(),
                        tweet
                ));
        tweetAttachmentsView.setLayoutManager(new GridLayoutManager(tweetAttachmentsView.getContext(), 2));
        tweetAttachmentsView.setAdapter(attachmentAdapter);
        attachmentAdapter.notifyDataSetChanged();
    }

}

package com.twitter.myapplication.ViewHolders;

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

public class TweetViewHolder extends RecyclerView.ViewHolder {
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

    public TweetViewHolder(@NonNull View itemView) {
        super(itemView);
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


        setCommentButton();
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

    private void setCommentButton(){

    }

    private void setRetweetButton() {
        retweetButton.setOnClickListener(v ->
                UserActionsManager.getInstance().retweet(
                        createRetweet(),
                            result -> {
                                if (result) {
                                    retweetButton.setColorFilter(
                                            ContextCompat.getColor(
                                                    v.getContext(), R.color.success_green
                                            )
                                    );
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
                                favButton.setBackgroundColor(
                                        ContextCompat.getColor(v.getContext(), R.color.failure_red)
                                );
                            }
                        },
                        error -> {}
                );
            } else {
                // Perform the unlike action
                UserActionsManager.getInstance().unlike(
                        Session.getInstance().getSessionUser().getUserId(),
                        tweet.getTweetId(),
                        result -> {
                            if (result) {
                                favButton.setBackgroundColor(
                                        ContextCompat.getColor(v.getContext(), R.color.transparent)
                                );
                            }
                        },
                        error -> {}
                );
            }
        });
    }


    private void setQuoteButton() {

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
        tweetAttachmentsView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
        tweetAttachmentsView.setAdapter(new AttachmentAdapter(tweet.getAttachments()));
    }

}

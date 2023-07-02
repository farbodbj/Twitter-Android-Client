package com.twitter.myapplication.ViewHolders.TweetViewHolders;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Retweet;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.User;
import com.twitter.myapplication.Adapters.AttachmentAdapter;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardViewHolder;
import com.twitter.myapplication.Utils.StorageManager.StorageHandler;

public class TweetViewHolder extends RecyclerView.ViewHolder implements StandardViewHolder<Tweet> {
    public interface TweetItemEventListener {
        void onMentionButtonClicked(Tweet parentTweet);
        void onQuoteButtonClicked(Tweet parentTweet);
        void onUserDisplayNameClicked(User user);
    }

    TweetItemEventListener tweetItemEventListener;

    private Tweet tweet;
    private ShapeableImageView profilePicture;
    private MaterialTextView authorDisplayName;
    private MaterialTextView authorUserName;

    private MaterialTextView tweetText;
    private RecyclerView tweetAttachmentsView;

    private MaterialTextView mentionCount;
    private MaterialTextView retweetCount;
    private MaterialTextView favCount;

    private ImageButton mentionButton;
    private ImageButton retweetButton;
    private ImageButton favButton;
    private ImageButton quoteButton;

    public TweetViewHolder(@NonNull View itemView, TweetItemEventListener tweetItemEventListener) {
        super(itemView);
        this.tweetItemEventListener = tweetItemEventListener;
    }

    @Override
    public void initializeUiComponents() {
        profilePicture = itemView.findViewById(R.id.profile_picture_choice);
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


        setAuthorDisplayName();
        setMentionButton();
        setFavButton();
        setRetweetButton();
        setQuoteButton();
    }

    @Override
    public void bind(Tweet tweet) {
        this.tweet = tweet;

        //TODO: set image for profile picture
        authorDisplayName.setText(tweet.getSender().getDisplayName());
        authorUserName.setText(itemView.getResources().getString(R.string.username_field, tweet.getSender().getUsername()));
        tweetText.setText(tweet.getText());
        mentionCount.setText(String.valueOf(tweet.getMentionCount()));
        retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        favCount.setText(String.valueOf(tweet.getFavCount()));
        if(tweet.getAttachments().size() != 0)
            setTweetAttachmentsView();
        setTweetProfilePicture();
    }

    private void setMentionButton(){
        mentionButton.setOnClickListener(v -> tweetItemEventListener.onMentionButtonClicked(tweet));
    }

    private void setAuthorDisplayName() {
        authorDisplayName.setOnClickListener(v -> tweetItemEventListener.onUserDisplayNameClicked(tweet.getSender()));
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
        quoteButton.setOnClickListener(v-> tweetItemEventListener.onQuoteButtonClicked(tweet));
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
        tweetAttachmentsView.setAdapter(attachmentAdapter);
        attachmentAdapter.notifyDataSetChanged();
    }

    private void setTweetProfilePicture() {
        Uri profileUri = StorageHandler.saveTweetProfilePicture(itemView.getContext(), tweet);
        if (profileUri != null){
            Glide
                    .with(profilePicture.getContext())
                    .load(profileUri)
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(profilePicture);
        }

    }

}

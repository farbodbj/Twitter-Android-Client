package com.twitter.myapplication.ViewHolders.TweetViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.twitter.common.Models.Messages.Textuals.Retweet;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardViewHolder;

public class RetweetViewHolder extends RecyclerView.ViewHolder implements StandardViewHolder<Retweet> {
    private TweetViewHolder tweetViewHolder;
    private MaterialTextView retweetedBy;
    public RetweetViewHolder(@NonNull View itemView, TweetViewHolder.TweetItemEventListener tweetItemEventListener) {
        super(itemView);
        tweetViewHolder = new TweetViewHolder(itemView, tweetItemEventListener);
    }

    @Override
    public void initializeUiComponents() {
        tweetViewHolder.initializeUiComponents();
        retweetedBy = itemView.findViewById(R.id.retweeted_by);
    }


    @Override
    public void bind(Retweet retweet) {
        tweetViewHolder.bind(retweet.getRetweeted());
        retweetedBy.setText(itemView.getContext().getString(R.string.retweeted_by, retweet.getSender().getDisplayName()));
    }
}

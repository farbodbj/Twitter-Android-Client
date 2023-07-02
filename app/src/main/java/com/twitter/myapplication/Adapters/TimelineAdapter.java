package com.twitter.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.common.Models.Messages.Textuals.Quote;
import com.twitter.common.Models.Messages.Textuals.Retweet;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Timeline;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardViewHolder;
import com.twitter.myapplication.ViewHolders.TweetViewHolders.MentionViewHolder;
import com.twitter.myapplication.ViewHolders.TweetViewHolders.QuoteViewHolder;
import com.twitter.myapplication.ViewHolders.TweetViewHolders.RetweetViewHolder;
import com.twitter.myapplication.ViewHolders.TweetViewHolders.TweetViewHolder;

import java.util.List;
import java.util.zip.Inflater;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TWEET = 0;
    private static final int MENTION = 1;
    private static final int RETWEET = 2;
    private static final int QUOTE = 3;
    TweetViewHolder.TweetItemEventListener onItemClickedListener;
    private Timeline timeline;

    public TimelineAdapter(Timeline timeline, TweetViewHolder.TweetItemEventListener onItemClickedListener) {
        this.timeline = timeline;
        this.onItemClickedListener = onItemClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        Tweet current = timeline.get(position);
        if(current instanceof Mention)
            return MENTION;
        else if(current instanceof Retweet)
            return RETWEET;
        else if(current instanceof Quote)
            return QUOTE;
        else
            return TWEET;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case TWEET -> {
                view = inflater.inflate(R.layout.tweet_item_layout, parent, false);
                TweetViewHolder tweetViewHolder = new TweetViewHolder(view, onItemClickedListener);
                tweetViewHolder.initializeUiComponents();
                return tweetViewHolder;
            }
            case MENTION -> {
                view = inflater.inflate(R.layout.mention_item_layout, parent, false);
                MentionViewHolder mentionViewHolder = new MentionViewHolder(view, onItemClickedListener);
                mentionViewHolder.initializeUiComponents();
                return mentionViewHolder;
            }
            case RETWEET -> {
                view = inflater.inflate(R.layout.retweet_item_layout, parent, false);
                RetweetViewHolder retweetViewHolder = new RetweetViewHolder(view, onItemClickedListener);
                retweetViewHolder.initializeUiComponents();
                return retweetViewHolder;
            }
            case QUOTE -> {
                view = inflater.inflate(R.layout.quote_item_layout, parent, false);
                QuoteViewHolder quoteViewHolder = new QuoteViewHolder(view, onItemClickedListener);
                quoteViewHolder.initializeUiComponents();
                return quoteViewHolder;
            }
            default -> throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tweet current = timeline.get(position);
        if(holder instanceof StandardViewHolder)
            ((StandardViewHolder) holder).bind(current);
    }

    @Override
    public int getItemCount() {
        return timeline.size();
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public boolean addToTimeline(List<Tweet> tweets) {
        return timeline.addAll(tweets);
    }
}


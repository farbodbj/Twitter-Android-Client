package com.twitter.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Timeline;
import com.twitter.myapplication.R;
import com.twitter.myapplication.ViewHolders.TweetViewHolder;

import java.util.Collection;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TweetViewHolder> {

    private Timeline timeline;

    public TimelineAdapter(Timeline timeline) {
        this.timeline = timeline;
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_item_layout, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        Tweet current = timeline.get(position);


        switch (current.getClass().getSimpleName()) {
            case "Tweet":
                holder.bind(current);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return timeline.size();
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public void addToTimeline(List<Tweet> tweets) {
        timeline.addAll(tweets);
    }
}


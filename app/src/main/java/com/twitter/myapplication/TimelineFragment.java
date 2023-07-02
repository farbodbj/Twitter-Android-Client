package com.twitter.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Timeline;
import com.twitter.common.Models.User;
import com.twitter.myapplication.Adapters.TimelineAdapter;
import com.twitter.myapplication.StandardFormats.StandardFragmentFormat;
import com.twitter.myapplication.ViewHolders.TweetViewHolders.TweetViewHolder;


public class TimelineFragment extends Fragment implements StandardFragmentFormat, TweetViewHolder.TweetItemEventListener {

    public interface TimelineFragmentEventListener {
        void onTimelineRefreshRequest(TimelineAdapter timelineAdapter, SwipeRefreshLayout swipeRefreshLayout);
        void onMentionButtonClicked(Tweet parentTweet);
        void onQuoteButtonClicked(Tweet parentTweet);
        void onUserDisplayNameClicked(User user);
    }
    private TimelineAdapter timelineAdapter;

    private TimelineFragmentEventListener timelineFragmentEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        initializeUIComponents(view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            timelineFragmentEventListener = (TimelineFragmentEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnRefreshTimelineListener");
        }
    }

    @Override
    public void initializeUIComponents(View view) {
        Timeline init = new Timeline();
        this.timelineAdapter = new TimelineAdapter(init, this);
        setTimelineRecyclerView(timelineAdapter, view);
        setTimeLineSwipeRefresh(timelineAdapter, view);
    }

    private void setTimelineRecyclerView(TimelineAdapter timelineAdapter, View view) {
        RecyclerView timelineRecyclerView = view.findViewById(R.id.timeline_recycler_view);
        timelineRecyclerView.setAdapter(timelineAdapter);
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setTimeLineSwipeRefresh(TimelineAdapter timelineAdapter, View view) {
        SwipeRefreshLayout timelineSwipeRefresh = view.findViewById(R.id.timeline_swipe_refresh);
        timelineSwipeRefresh.setOnRefreshListener(() -> timelineFragmentEventListener.onTimelineRefreshRequest(timelineAdapter, timelineSwipeRefresh));
    }

    @Override
    public void onMentionButtonClicked(Tweet parentTweet) {
        timelineFragmentEventListener.onMentionButtonClicked(parentTweet);
    }

    @Override
    public void onQuoteButtonClicked(Tweet parentTweet) {
        timelineFragmentEventListener.onQuoteButtonClicked(parentTweet);
    }

    @Override
    public void onUserDisplayNameClicked(User user) {
        timelineFragmentEventListener.onUserDisplayNameClicked(user);
    }
}

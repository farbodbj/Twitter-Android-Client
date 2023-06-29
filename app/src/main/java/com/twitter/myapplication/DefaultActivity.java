package com.twitter.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Timeline;
import com.twitter.myapplication.Adapters.TimelineAdapter;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;
import com.twitter.myapplication.ViewHolders.TweetViewHolder;
import com.twitter.myapplication.WritingActivities.WriteMentionActivity;
import com.twitter.myapplication.WritingActivities.WriteTweetActivity;

public class DefaultActivity extends AppCompatActivity implements StandardActivityFormat, TweetViewHolder.OnClickListener {
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        initializeUIComponents();
    }

    @Override
    public void initializeUIComponents() {
        RecyclerView timelineRecyclerView = findViewById(R.id.timeline_recycler_view);
        SwipeRefreshLayout timelineSwipeRefresh = findViewById(R.id.timeline_swipe_refresh);
        FloatingActionButton tweetButton = findViewById(R.id.floating_tweet_button);

        TabItem homeItem = findViewById(R.id.home_item);
        TabItem profileItem = findViewById(R.id.profile_item);
        TabItem directsItem = findViewById(R.id.directs_item);

        setTweetButton(tweetButton);
        Timeline init = new Timeline();
        TimelineAdapter timelineAdapter = new TimelineAdapter(init, this);
        setTimelineRecyclerView(timelineRecyclerView, timelineAdapter);
        setTimeLineSwipeRefresh(timelineSwipeRefresh, timelineAdapter);

    }

    //a parent class is set for input type for ease of change
    private void setTweetButton(ImageButton tweetButton) {
        tweetButton.setOnClickListener(view -> AndroidUtils.gotoActivity(this, WriteTweetActivity.class, null));
    }

    private void setTimelineRecyclerView(RecyclerView timelineRecyclerView, TimelineAdapter timelineAdapter) {
        timelineRecyclerView.setAdapter(timelineAdapter);
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadTimeline(Timeline timeline, TimelineAdapter timelineAdapter) {
        timelineAdapter.setTimeline(timeline);
        timelineAdapter.notifyDataSetChanged();
    }


    private void setTimeLineSwipeRefresh(SwipeRefreshLayout timelineSwipeRefresh, TimelineAdapter timelineAdapter) {
        timelineSwipeRefresh.setOnRefreshListener(() -> {
            UserActionsManager.getInstance().getTimeline(
                    Session.getInstance().getSessionUser().getUserId(),
                    100,
                    result -> {
                        runOnUiThread(()->{
                            loadTimeline(result, timelineAdapter);
                            timelineSwipeRefresh.setRefreshing(false);
                        });

                    },
                    error -> {
                        //Error handling logic
                    });
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        AndroidUtils.showShortMessage(this, "Please click BACK again to exit", -1);

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    public void onMentionButtonClicked(Tweet parentTweet) {
        Bundle bundle = new Bundle();
        bundle.putLong("parent_tweet_id", parentTweet.getTweetId());
        bundle.putString("parent_username", parentTweet.getSender().getUsername());
        AndroidUtils.gotoActivity(this, WriteMentionActivity.class,  bundle);
    }

    @Override
    public void onFavButtonClicked() {

    }

    @Override
    public void onRetweetButtonClicked() {

    }

    @Override
    public void onQuoteButtonClicked(Tweet parentTweet) {

    }
}
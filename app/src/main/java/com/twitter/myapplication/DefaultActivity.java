package com.twitter.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Exceptions.DuplicateRecordException;
import com.twitter.common.Exceptions.NotFoundException;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Timeline;
import com.twitter.common.Models.User;
import com.twitter.myapplication.Adapters.TimelineAdapter;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;
import com.twitter.myapplication.WritingActivities.WriteMentionActivity;
import com.twitter.myapplication.WritingActivities.WriteQuoteActivity;
import com.twitter.myapplication.WritingActivities.WriteTweetActivity;

import java.util.concurrent.CountDownLatch;

public class DefaultActivity extends AppCompatActivity implements StandardActivityFormat, TimelineFragment.TimelineFragmentEventListener, UserProfileFragment.UserProfileEventListener {
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        initializeUIComponents();

        TimelineFragment timelineFragment = new TimelineFragment();

        AndroidUtils.gotoFragment(getSupportFragmentManager(), timelineFragment, R.id.default_activity_fragment_container, null);
    }

    @Override
    public void initializeUIComponents() {
        FloatingActionButton tweetButton = findViewById(R.id.floating_tweet_button);
        TabItem homeItem = findViewById(R.id.home_item);
        TabItem profileItem = findViewById(R.id.profile_item);
        TabItem directsItem = findViewById(R.id.directs_item);

        setTweetButton(tweetButton);
    }

    private void setTweetButton(FloatingActionButton tweetButton) {
        tweetButton.setOnClickListener(
                view -> AndroidUtils.gotoActivity(this, WriteTweetActivity.class, null)
        );
    }

    private void loadTimeline(Timeline timeline, TimelineAdapter timelineAdapter) {
        timelineAdapter.setTimeline(timeline);
        timelineAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTimelineRefreshRequest(TimelineAdapter timelineAdapter, SwipeRefreshLayout swipeRefreshLayout) {
        UserActionsManager.getInstance().getTimeline(
                Session.getInstance().getSessionUser().getUserId(),
                100,
                result -> {
                    runOnUiThread(() -> {
                        loadTimeline(result, timelineAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                },
                error -> {
                    //Error handling logic
                }
        );
    }

    @Override
    public void onMentionButtonClicked(Tweet parentTweet) {
        Bundle bundle = new Bundle();
        bundle.putLong("parent_tweet_id", parentTweet.getTweetId());
        bundle.putString("parent_username", parentTweet.getSender().getUsername());
        AndroidUtils.gotoActivity(this, WriteMentionActivity.class,  bundle);
    }


    @Override
    public void onQuoteButtonClicked(Tweet parentTweet) {
        Bundle bundle = new Bundle();
        bundle.putLong("parent_tweet_id", parentTweet.getTweetId());
        bundle.putString("parent_username", parentTweet.getSender().getUsername());
        AndroidUtils.gotoActivity(this, WriteQuoteActivity.class, bundle);
    }

    @Override
    public void onUserDisplayNameClicked(User user) {
        Bundle bundle = new Bundle();
        CountDownLatch latch = new CountDownLatch(3);

        UserActionsManager.getInstance().getUser(
                user.getUserId(),
                result -> {
                    bundle.putSerializable("user", result);
                    latch.countDown();
                },
                error -> {
                    //Error handling logic
                }
        );

        UserActionsManager.getInstance().getFollowingsCount(
                user.getUserId(),
                result -> {
                    bundle.putInt("followings_count", result);
                    latch.countDown();
                },
                error -> {
                    //Error handling logic
                }
        );

        UserActionsManager.getInstance().getFollowersCount(
                user.getUserId(),
                result -> {
                    bundle.putInt("followers_count", result);
                    latch.countDown();
                },
                error -> {
                    //Error handling logic
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            //Error handling logic
        }
        AndroidUtils.gotoFragment(getSupportFragmentManager(), new UserProfileFragment(false), R.id.default_activity_fragment_container, bundle);
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
    public void onFollowButtonClicked(UserProfileFragment userProfileFragment, User target) {
        UserActionsManager.getInstance().follow(
                Session.getInstance().getSessionUser().getUserId(),
                target.getUserId(),
                result -> {
                    if(result)
                        userProfileFragment.showActionResultToast(getString(R.string.follow_success));
                },
                error -> {
                    userProfileFragment.showActionResultToast(getString(R.string.already_followed));
                    //Error handling logic
                }
        );
    }

    @Override
    public void onUnfollowButtonClicked(UserProfileFragment userProfileFragment, User target) {
        UserActionsManager.getInstance().unfollow(
                Session.getInstance().getSessionUser().getUserId(),
                target.getUserId(),
                result -> {
                    if(result)
                        userProfileFragment.showActionResultToast(getString(R.string.unfollow_success));
                },
                error -> {
                    if(error instanceof NotFoundException) {
                        userProfileFragment.showActionResultToast(getString(R.string.not_followed));
                    }
                    //Error handling logic
                }
        );
    }

    @Override
    public void onBlockButtonClicked(UserProfileFragment userProfileFragment, User target) {
        UserActionsManager.getInstance().block(
                target.getUserId(),
                Session.getInstance().getSessionUser().getUserId(),
                result -> {
                    if(result)
                        userProfileFragment.showActionResultToast(getString(R.string.block_success));
                },
                error -> {
                    userProfileFragment.showActionResultToast(getString(R.string.already_blocked));
                    //Error handling logic
                }
        );
    }

    @Override
    public void onUnblockButtonClicked(UserProfileFragment userProfileFragment, User target) {
        UserActionsManager.getInstance().unblock(
                target.getUserId(),
                Session.getInstance().getSessionUser().getUserId(),
                result -> {
                    if(result)
                        userProfileFragment.showActionResultToast(getString(R.string.unblock_success));
                },
                error -> {
                    if(error instanceof NotFoundException) {
                        userProfileFragment.showActionResultToast(getString(R.string.not_blocked));
                    }
                    //Error handling logic
                }
        );
    }

    @Override
    public void onEditProfileButtonClicked() {

    }
}
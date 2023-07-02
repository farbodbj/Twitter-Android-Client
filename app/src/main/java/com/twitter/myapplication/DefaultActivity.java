package com.twitter.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Exceptions.HandledException;
import com.twitter.common.Exceptions.NotFoundException;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Timeline;
import com.twitter.common.Models.User;
import com.twitter.myapplication.Adapters.TimelineAdapter;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;
import com.twitter.myapplication.ViewHolders.UserConnectionsViewHolder;
import com.twitter.myapplication.WritingActivities.WriteMentionActivity;
import com.twitter.myapplication.WritingActivities.WriteQuoteActivity;
import com.twitter.myapplication.WritingActivities.WriteTweetActivity;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;


public class DefaultActivity extends AppCompatActivity implements
        StandardActivityFormat,
        TimelineFragment.TimelineFragmentEventListener,
        UserProfileFragment.UserProfileEventListener,
        EditProfileFragment.OnProfileChangeEventListener,
        UserSearchFragment.OnUserSearchFragmentEventListener,
        UserConnectionsViewHolder.OnUserConnectionsItemEventListener
{
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        initializeUIComponents();

        AndroidUtils.gotoFragment(getSupportFragmentManager(), new TimelineFragment(), R.id.default_activity_fragment_container, null);
    }

    @Override
    public void initializeUIComponents() {
        setTweetButton();
        setTabItems();
    }

    private void setTabItems() {
        TabLayout bottomNavigator = findViewById(R.id.tabs);

        bottomNavigator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0 -> {
                        TimelineFragment timelineFragment = new TimelineFragment();
                        AndroidUtils.gotoFragment(getSupportFragmentManager(), timelineFragment, R.id.default_activity_fragment_container, null);
                    }
                    case 1 -> {
                        AndroidUtils.gotoFragment(getSupportFragmentManager(), new UserSearchFragment(), R.id.default_activity_fragment_container, null);
                    }
                    case 2 -> {
                        UserProfileFragment userProfileFragment = new UserProfileFragment(true);
                        AndroidUtils.gotoFragment(
                                getSupportFragmentManager(),
                                userProfileFragment,
                                R.id.default_activity_fragment_container,
                                getUserProfileBundle(Session.getInstance().getSessionUser())
                        );
                    }
                    case 3 -> {
                        //TODO: directs tab
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setTweetButton() {
        FloatingActionButton tweetButton = findViewById(R.id.floating_tweet_button);
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
        AndroidUtils.gotoFragment(
                getSupportFragmentManager(),
                new UserProfileFragment(false),
                R.id.default_activity_fragment_container,
                getUserProfileBundle(user));
    }

    private Bundle getUserProfileBundle(User user) {
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
                });

        UserActionsManager.getInstance().getFollowingsCount(
                user.getUserId(),
                result -> {
                    bundle.putInt("followings_count", result);
                    latch.countDown();
                },
                error -> {
                    //Error handling logic
                });

        UserActionsManager.getInstance().getFollowersCount(
                user.getUserId(),
                result -> {
                    bundle.putInt("followers_count", result);
                    latch.countDown();
                },
                error -> {
                    //Error handling logic
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            //Error handling logic
        }

        return bundle;
    }



    @Override
    public void onSearchItemDisplayNameClicked(UserSearchFragment userSearchFragment, User user) {
        onUserDisplayNameClicked(user);
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
    public void onFollowingsCountClicked(UserProfileFragment userProfileFragment, User target) {
        UserActionsManager.getInstance().getFollowings(
                target.getUserId(),
                result -> {
                    runOnUiThread(()->{
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("users", (Serializable) result);
                        AndroidUtils.gotoFragment(getSupportFragmentManager(), new UserConnectionsFragment(), R.id.default_activity_fragment_container, bundle);
                    });
                },
                error -> {
                    //Error handling logic
                }
        );
    }

    @Override
    public void onFollowersCountClicked(UserProfileFragment userProfileFragment, User target) {
        UserActionsManager.getInstance().getFollowers(
                target.getUserId(),
                result -> {
                    runOnUiThread(()->{
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("users", (Serializable) result);
                        AndroidUtils.gotoFragment(getSupportFragmentManager(), new UserConnectionsFragment(), R.id.default_activity_fragment_container, bundle);
                    });
                },
                error -> {
                    //Error handling logic
                }
        );
    }

    @Override
    public void onEditProfileButtonClicked() {
        AndroidUtils.gotoFragment(getSupportFragmentManager(), new EditProfileFragment(), R.id.default_activity_fragment_container, null);
    }

    public void onProfileEditDone() {
        runOnUiThread(()->
                {
                    AndroidUtils.gotoFragment(getSupportFragmentManager(), new UserProfileFragment(true), R.id.default_activity_fragment_container, null);
                    AndroidUtils.showLongToastMessage(this, "profile edited successfully", 2000);
                });
    }



    //Following functions contain some code duplication which can probably be eliminated using Function4 interface,
    //but because the deadline for this project is in less than 24 hours I'm not doing it :)
    @Override
    public void onProfilePicChanged(File profileFile) {
        Image image = new Image(profileFile);

        UserActionsManager.getInstance().setNewProfilePic(
                Session.getInstance().getSessionUser().getUserId(),
                image,
                result -> {
                    if(result)
                        onProfileEditDone();
                },
                error -> {
                    //Error handling logic
                });
    }

    @Override
    public void onHeaderPicChanged(File headerFile) {
        Image header = new Image(headerFile);

        UserActionsManager.getInstance().setNewHeaderPic(
                Session.getInstance().getSessionUser().getUserId(),
                header,
                result -> {
                    if(result)
                        onProfileEditDone();
                },
                error -> {
                    //Error handling logic
                });
    }

    @Override
    public void onAccountNameChanged(String newAccountName) {
        UserActionsManager.getInstance().setNewAccountName(
                Session.getInstance().getSessionUser().getUserId(),
                newAccountName,
                result -> {
                    if(result)
                        onProfileEditDone();
                },
                error -> {
                    //Error handling logic
                });
    }

    @Override
    public void onBioChanged(String newBio) {
        UserActionsManager.getInstance().setNewBio(
                Session.getInstance().getSessionUser().getUserId(),
                newBio,
                result -> {
                    if(result)
                        onProfileEditDone();
                },
                error -> {
                    //Error handling logic
                });
    }

    @Override
    public void onLocationChanged(String newLocation) {
        UserActionsManager.getInstance().setNewLocation(
                Session.getInstance().getSessionUser().getUserId(),
                newLocation,
                result -> {
                    if(result)
                        onProfileEditDone();
                },
                error -> {
                    //Error handling logic
                });
    }

    @Override
    public void onSearchButtonClicked(UserSearchFragment userSearchFragment, String searchTerm) {
        if(searchTerm.isBlank()) return;
        UserActionsManager.getInstance().searchForUser(
                searchTerm,
                result -> {
                    if(result != null) {
                        userSearchFragment.showSearchResult(result);
                    }
                },
                error -> {
                    //Error handling logic
                });
    }
}
package com.twitter.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;

public class DefaultActivity extends AppCompatActivity implements StandardActivityFormat {
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        initializeUIComponents();
    }

    @Override
    public void initializeUIComponents() {
        FloatingActionButton tweetButton = findViewById(R.id.floating_tweet_button);

        TabItem homeItem = findViewById(R.id.home_item);
        TabItem profileItem = findViewById(R.id.profile_item);
        TabItem directsItem = findViewById(R.id.directs_item);

        setTweetButton(tweetButton);
    }

    //a parent class is set for input type for ease of change
    private void setTweetButton(ImageButton tweetButton) {
        tweetButton.setOnClickListener(view -> AndroidUtils.gotoActivity(this, WriteTweetActivity.class, null));
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
}
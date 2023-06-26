package com.twitter.myapplication.WritingActivities;

import static com.twitter.common.Utils.SafeCall.safe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.myapplication.DefaultActivity;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;

import java.time.LocalDateTime;

public class WriteTweetActivity extends BaseWritingActivity {
    private final static int SEND_TWEET_RESULT_TOAST_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tweet = new Tweet();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeUIComponents() {
        ImageButton  backButton = findViewById(R.id.back_button);
        ImageButton chooseAttachments = findViewById(R.id.select_attachments);
        Button tweetButton = findViewById(R.id.tweet_button);
        CircularProgressIndicator tweetCharIndicator = findViewById(R.id.tweet_characters);
        TextInputEditText etTweet = findViewById(R.id.etTweet);

        setBackButton(backButton);
        setProgressIndicator(tweetCharIndicator);
        setInputEditText(etTweet, tweetCharIndicator);
        setChooseAttachments(chooseAttachments);
        setSendButton(tweetButton);
    }


    @Override
    protected void onSendButtonClick() {
            tweet.setSender(Session.getInstance().getSessionUser());
            tweet.setSentAt(LocalDateTime.now());
            UserActionsManager.getInstance()
                .tweet(tweet,
                    result ->
                        runOnUiThread(
                            ()-> {
                                AndroidUtils.showLongToastMessage(
                                        WriteTweetActivity.this,
                                        (result) ? getString(R.string.tweet_successful) : (getString(R.string.tweet_failed)),
                                        SEND_TWEET_RESULT_TOAST_DURATION);
                                AndroidUtils.gotoActivity(WriteTweetActivity.this, DefaultActivity.class, null);
                            }),
                    error -> {
                        //Error handling logic
                    }
                );
    }
}
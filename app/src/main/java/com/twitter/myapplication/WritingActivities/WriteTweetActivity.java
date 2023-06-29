package com.twitter.myapplication.WritingActivities;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.myapplication.R;

import java.time.LocalDateTime;

public class WriteTweetActivity extends BaseWritingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        parentTweet = new Tweet();
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
        setInputEditText(etTweet, tweetCharIndicator, parentTweet);
        setChooseAttachments(chooseAttachments, parentTweet);
        setSendButton(tweetButton);
    }


    @Override
    protected void onSendButtonClick() {
            parentTweet.setSender(Session.getInstance().getSessionUser());
            parentTweet.setSentAt(LocalDateTime.now());
            UserActionsManager.getInstance()
                .tweet(parentTweet,
                    result -> {
                        runOnUiThread(()-> {
                            tweetSendResultCallback(result);
                        });
                    },
                error -> {
                        //Error handling logic
                    }
                );
    }

    //override onDestroy, onStop and onPause methods and add a log message to them
    @Override
    protected void onDestroy() {
        Log.d("Writing activity", "Writing activity on destroy called");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d("Writing activity", "Writing activity on stop called");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("Writing activity", "Writing activity on pause called");
        super.onPause();
    }
}
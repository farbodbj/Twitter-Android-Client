package com.twitter.myapplication.WritingActivities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.myapplication.DefaultActivity;
import com.twitter.myapplication.R;
import com.twitter.myapplication.Utils.AndroidUtils;

import java.time.LocalDateTime;

public class WriteMentionActivity extends BaseWritingActivity {
    private final Mention mention = new Mention();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        parentTweet = new Tweet();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setInputEditText(TextInputEditText etInput, CircularProgressIndicator progressIndicator, Tweet currentTweet) {
        parentTweet.setTweetId(getIntent().getExtras().getLong("parent_tweet_id"));
        etInput.setHintTextColor(ContextCompat.getColor(this, R.color.A700));
        super.setInputEditText(etInput, progressIndicator, mention);
    }

    @Override
    protected void setTextInputLayout(TextInputLayout textInputLayout) {
        textInputLayout.setHint(getString(R.string.mention_field_hint, getIntent().getExtras().getString("parent_username")));
    }

    @Override
    protected void onSendButtonClick() {
        mention.setMentionedTo(parentTweet);
        mention.setSender(Session.getInstance().getSessionUser());
        mention.setSentAt(LocalDateTime.now());
        UserActionsManager.getInstance().mention(
                mention,
                result -> {
                    runOnUiThread(()-> {
                        tweetSendResultCallback(result);
                    });
                },
                error -> {
                    //Error handling logic
                });
    }
}

package com.twitter.myapplication.WritingActivities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.common.Models.Messages.Textuals.Quote;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.myapplication.R;

import java.time.LocalDateTime;

public class WriteQuoteActivity extends BaseWritingActivity {
    private final Quote quote = new Quote();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        parentTweet = new Tweet();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setInputEditText(TextInputEditText etInput, CircularProgressIndicator progressIndicator, Tweet currentTweet) {
        parentTweet.setTweetId(getIntent().getExtras().getLong("parent_tweet_id"));
        etInput.setHintTextColor(ContextCompat.getColor(this, R.color.A700));
        super.setInputEditText(etInput, progressIndicator, quote);
    }


    @Override
    protected void setTextInputLayout(TextInputLayout textInputLayout) {
        textInputLayout.setHint(getString(R.string.quote_field_hint, getIntent().getExtras().getString("parent_username")));
    }


    @Override
    protected void onSendButtonClick() {
        quote.setQuoted(parentTweet);
        quote.setSender(Session.getInstance().getSessionUser());
        quote.setSentAt(LocalDateTime.now());
        UserActionsManager.getInstance().quote(
                quote,
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

package com.twitter.myapplication.WritingActivities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.myapplication.R;

public class WriteMentionActivity extends BaseWritingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tweet = new Mention();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setInputEditText(TextInputEditText etInput, CircularProgressIndicator progressIndicator) {
        String mentionTo = getIntent().getExtras().getString("mentionTo");

        etInput.setHint(getString(R.string.mention_field_hint) + mentionTo);
        etInput.setHintTextColor(ContextCompat.getColor(this, R.color.A700));

        super.setInputEditText(etInput, progressIndicator);
    }

    @Override
    protected void onSendButtonClick() {

    }
}

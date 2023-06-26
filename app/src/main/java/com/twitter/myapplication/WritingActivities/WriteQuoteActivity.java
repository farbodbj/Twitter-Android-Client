package com.twitter.myapplication.WritingActivities;

import android.os.Bundle;

import com.twitter.common.Models.Messages.Textuals.Mention;

public class WriteQuoteActivity extends BaseWritingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tweet = new Mention();
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onSendButtonClick() {

    }
}

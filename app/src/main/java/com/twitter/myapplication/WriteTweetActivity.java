package com.twitter.myapplication;

import static com.twitter.common.Utils.SafeCall.safe;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Messages.Visuals.Video;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;

import java.time.LocalDateTime;

public class WriteTweetActivity extends AppCompatActivity implements StandardActivityFormat {

    private Tweet tweet = new Tweet();

    private final static int SEND_TWEET_RESULT_TOAST_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tweet);

        initializeUIComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tweet = null;
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
        setTweetEditText(etTweet, tweetCharIndicator);
        setChooseAttachments(chooseAttachments);
        setTweetButton(tweetButton);
    }

    private void setBackButton(ImageButton backButton) {
        backButton.setOnClickListener(view -> AndroidUtils.gotoActivity(this, DefaultActivity.class, null));
    }

    private void setChooseAttachments(ImageButton chooseAttachments) {

        ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(Tweet.MAX_ATTACHMENT_COUNT), uris -> {
                    for (Uri uri : uris) {
                        processUri(uri);
                    }

                    if (!uris.isEmpty()) {
                        Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        chooseAttachments.setOnClickListener(view -> pickMultipleMedia.launch(new PickVisualMediaRequest.Builder().build()));
    }

    private void setTweetButton(Button tweetButton) {
        tweetButton.setOnClickListener(view -> {
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

                        }
                );
        });
    }

    private void setTweetEditText(TextInputEditText etTweet, CircularProgressIndicator tweetCharIndicator) {
        etTweet.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Tweet.MAX_TWEET_LENGTH) });

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                setIndicatorProgress(tweetCharIndicator, editable.length());
                tweet.setText(editable.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });
    }

    private void setProgressIndicator(CircularProgressIndicator tweetCharIndicator) {
        tweetCharIndicator.setMax(Tweet.MAX_TWEET_LENGTH);
    }

    private void setIndicatorProgress(CircularProgressIndicator tweetCharIndicator, int progress) {
        tweetCharIndicator.setProgress(progress);
    }


    private void processUri(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        String type = contentResolver.getType(uri);
        String extension = mime.getExtensionFromMimeType(type);

        Visual att = (type.startsWith("image")) ? new Image() : new Video();

        att.setFileBytes(AndroidUtils.getBytesFromUri(this, uri));
        att.setFileFormat(extension);

        if (att instanceof Image) {
            safe(() -> tweet.addImageAttachment((Image) att));
        } else {
            safe(() -> tweet.addVideoAttachment((Video) att));
        }
    }

}
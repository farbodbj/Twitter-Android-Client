package com.twitter.myapplication.WritingActivities;

import static com.twitter.common.Utils.SafeCall.safe;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Messages.Visuals.Video;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.myapplication.DefaultActivity;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;

public abstract class BaseWritingActivity extends AppCompatActivity implements StandardActivityFormat {
    protected Tweet tweet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_writing);


        initializeUIComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tweet = null;
    }

    public void initializeUIComponents() {
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton chooseAttachments = findViewById(R.id.select_attachments);
        Button sendButton = findViewById(R.id.tweet_button);
        CircularProgressIndicator progressIndicator = findViewById(R.id.tweet_characters);
        TextInputEditText etInput = findViewById(R.id.etTweet);

        setBackButton(backButton);
        setProgressIndicator(progressIndicator);
        setInputEditText(etInput, progressIndicator);
        setChooseAttachments(chooseAttachments);
        setSendButton(sendButton);
    }

    protected void setBackButton(ImageButton backButton) {
        backButton.setOnClickListener(view -> AndroidUtils.gotoActivity(this, DefaultActivity.class, null));
    }

    protected void setChooseAttachments(ImageButton chooseAttachments) {
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

    protected void setSendButton(Button sendButton) {
        sendButton.setOnClickListener(v-> onSendButtonClick());
    }

    protected abstract void onSendButtonClick();

    protected void setInputEditText(TextInputEditText etInput, CircularProgressIndicator progressIndicator) {
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                setIndicatorProgress(progressIndicator, editable.length());
                tweet.setText(editable.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });
    }

    protected void setInputEditTextFilters(TextInputEditText etInput) {
        etInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Tweet.MAX_TWEET_LENGTH) });
    }


    protected void setProgressIndicator(CircularProgressIndicator progressIndicator) {
        progressIndicator.setMax(Tweet.MAX_TWEET_LENGTH);
    }

    protected void setIndicatorProgress(CircularProgressIndicator progressIndicator, int progress) {
        progressIndicator.setProgress(progress);
    }


    protected void processUri(Uri uri) {
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

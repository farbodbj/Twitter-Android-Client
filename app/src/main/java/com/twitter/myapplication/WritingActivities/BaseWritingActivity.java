package com.twitter.myapplication.WritingActivities;

import static com.twitter.common.Utils.SafeCall.safe;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.twitter.common.Models.Messages.Textuals.Tweet;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Messages.Visuals.Video;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.myapplication.DefaultActivity;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardActivityFormat;
import com.twitter.myapplication.Utils.AndroidUtils;

public abstract class BaseWritingActivity extends AppCompatActivity implements StandardActivityFormat {
    protected final static int SEND_TWEET_RESULT_TOAST_DURATION = 2500;
    protected Tweet parentTweet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_writing);


        initializeUIComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        parentTweet = null;
    }

    @Override
    public void initializeUIComponents() {
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton chooseAttachments = findViewById(R.id.select_attachments);
        Button sendButton = findViewById(R.id.tweet_button);
        CircularProgressIndicator progressIndicator = findViewById(R.id.tweet_characters);
        TextInputLayout etInputLayout = findViewById(R.id.etTweetLayout);
        TextInputEditText etInput = findViewById(R.id.etTweet);

        setBackButton(backButton);
        setProgressIndicator(progressIndicator);
        setTextInputLayout(etInputLayout);
        setInputEditText(etInput, progressIndicator, parentTweet);
        setChooseAttachments(chooseAttachments, parentTweet);
        setSendButton(sendButton);
    }

    protected void setBackButton(ImageButton backButton) {
        backButton.setOnClickListener(view -> AndroidUtils.gotoActivity(this, DefaultActivity.class, null));
    }

    protected void setChooseAttachments(ImageButton chooseAttachments, Tweet currentTweet) {
        ActivityResultLauncher<PickVisualMediaRequest> imageSelector =
                AndroidUtils.multipleImageSelector(
                        this,
                        Tweet.MAX_ATTACHMENT_COUNT,
                        uris-> {
                            for (Uri uri : uris) {
                                setAttachments(currentTweet, uri);
                            }
                        });

        chooseAttachments.setOnClickListener(view -> imageSelector.launch(new PickVisualMediaRequest.Builder().build()));
    }

    protected void setSendButton(Button sendButton) {
        sendButton.setOnClickListener(v -> onSendButtonClick());
    }

    protected abstract void onSendButtonClick();

    protected void setInputEditText(TextInputEditText etInput, CircularProgressIndicator progressIndicator, Tweet currentTweet) {
        setInputEditTextFilters(etInput);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                setIndicatorProgress(progressIndicator, editable.length());
                currentTweet.setText(editable.toString());
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

    protected void setTextInputLayout(TextInputLayout textInputLayout) {
        textInputLayout.setHint(getString(R.string.tweet_field_hint));
    }


    protected void tweetSendResultCallback(Boolean result) {
        if(result){
            AndroidUtils.showLongToastMessage(
                    this,
                    (result) ? getString(R.string.tweet_successful) : (getString(R.string.tweet_failed)),
                    SEND_TWEET_RESULT_TOAST_DURATION);
            AndroidUtils.gotoActivity(this, DefaultActivity.class, null);
        }
    }

    protected void setAttachments(Tweet currentTweet, Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        String type = contentResolver.getType(uri);
        String extension = mime.getExtensionFromMimeType(type);

        Visual att = (type.startsWith("image")) ? new Image() : new Video();

        att.setFileBytes(AndroidUtils.getBytesFromUri(this, uri));
        att.setFileFormat(extension);

        if (att instanceof Image) {
            safe(() -> currentTweet.addImageAttachment((Image) att));
        } else {
            safe(() -> currentTweet.addVideoAttachment((Video) att));
        }
    }

}

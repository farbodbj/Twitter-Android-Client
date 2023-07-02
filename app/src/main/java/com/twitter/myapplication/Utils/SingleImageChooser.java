package com.twitter.myapplication.Utils;

import android.net.Uri;

@FunctionalInterface
public interface SingleImageChooser {
    void onSingleImageChosen(Uri imageUri);
}

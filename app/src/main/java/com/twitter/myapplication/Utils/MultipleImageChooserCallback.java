package com.twitter.myapplication.Utils;

import android.net.Uri;

import java.util.List;

@FunctionalInterface
public interface MultipleImageChooserCallback {
    void onMultipleImageChosen(List<Uri> imageUri);
}

package com.twitter.myapplication.StandardFormats;

import android.view.View;

import androidx.annotation.NonNull;

public interface StandardFragmentFormat extends StandardLayoutFormat {
    void initializeUIComponents(@NonNull View view);
}

package com.twitter.myapplication;

import android.view.View;

import androidx.annotation.NonNull;

public interface StandardFragment extends StandardLayout {
    void initializeUIComponents(@NonNull View view);
}

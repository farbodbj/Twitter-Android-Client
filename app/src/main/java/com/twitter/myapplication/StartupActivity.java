package com.twitter.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import static com.twitter.myapplication.Utils.AndroidUtils.gotoFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        //Fragment
        Fragment startupChoiceFragment = new StartupChoiceFragment();

        //getting the fragment manager and finding the destination fragment container
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.findFragmentById(R.id.fragment_container);

        gotoFragment(fragmentManager, startupChoiceFragment, R.id.fragment_container, null);
    }

}
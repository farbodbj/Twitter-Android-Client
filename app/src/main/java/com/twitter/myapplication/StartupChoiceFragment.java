package com.twitter.myapplication;

import static com.twitter.myapplication.Utils.AndroidUtils.gotoFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StartupChoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_startup_choice, container, false);

        Button signInChoice = view.findViewById(R.id.startup_sign_in_button);
        Button signUpChoice = view.findViewById(R.id.startup_sign_up_button);
        Fragment signInFragment = new SignInFragment();
        Fragment signUpFragment = new SignUpFragment();

        FragmentManager fragmentManager = getParentFragmentManager();


        signInChoice.setOnClickListener(v -> gotoFragment(fragmentManager, signInFragment, R.id.fragment_container, null));
        signUpChoice.setOnClickListener(v -> gotoFragment(fragmentManager, signUpFragment, R.id.fragment_container, null));

        return view;
    }
}
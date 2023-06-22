package com.twitter.myapplication;

import static com.twitter.myapplication.Utils.ValidationUtils.isEmailValid;
import static com.twitter.myapplication.Utils.ValidationUtils.isPasswordValid;
import static com.twitter.myapplication.Utils.ValidationUtils.isUsernameValid;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.twitter.common.Exceptions.InternalServerError;
import com.twitter.common.Exceptions.WrongCredentials;
import com.twitter.common.Models.User;
import com.twitter.myapplication.Utils.StorageManager.InternalStorageManager;

public class SignInFragment extends Fragment {

    public interface SignInListener {
        void onSignInButtonClicked(String username, String password);
        void onSignInResult(boolean isSuccess, User user, Exception error);
    }
    private SignInListener signInListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        Button signInButton = view.findViewById(R.id.sing_in_button);
        TextInputEditText etUsername = view.findViewById(R.id.etUsername);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);

        signInButton.setOnClickListener(view1 -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            //the entered value in username field can be either a username or an email
            if (isPasswordValid(password) && (isEmailValid(username) || isUsernameValid(username))) {
                signInListener.onSignInButtonClicked(username, password);

            } else if (!isEmailValid(username)){
                etUsername.setError(getString(R.string.invalid_email_format));

            } else if (!isUsernameValid(username)) {
                etUsername.setError(getString(R.string.invalid_username_format));

            } else if (!isPasswordValid(username)) {
                etPassword.setError(getString(R.string.invalid_password_format));

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignInListener) {
            signInListener = (SignInListener) context;
        } else {
            throw new ClassCastException(context
                    + " must implement MyFragment.OnButtonClickListener");
        }
    }

    public void handleSignInResult(boolean isSuccess, User user, Class<? extends Exception> failReason) {
        //TODO: make the button clickable only once
        if(isSuccess && failReason == null) {
            getView().findViewById(R.id.sign_in_successful).setVisibility(View.VISIBLE);
            InternalStorageManager.writeObjectToFile(getActivity().getApplicationContext(), user, "current_user.bin");
            //TODO: goto the next activity (probably timeline or user account page)
        }
        else {
            if(failReason == WrongCredentials.class) {
                getView().findViewById(R.id.wrong_user_pass).setVisibility(View.VISIBLE);

            } else if (failReason == InternalServerError.class) {
                getView().findViewById(R.id.internal_server_error).setVisibility(View.VISIBLE);
            }
        }
    }
}
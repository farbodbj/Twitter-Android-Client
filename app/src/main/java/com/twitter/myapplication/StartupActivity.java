package com.twitter.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import static com.twitter.myapplication.Utils.AndroidUtils.gotoFragment;

import android.os.Bundle;
import android.os.Debug;

import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.common.Exceptions.HandledException;
import com.twitter.common.Exceptions.InternalServerError;
import com.twitter.common.Exceptions.WrongCredentials;
import com.twitter.common.Models.User;

import kotlinx.coroutines.AwaitKt;

public class StartupActivity extends AppCompatActivity implements SignInFragment.SignInListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        //Startup page initial fragment
        Fragment startupChoiceFragment = new StartupChoiceFragment();

        //getting the fragment manager and finding the destination fragment container
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.fragment_container);

        gotoFragment(fragmentManager, startupChoiceFragment, R.id.fragment_container, null);
    }

    @Override
    public void onSignInButtonClicked(String username, String password) {
        try {
            UserActionsManager.getInstance().signIn(
                    username,
                    password,
                    success -> {
                        Debug.waitForDebugger();
                        System.out.println("hey");
                        onSignInResult(true, success, null);
                    },
                    error -> {
                        Debug.waitForDebugger();
                        System.out.println(error.getMessage());
                    }
            );

        } catch (WrongCredentials e) {
            onSignInResult(false, null, e);

        } catch (InternalServerError e) {
            onSignInResult(false, null, e);

        } catch (IllegalStateException e) {
            onSignInResult(false, null, e);

        } catch (HandledException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onSignInResult(boolean isSuccess, User user, Exception error) {
        SignInFragment signInFragment =  (SignInFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(signInFragment != null) {
            if (error != null)
                signInFragment.handleSignInResult(isSuccess, user, error.getClass());

            else
                signInFragment.handleSignInResult(isSuccess, user, null);
        }
    }
}
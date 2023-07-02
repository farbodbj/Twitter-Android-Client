package com.twitter.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.twitter.common.Models.User;
import com.twitter.myapplication.StandardFormats.StandardFragmentFormat;
import com.twitter.myapplication.Utils.AndroidUtils;
import com.twitter.myapplication.Utils.StorageManager.StorageHandler;

import java.util.Calendar;
import java.util.Locale;

public class UserProfileFragment extends Fragment implements StandardFragmentFormat {
    public interface UserProfileEventListener {
        void onFollowButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onUnfollowButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onBlockButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onUnblockButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onFollowingsCountClicked(UserProfileFragment userProfileFragment, User target);
        void onFollowersCountClicked(UserProfileFragment userProfileFragment, User target);
        void onEditProfileButtonClicked();
    }

    private boolean isSelfProfile = false;

    private UserProfileEventListener userProfileEventListener;

    public UserProfileFragment(boolean isSelfProfile) {
        this.isSelfProfile = isSelfProfile;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);
        initializeUIComponents(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            userProfileEventListener = (UserProfileEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement UserProfileEventListener");
        }
    }

    @Override
    public void initializeUIComponents(@NonNull View view) {
        Bundle userData = getArguments();
        if(userData != null) {
            User user = (User)userData.getSerializable("user");

            if(getContext() != null) {

                loadProfilePicture(user, view);
                loadHeaderPicture(user, view);
                setProfile(user, view);
                setFollowingsCountView(userData, view);
                setFollowersCountView(userData, view);

                if(!isSelfProfile) {
                    setFollowButton(user, view);
                    setUnfollowButton(user, view);

                } else {
                    setEditProfileButton(view);
                }

            }
        }
    }

    private void loadProfilePicture(User user, View view) {
        ShapeableImageView profilePictureView = view.findViewById(R.id.profile_picture_choice);

        Uri userProfilePicUri = StorageHandler.saveProfilePicture(getContext(), user);
        if (userProfilePicUri != null) {
            Glide
                    .with(getContext())
                    .load(userProfilePicUri)
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(profilePictureView);
        }
    }

    private void loadHeaderPicture(User user, View view) {
        AppCompatImageView headerPictureView = view.findViewById(R.id.header_picture_choice);

        Uri userHeaderPicUri = StorageHandler.saveHeaderPicture(getContext(), user);
        if(userHeaderPicUri != null) {
            Glide
                    .with(getContext())
                    .load(userHeaderPicUri)
                    .centerCrop()
                    .placeholder(R.drawable.header_placeholder)
                    .into(headerPictureView);
        }
    }

    private void setProfile(User user, View view) {
        MaterialTextView displayNameView = view.findViewById(R.id.display_name);
        MaterialTextView usernameView = view.findViewById(R.id.username);
        MaterialTextView biographyView = view.findViewById(R.id.biography);
        MaterialTextView dateJoinedView = view.findViewById(R.id.date_joined);

        displayNameView.setText(user.getDisplayName());
        usernameView.setText(getString(R.string.username_field, user.getUsername()));
        biographyView.setText(user.getBio());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getAccountMade());
        dateJoinedView.setText(getString(
                        R.string.date_joined_field,
                        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                        calendar.get(Calendar.YEAR)));


        usernameView.setOnLongClickListener(v-> onUsernameHoldListener(user));
    }

    private boolean onUsernameHoldListener(User user) {
        AlertDialog.Builder blockOptionsBuilder = new AlertDialog.Builder(getContext());
        blockOptionsBuilder.setTitle(R.string.block_options_title);
        blockOptionsBuilder.setItems(R.array.block_options, (dialogInterface, i) -> {
            switch (i) {
                case 0 ->  userProfileEventListener.onBlockButtonClicked(this, user);
                case 1 ->  userProfileEventListener.onUnblockButtonClicked(this, user);
            }
        });
        blockOptionsBuilder.show();
        return true;
    }

    private void setFollowButton(User user, View view) {
        Button followButton = view.findViewById(R.id.follow_button);
        followButton.setVisibility(View.VISIBLE);
        followButton.setOnClickListener(v-> userProfileEventListener.onFollowButtonClicked(this, user));
    }

    private void setUnfollowButton(User user, View view) {
        Button unfollowButton = view.findViewById(R.id.unfollow_button);
        unfollowButton.setVisibility(View.VISIBLE);
        unfollowButton.setOnClickListener(v -> userProfileEventListener.onUnfollowButtonClicked(this, user));
    }

    private void setEditProfileButton(View view) {
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        editProfileButton.setVisibility(View.VISIBLE);
        editProfileButton.setOnClickListener(v -> userProfileEventListener.onEditProfileButtonClicked());

    }

    public void showActionResultToast(String message) {
        if(getActivity() != null)
            getActivity().runOnUiThread(() -> AndroidUtils.showLongToastMessage(getContext(), message, 2000));
    }


    private void setFollowingsCountView(Bundle userData, View view) {
        MaterialTextView followingsCountView = view.findViewById(R.id.following_count);
        followingsCountView.setText(getString(R.string.following_count, userData.getInt("followings_count")));

        followingsCountView.setOnClickListener(v-> userProfileEventListener.onFollowersCountClicked(this, (User)userData.getSerializable("user")));
    }

    private void setFollowersCountView(Bundle userData, View view) {
        MaterialTextView followersCountView = view.findViewById(R.id.follower_count);
        followersCountView.setText(getString(R.string.follower_count, userData.getInt("followers_count")));

        followersCountView.setOnClickListener(v-> userProfileEventListener.onFollowersCountClicked(this, (User)userData.getSerializable("user")));
    }
}
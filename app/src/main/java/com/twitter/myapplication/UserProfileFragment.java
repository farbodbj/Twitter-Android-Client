package com.twitter.myapplication;

import static com.twitter.myapplication.Utils.StorageManager.StorageHandler.saveProfilePicture;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AlertDialogLayout;
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
import java.util.Date;
import java.util.Locale;

public class UserProfileFragment extends Fragment implements StandardFragmentFormat {
    public interface UserProfileEventListener {
        void onFollowButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onUnfollowButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onBlockButtonClicked(UserProfileFragment userProfileFragment, User target);
        void onUnblockButtonClicked(UserProfileFragment userProfileFragment, User target);
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
        ShapeableImageView profilePictureView = view.findViewById(R.id.profile_picture);
        AppCompatImageView headerPictureView = view.findViewById(R.id.header_picture);
        Button followButton = view.findViewById(R.id.follow_button);
        Button unfollowButton = view.findViewById(R.id.unfollow_button);
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        MaterialTextView displayNameView = view.findViewById(R.id.display_name);
        MaterialTextView usernameView = view.findViewById(R.id.username);
        MaterialTextView biographyView = view.findViewById(R.id.biography);
        MaterialTextView dateJoinedView = view.findViewById(R.id.date_joined);
        MaterialTextView followingsCountView = view.findViewById(R.id.following_count);
        MaterialTextView View = view.findViewById(R.id.follower_count);
        //TODO: handle follow unfollow and edit profile button


        Bundle userData = getArguments();
        if(userData != null) {
            View.setText(getString(R.string.follower_count, userData.getInt("followers_count")));
            followingsCountView.setText(getString(R.string.following_count, userData.getInt("followings_count")));
            bindUserData(
                    (User)userData.getSerializable("user"),
                    profilePictureView,
                    headerPictureView,
                    displayNameView,
                    usernameView,
                    biographyView,
                    dateJoinedView,
                    followButton,
                    unfollowButton,
                    editProfileButton);
        }

    }

    private void bindUserData(User user,
                              ShapeableImageView profilePictureView,
                              ImageView headerPictureView,
                              MaterialTextView displayNameView,
                              MaterialTextView usernameView,
                              MaterialTextView biographyView,
                              MaterialTextView dateJoinedView,
                              Button followButtonView,
                              Button unfollowButtonView,
                              Button editProfileButtonView
    ) {
        if(getContext() != null) {
            loadProfilePicture(user, profilePictureView);

            loadHeaderPicture(user, headerPictureView);

            setProfile(user, displayNameView, usernameView, biographyView, dateJoinedView);


            if(!isSelfProfile) {
                setFollowButton(user, followButtonView);
                setUnfollowButton(user, unfollowButtonView);
            }
            else {
                setEditProfileButton(editProfileButtonView);
            }

        }
    }

    private void loadProfilePicture(User user, ShapeableImageView profilePictureView) {
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

    private void loadHeaderPicture(User user, ImageView headerPictureView) {
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

    private void setProfile(User user, MaterialTextView displayNameView, MaterialTextView usernameView, MaterialTextView biographyView, MaterialTextView dateJoinedView) {
        displayNameView.setText(user.getDisplayName());

        usernameView.setText(getString(R.string.username_field, user.getUsername()));
        usernameView.setOnLongClickListener(view-> onUsernameHoldListener(user));

        biographyView.setText(user.getBio());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getAccountMade());
        dateJoinedView.setText(getString(
                R.string.date_joined_field,
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                calendar.get(Calendar.YEAR))
        );
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

    private void setFollowButton(User user, Button followButton) {
        followButton.setVisibility(View.VISIBLE);
        followButton.setOnClickListener(view-> userProfileEventListener.onFollowButtonClicked(this, user));
    }

    private void setUnfollowButton(User user, Button unfollowButton) {
        unfollowButton.setVisibility(View.VISIBLE);
        unfollowButton.setOnClickListener(view -> userProfileEventListener.onUnfollowButtonClicked(this, user));
    }

    private void setEditProfileButton(Button editProfileButton) {
        editProfileButton.setVisibility(View.VISIBLE);

    }

    public void showActionResultToast(String message) {
        if(getActivity() != null)
            getActivity().runOnUiThread(() -> AndroidUtils.showLongToastMessage(getContext(), message, 2000));
    }

}
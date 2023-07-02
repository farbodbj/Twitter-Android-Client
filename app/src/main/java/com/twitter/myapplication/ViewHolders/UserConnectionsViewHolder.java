package com.twitter.myapplication.ViewHolders;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.twitter.common.Models.User;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardViewHolder;
import com.twitter.myapplication.Utils.StorageManager.StorageHandler;

public class UserConnectionsViewHolder extends RecyclerView.ViewHolder implements StandardViewHolder<User> {

    public interface OnUserConnectionsItemEventListener {
        void onUserDisplayNameClicked(User user);
    }

    OnUserConnectionsItemEventListener onUserConnectionsItemEventListener;

    public UserConnectionsViewHolder(@NonNull View itemView, OnUserConnectionsItemEventListener onUserConnectionsItemEventListener) {
        super(itemView);
        this.onUserConnectionsItemEventListener = onUserConnectionsItemEventListener;
    }

    @Override
    public void initializeUiComponents() {}

    @Override
    public void bind(User data) {
        setProfilePics(data);
        setDisplayName(data);
        setUsername(data);
        setBio(data);
    }

    private void setDisplayName(User user) {
        MaterialTextView displayNameView = itemView.findViewById(R.id.account_name);
        displayNameView.setText(user.getDisplayName());
        displayNameView.setOnClickListener(v->onUserConnectionsItemEventListener.onUserDisplayNameClicked(user));
    }

    private void setUsername(User user) {
        ((MaterialTextView)itemView.findViewById(R.id.username)).setText(itemView.getContext().getString(R.string.username_field,user.getUsername()));
    }

    private void setBio(User user) {
        ((MaterialTextView)itemView.findViewById(R.id.biography)).setText(user.getBio());
    }

    private void setProfilePics(User user) {
        ShapeableImageView profilePic = itemView.findViewById(R.id.profile_picture);

        Uri profilePicUri = StorageHandler.saveProfilePicture(profilePic.getContext(), user);
        if(profilePicUri != null) {
            Glide
                    .with(profilePic.getContext())
                    .load(profilePicUri)
                    .placeholder(R.drawable.profile_placeholder)
                    .centerCrop()
                    .into(profilePic);
        }
    }
}

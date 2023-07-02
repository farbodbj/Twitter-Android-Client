package com.twitter.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.twitter.client.Session;
import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.User;
import com.twitter.myapplication.StandardFormats.StandardFragmentFormat;
import com.twitter.myapplication.Utils.AndroidUtils;
import com.twitter.myapplication.Utils.StorageManager.StorageAccessor;

import java.io.File;

public class EditProfileFragment extends Fragment implements StandardFragmentFormat {
    public interface OnProfileChangeEventListener {
        void onProfilePicChanged(File profile);
        void onHeaderPicChanged(File header);
        void onAccountNameChanged(String newAccountName);
        void onBioChanged(String newBio);
        void onLocationChanged(String newLocation);
    }

    private boolean isProfilePicChanged = false;
    private boolean isHeaderPicChanged = false;
    private Uri newProfileUri;
    private Uri newHeaderUri;

    private OnProfileChangeEventListener onProfileChangeEventListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initializeUIComponents(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onProfileChangeEventListener = (OnProfileChangeEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement UserProfileEventListener");
        }
    }

    @Override
    public void initializeUIComponents(@NonNull View view) {
        setProfilePictureChooser(view);
        setHeaderPicChooser(view);
        setSubmitChangesButton(view);
    }

    private void setProfilePictureChooser(View view) {
        ShapeableImageView profile = view.findViewById(R.id.profile_picture_choice);
        ActivityResultLauncher<PickVisualMediaRequest> imageSelector =
                AndroidUtils.singleImageSelector(this,
                uri -> {
                    newProfileUri = uri;
                    Glide.
                            with(view.getContext())
                            .load(newProfileUri)
                            .centerCrop()
                            .into(profile);
                    isProfilePicChanged = true;
                });

        profile.setOnClickListener(v -> imageSelector.launch(new PickVisualMediaRequest()));
    }

    private void setHeaderPicChooser(View view) {
        ImageView banner = view.findViewById(R.id.header_picture_choice);
        ActivityResultLauncher<PickVisualMediaRequest> imageSelector =
                AndroidUtils.singleImageSelector(this,
                uri -> {
                    newHeaderUri = uri;
                    Glide.
                            with(view.getContext())
                            .load(newHeaderUri)
                            .centerCrop()
                            .into(banner);
                    isHeaderPicChanged = true;
                });

        banner.setOnClickListener(v -> imageSelector.launch(new PickVisualMediaRequest()));
    }

    private void setSubmitChangesButton(View view) {
        User currentUser = Session.getInstance().getSessionUser();
        Button submitChanges = view.findViewById(R.id.submit_changes);
        submitChanges.setOnClickListener(v->{
            String newAccountName = ((EditText) view.findViewById(R.id.account_name_field)).getText().toString();
            String newBiography = ((EditText)view.findViewById(R.id.biography_field)).getText().toString();
            String newLocation = ((EditText)view.findViewById(R.id.location_field)).getText().toString();

            if(!newAccountName.equals(currentUser.getDisplayName()) && !newAccountName.isBlank()) {
                currentUser.setDisplayName(newAccountName);
                onProfileChangeEventListener.onAccountNameChanged(newAccountName);
            }
            if(!newBiography.equals(currentUser.getBio()) && !newBiography.isBlank()) {
                currentUser.setBio(newBiography);
                onProfileChangeEventListener.onBioChanged(newBiography);
            }
            if(!newLocation.equals(currentUser.getLocation()) && !newLocation.isBlank()) {
                currentUser.setLocation(newLocation);
                onProfileChangeEventListener.onLocationChanged(newLocation);
            }
            if(isProfilePicChanged) {
                File newProfileFile = StorageAccessor.contentUriToFile(getContext(), newProfileUri);
                currentUser.setProfilePic(new Image(newProfileFile));
                onProfileChangeEventListener.onProfilePicChanged(newProfileFile);
            }
            if(isHeaderPicChanged) {
                File newHeaderFile = StorageAccessor.contentUriToFile(getContext(), newHeaderUri);
                currentUser.setHeaderPic(new Image(newHeaderFile));
                onProfileChangeEventListener.onHeaderPicChanged(newHeaderFile);
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("user", currentUser);
            AndroidUtils.gotoFragment(getParentFragmentManager(), new UserProfileFragment(true), R.id.default_activity_fragment_container, bundle);
            AndroidUtils.showLongToastMessage(getContext(), getString(R.string.profile_updated), 2000);

        });
    }
}
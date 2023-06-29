package com.twitter.myapplication.ViewHolders;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.myapplication.R;
import com.twitter.myapplication.Utils.StorageManager.StorageHandler;

import java.io.File;
import java.io.IOException;

public class AttachmentViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;

    public AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.attachment_image_view);
    }

    public void bind(Uri attachmentUri) throws IOException {
        String format = StorageHandler.getExtensionFromUri(attachmentUri);
        File image = new File(attachmentUri.getPath());
        if(Visual.ALLOWED_IMAGE_FORMAT_EXTENSIONS.contains(format)) {
            Glide
                    .with(imageView.getContext())
                    .load(image)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners((int) itemView.getContext().getResources().getDimension(R.dimen.default_corner_radius))))
                    .into(imageView);

            imageView.setVisibility(View.VISIBLE);
        }
    }
}

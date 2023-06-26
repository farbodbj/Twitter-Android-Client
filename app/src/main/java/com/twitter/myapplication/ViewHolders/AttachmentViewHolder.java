package com.twitter.myapplication.ViewHolders;

import static java.security.AccessController.getContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.twitter.common.Models.Messages.Visuals.Image;
import com.twitter.common.Models.Messages.Visuals.Video;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.myapplication.R;
import com.twitter.myapplication.Utils.AndroidUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AttachmentViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private VideoView videoView;

    private final BroadcastReceiver videoFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(intent.getAction())) {
                Uri videoUri = intent.getData();
                if (videoUri != null) {
                    File tempFile = new File(videoUri.getPath());
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                }
                context.unregisterReceiver(this);
            }
        }
    };

    public AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.attachment_image_view);
        videoView = itemView.findViewById(R.id.attachment_video_view);
    }

    public void bind(Visual attachment) {
        if (attachment instanceof Image) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(attachment.getFileBytes(), 0, attachment.getFileBytes().length);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
        } else if (attachment instanceof Video) {
            // Follow the steps in the previous answer to handle video attachments
            Bitmap thumbnail = getVideoThumbnail(attachment, imageView.getContext());
            imageView.setImageBitmap(thumbnail);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                playVideo(attachment, imageView.getContext());
            });
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    public VideoView getVideoView() {
        return videoView;
    }

    private Bitmap getVideoThumbnail(Visual videoAttachment, Context context) {
        // Convert byte array to a temporary file
        File tempFile = AndroidUtils.cacheFileBytes(
                videoAttachment.getFileBytes(),
                "tmp_video",
                videoAttachment.getFileFormat(),
                context);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(videoAttachment.getFileBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the thumbnail of the video
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return context.getContentResolver().loadThumbnail(Uri.fromFile(tempFile), new Size(640, 480), null);
            }
            else return ThumbnailUtils.createVideoThumbnail(tempFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);

        } catch (IOException e) {
            //Error handling logic
        }
        return null;
    }


    private void playVideo(Visual videoAttachment, Context context) {
        // Save the video bytes to a temporary file
        File tmpFile = AndroidUtils.cacheFileBytes(
                videoAttachment.getFileBytes(),
                "tmp_video",
                videoAttachment.getFileFormat(),
                context);

        // Register the BroadcastReceiver to listen for the media scanner finished action
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        context.registerReceiver(videoFinishedReceiver, filter);

        // Play the video using an Intent
        Uri videoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", tmpFile);
        AndroidUtils.playVideoFromUri(videoUri, context);
    }
}

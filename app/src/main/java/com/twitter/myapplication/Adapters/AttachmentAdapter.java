package com.twitter.myapplication.Adapters;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.twitter.common.Models.Messages.Visuals.Visual;
import com.twitter.myapplication.R;
import com.twitter.myapplication.ViewHolders.AttachmentViewHolder;

import java.io.IOException;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {
    private List<Uri> attachmentUris;

    public AttachmentAdapter(List<Uri> attachmentUris) {
        this.attachmentUris = attachmentUris;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item, parent, false);
        return new AttachmentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        Uri attachment = attachmentUris.get(position);
        try {
            holder.bind(attachment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return attachmentUris.size();
    }
}

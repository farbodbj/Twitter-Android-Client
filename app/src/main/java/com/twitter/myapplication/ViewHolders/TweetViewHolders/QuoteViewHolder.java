package com.twitter.myapplication.ViewHolders.TweetViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.twitter.common.Models.Messages.Textuals.Quote;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardViewHolder;

public class QuoteViewHolder extends RecyclerView.ViewHolder implements StandardViewHolder<Quote>{
    private TweetViewHolder quoteViewHolder;

    private ShapeableImageView quotedProfilePicture;
    private MaterialTextView quotedDisplayName;
    private MaterialTextView quotedUsername;
    private MaterialTextView quotedText;


    public QuoteViewHolder(@NonNull View itemView, TweetViewHolder.TweetItemEventListener tweetItemEventListener) {
        super(itemView);
        quoteViewHolder = new TweetViewHolder(itemView.findViewById(R.id.quote_item), tweetItemEventListener);
    }

    @Override
    public void initializeUiComponents() {
        quoteViewHolder.initializeUiComponents();
        hideTweetLineBreak();
        quotedProfilePicture = itemView.findViewById(R.id.quoted_profile_picture);
        quotedDisplayName = itemView.findViewById(R.id.quoted_author_display_name);
        quotedUsername = itemView.findViewById(R.id.quoted_author_username);
        quotedText = itemView.findViewById(R.id.quoted_text);
    }

    @Override
    public void bind(Quote data) {
        quoteViewHolder.bind(data);

        //TODO: bind quoted profile picture
        quotedDisplayName.setText(data.getQuoted().getSender().getDisplayName());
        quotedUsername.setText(data.getQuoted().getSender().getUsername());
        quotedText.setText(data.getQuoted().getText());
    }

    private void hideTweetLineBreak() {
        itemView.findViewById(R.id.quote_frame).findViewById(R.id.tweet_horizontal_line_break).setVisibility(View.GONE);
    }
}

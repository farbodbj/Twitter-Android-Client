package com.twitter.myapplication.ViewHolders.TweetViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twitter.common.Models.Messages.Textuals.Mention;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardViewHolder;

public class MentionViewHolder extends RecyclerView.ViewHolder implements StandardViewHolder<Mention> {

    private TweetViewHolder mentionViewHolder;
    private TweetViewHolder mentionedViewHolder;

    public MentionViewHolder(@NonNull View itemView, TweetViewHolder.TweetItemEventListener tweetItemEventListener) {
        super(itemView);
        mentionViewHolder = new TweetViewHolder(itemView.findViewById(R.id.mention_item), tweetItemEventListener);
        mentionedViewHolder = new TweetViewHolder(itemView.findViewById(R.id.mentioned_item), tweetItemEventListener);
    }


    @Override
    public void initializeUiComponents() {
        mentionViewHolder.initializeUiComponents();
        hideTweetLineBreak();
        mentionedViewHolder.initializeUiComponents();
    }

    @Override
    public void bind(Mention data) {
        mentionViewHolder.bind(data);
        mentionedViewHolder.bind(data.getMentionedTo());
    }

    private void hideTweetLineBreak() {
        itemView.findViewById(R.id.mentioned_item).findViewById(R.id.tweet_horizontal_line_break).setVisibility(View.GONE);
    }
}

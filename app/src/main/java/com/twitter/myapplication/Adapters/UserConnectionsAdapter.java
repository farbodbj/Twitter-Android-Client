package com.twitter.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twitter.common.Models.UserGraph;
import com.twitter.myapplication.R;
import com.twitter.myapplication.ViewHolders.UserConnectionsViewHolder;

public class UserConnectionsAdapter extends RecyclerView.Adapter<UserConnectionsViewHolder>{
    private UserGraph userGraph;

    private UserConnectionsViewHolder.OnUserConnectionsItemEventListener onUserConnectionsItemListener;

    public UserConnectionsAdapter(UserGraph userGraph, UserConnectionsViewHolder.OnUserConnectionsItemEventListener onUserConnectionsItemListener) {
        this.userGraph = userGraph;
        this.onUserConnectionsItemListener = onUserConnectionsItemListener;
    }

    @NonNull
    @Override
    public UserConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_connections_item, parent, false);
        return new UserConnectionsViewHolder(view, onUserConnectionsItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserConnectionsViewHolder holder, int position) {
        holder.bind(userGraph.get(position));
    }

    @Override
    public int getItemCount() {return userGraph.size();
    }

}

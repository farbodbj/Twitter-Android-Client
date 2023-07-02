package com.twitter.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.common.Models.User;
import com.twitter.common.Models.UserGraph;
import com.twitter.myapplication.Adapters.UserConnectionsAdapter;
import com.twitter.myapplication.StandardFormats.StandardFragmentFormat;
import com.twitter.myapplication.ViewHolders.UserConnectionsViewHolder;


public class UserConnectionsFragment extends Fragment implements StandardFragmentFormat, UserConnectionsViewHolder.OnUserConnectionsItemEventListener {

    UserConnectionsViewHolder.OnUserConnectionsItemEventListener onConnectionsItemListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_connections, container, false);
        initializeUIComponents(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.onConnectionsItemListener = (UserConnectionsViewHolder.OnUserConnectionsItemEventListener) context;
        } catch (ClassCastException e) {
            System.out.println("context Activity must implement UserConnectionsViewHolder.OnUserConnectionsItemEventListener.");
        }
    }

    @Override
    public void initializeUIComponents(@NonNull View view) {
        setConnections(view);
    }

    private void setConnections(View view) {
        UserGraph users = (UserGraph) getArguments().getSerializable("users");
        if(users != null) {
            RecyclerView connectionsView = view.findViewById(R.id.connections_list);
            UserConnectionsAdapter connectionsAdapter = new UserConnectionsAdapter(users, this);
            connectionsView.setAdapter(connectionsAdapter);
            connectionsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onUserDisplayNameClicked(User user) {
        onConnectionsItemListener.onUserDisplayNameClicked(user);
    }
}
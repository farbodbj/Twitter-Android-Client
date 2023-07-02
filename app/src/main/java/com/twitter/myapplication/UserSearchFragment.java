package com.twitter.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.twitter.common.Models.User;
import com.twitter.common.Models.UserGraph;
import com.twitter.myapplication.Adapters.UserConnectionsAdapter;
import com.twitter.myapplication.StandardFormats.StandardFragmentFormat;
import com.twitter.myapplication.ViewHolders.UserConnectionsViewHolder;


public class UserSearchFragment extends Fragment implements StandardFragmentFormat, UserConnectionsViewHolder.OnUserConnectionsItemEventListener {
    public interface OnUserSearchFragmentEventListener {
        void onSearchButtonClicked(UserSearchFragment userSearchFragment, String searchTerm);
        void onSearchItemDisplayNameClicked(UserSearchFragment userSearchFragment, User user);
    }

    private UserConnectionsAdapter searchResultAdapter;

    OnUserSearchFragmentEventListener onUserSearchItemListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_search, container, false);
        initializeUIComponents(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onUserSearchItemListener = (OnUserSearchFragmentEventListener) context;
        } catch (ClassCastException e) {
            System.out.println("context must implement OnUserSearchFragmentEventListener!");
        }
    }

    @Override
    public void initializeUIComponents(@NonNull View view) {
        setSearchBar(view);
    }

    @Override
    public void onUserDisplayNameClicked(User user) {
        onUserSearchItemListener.onSearchItemDisplayNameClicked(this, user);
    }

    private void setSearchBar(View view) {
        ImageButton searchButton = view.findViewById(R.id.search_button);
        EditText searchField = view.findViewById(R.id.search_edit_text);

        searchButton.setOnClickListener(v-> onUserSearchItemListener.onSearchButtonClicked(this, searchField.getText().toString()));
    }


    private void setSearchResult(UserGraph searchResult, View view) {
        RecyclerView searchResultView = view.findViewById(R.id.search_result);
        this.searchResultAdapter = new UserConnectionsAdapter(searchResult, this);
        searchResultView.setAdapter(this.searchResultAdapter);
        searchResultAdapter.notifyDataSetChanged();
    }

    public void showSearchResult(UserGraph userGraph) {
        setSearchResult(userGraph, getView());
    }
}
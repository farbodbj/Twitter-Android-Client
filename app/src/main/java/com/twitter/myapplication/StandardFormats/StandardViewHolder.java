package com.twitter.myapplication.StandardFormats;

import androidx.recyclerview.widget.RecyclerView;

public interface StandardViewHolder<T>  {
    void initializeUiComponents();
    void bind(T data);
}

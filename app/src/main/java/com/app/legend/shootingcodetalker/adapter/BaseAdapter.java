package com.app.legend.shootingcodetalker.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.app.legend.shootingcodetalker.interfaces.OnFileDownloadListener;

public class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {


    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

}

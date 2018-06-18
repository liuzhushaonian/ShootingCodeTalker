package com.app.legend.shootingcodetalker.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.DownloadFile;

import java.util.List;

public class DownloadAdapter extends BaseAdapter<DownloadAdapter.ViewHolder>{

    List<DownloadFile> fileList;

    public void setFileList(List<DownloadFile> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View view=inflater.inflate(R.layout.download_item,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
        if (this.fileList==null){
            return;
        }

        DownloadFile file=this.fileList.get(position);

        holder.name.setText(file.getName());
        holder.path.setText(file.getPath());

    }

    @Override
    public int getItemCount() {

        if (this.fileList!=null){
            return this.fileList.size();
        }

        return super.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView name,path;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            this.name=itemView.findViewById(R.id.download_name);
            this.path=itemView.findViewById(R.id.download_path);
        }
    }
}

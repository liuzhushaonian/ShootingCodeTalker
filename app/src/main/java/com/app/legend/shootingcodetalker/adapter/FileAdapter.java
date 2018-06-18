package com.app.legend.shootingcodetalker.adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.OnFileClickListener;
import com.app.legend.shootingcodetalker.utils.HtmlPagerUtil;
import com.app.legend.shootingcodetalker.utils.ShootingApp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FileAdapter extends BaseAdapter<FileAdapter.ViewHolder> {


    private List<SubFile> subFileList;
    private OnFileClickListener onFileClickListener;


    public void setOnFileClickListener(OnFileClickListener onFileClickListener) {
        this.onFileClickListener = onFileClickListener;
    }

    public void setSubFileList(List<SubFile> subFileList) {
        this.subFileList = subFileList;
        notifyDataSetChanged();
    }

    public void addSubFiles(SubFile subFile){

        if (this.subFileList==null){
            this.subFileList=new ArrayList<>();
        }

        if (subFile==null){
            return;
        }

        this.subFileList.add(subFile);

//        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View view=inflater.inflate(R.layout.file_item,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);

        viewHolder.downloadBtn.setOnClickListener(v -> {

            int position=viewHolder.getAdapterPosition();

            SubFile subFile=this.subFileList.get(position);

            download(subFile);

        });


        viewHolder.view.setOnClickListener(v -> {

            int position=viewHolder.getAdapterPosition();
            SubFile subFile=this.subFileList.get(position);

            if (this.onFileClickListener!=null){

                this.onFileClickListener.click(subFile,viewHolder.view);//交给外部处理
            }

        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
        if (this.subFileList==null){
            return;
        }

        SubFile subFile=this.subFileList.get(position);

        holder.title.setText(subFile.getName());
        holder.size.setText(subFile.getSize());

        int d=subFile.getDownload();

        if (d>0){

            holder.downloadBtn.setImageResource(R.drawable.ic_done_black_24dp);

            int color= ShootingApp.getContext().getResources().getColor(R.color.colorGreen);

            holder.downloadBtn.setImageTintList(ColorStateList.valueOf(color));

        }else {
            int color= ShootingApp.getContext().getResources().getColor(R.color.colorPink);
            holder.downloadBtn.setImageResource(R.drawable.download);
            holder.downloadBtn.setImageTintList(ColorStateList.valueOf(color));
        }


    }

    @Override
    public int getItemCount() {
        if (this.subFileList==null) {
            return super.getItemCount();
        }

        return this.subFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,size;
        ImageView downloadBtn;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            title=itemView.findViewById(R.id.file_title);
            size=itemView.findViewById(R.id.file_size);
            downloadBtn=itemView.findViewById(R.id.file_download);
        }
    }

    private void download(SubFile subFile){

        Observable
                .create((ObservableOnSubscribe<Integer>) e -> {

                    int d= HtmlPagerUtil.download(subFile.getDownloadLink(),subFile.getName());

                    e.onNext(d);
                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    Disposable disposable;
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Integer integer) {

                        if (integer==-100){//无网络状态
                            Toast.makeText(ShootingApp.getContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                        }

                        subFile.setDownload(integer);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });
    }


}

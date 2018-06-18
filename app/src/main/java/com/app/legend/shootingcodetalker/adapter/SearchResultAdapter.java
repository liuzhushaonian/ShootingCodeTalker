package com.app.legend.shootingcodetalker.adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.interfaces.OnSearchItemClickListener;
import com.app.legend.shootingcodetalker.utils.DatabaseUtil;
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

public class SearchResultAdapter extends BaseAdapter<SearchResultAdapter.ViewHolder> {

    private List<Result> resultList;
    private OnSearchItemClickListener listener;

    public void setListener(OnSearchItemClickListener listener) {
        this.listener = listener;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
        notifyDataSetChanged();
    }

    public void addItems(Result result){

        if (this.resultList==null){
            this.resultList=new ArrayList<>();
        }

        this.resultList.add(result);
//        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());

        View view=layoutInflater.inflate(R.layout.search_result_item,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);

        /**
         * 下载
         */
        viewHolder.downloadBtn.setOnClickListener(v -> {

            int position=viewHolder.getAdapterPosition();

            Result result=this.resultList.get(position);


            download(result);


        });

        /**
         * 收藏
         */
        viewHolder.imageView.setOnClickListener(v -> {

            int position=viewHolder.getAdapterPosition();

            likeClick(position);

        });

        /**
         * 点击详情
         */
        viewHolder.view.setOnClickListener(v -> {

            if (this.listener!=null&&this.resultList!=null){

                int position=viewHolder.getAdapterPosition();

                Result result=this.resultList.get(position);

                this.listener.click(viewHolder.view,result);//丢给外部处理

            }

        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(this.resultList==null) {

            return;
        }

        Result result=this.resultList.get(position);

        holder.title.setText(result.getTitle());

        holder.format.setText(result.getFormat());

        holder.language.setText(result.getLanguage());

        int id=result.getId();

        if (id<0){

            holder.imageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        }else {

            holder.imageView.setImageResource(R.drawable.ic_favorite_black_24dp);
        }

        int d=result.getDownload();

        if (d>0){//已下载

            holder.downloadBtn.setImageResource(R.drawable.ic_done_black_24dp);

            int color=ShootingApp.getContext().getResources().getColor(R.color.colorGreen);

            holder.downloadBtn.setBackgroundTintList(ColorStateList.valueOf(color));

        }else {

            holder.downloadBtn.setBackgroundResource(R.drawable.download);

            int color=ShootingApp.getContext().getResources().getColor(R.color.colorTeal);

            holder.downloadBtn.setBackgroundTintList(ColorStateList.valueOf(color));

        }

    }

    @Override
    public int getItemCount() {

        if (this.resultList!=null){
            return this.resultList.size();
        }

        return super.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        TextView title,format,language;

        FloatingActionButton downloadBtn;

        View view;


        ViewHolder(View itemView) {
            super(itemView);

            view=itemView;

            imageView=itemView.findViewById(R.id.like);

            title=itemView.findViewById(R.id.item_title);

            format=itemView.findViewById(R.id.file_format);

            language=itemView.findViewById(R.id.language);

            downloadBtn=itemView.findViewById(R.id.item_download);

        }
    }

    /**
     * 点击收藏or取消收藏，改变UI
     * @param position
     */
    private void likeClick(int position){

        if (this.resultList==null){
            return;
        }

        if (position<0||position>=this.resultList.size()){
            return;
        }

        Result result=this.resultList.get(position);

        Observable
                .create((ObservableOnSubscribe<Integer>) e -> {

                    int id=result.getId();

                    int s=-1;

                    if (id>0){//有id，表示已经收藏，取消收藏

                        s=DatabaseUtil.getDefault().removeLike(result);

                    }else {//无id，表示未收藏，收藏到数据库

                        s=DatabaseUtil.getDefault().saveLike(result);
                    }

                    e.onNext(s);

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
                        if (integer==0){//没删除成功

                            Toast.makeText(ShootingApp.getContext(), "取消收藏失败", Toast.LENGTH_SHORT).show();

                        }else {
                            result.setId(integer);
                        }


                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }
                });

    }

    /**
     * 下载并改变UI
     * @param result
     */
    private void download(Result result){

        Observable
                .create((ObservableOnSubscribe<Integer>) e -> {
                    int s=HtmlPagerUtil.download(result.getDownload_link(),result.getTitle());

                    e.onNext(s);
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

                        result.setDownload(integer);//标记已下载
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });

    }

    public void clearList(){
        if (this.resultList!=null){
            this.resultList.clear();
            notifyDataSetChanged();
        }
    }

    public void notifyItem(){

        Observable
                .create((ObservableOnSubscribe<Integer>) e -> {

                    if (this.resultList==null){
                        return;
                    }

                    for (Result result:this.resultList){

                        if (!DatabaseUtil.getDefault().isFileExists(result.getDownload_link())){
                            result.setDownload(1);
                        }

                        int d=DatabaseUtil.getDefault().queryTable(result.getDownload_link());

                        result.setId(d);

                    }

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

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        notifyDataSetChanged();
                        if (!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });

    }
}

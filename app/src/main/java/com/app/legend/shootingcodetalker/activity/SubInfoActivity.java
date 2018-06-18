package com.app.legend.shootingcodetalker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.adapter.FileAdapter;
import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.ISubActivity;
import com.app.legend.shootingcodetalker.presenter.SubPresenter;
import com.app.legend.shootingcodetalker.utils.FileUtil;
import com.app.legend.shootingcodetalker.utils.HtmlPagerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubInfoActivity extends BaseActivity<ISubActivity,SubPresenter> implements ISubActivity{


    private Toolbar toolbar;
    private ImageView like;
    private TextView title,language,format;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton downloadBtn;
    private Result result;
    private FileAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_info);

        getComponent();

        initToolbar();

        initFileList();

        initResult();

        click();

    }

    @Override
    protected void onResume() {
        super.onResume();

        autoChangeColor(this.toolbar);
    }

    @Override
    protected SubPresenter createPresenter() {
        return new SubPresenter(this);
    }



    private void getComponent(){

        toolbar=findViewById(R.id.info_toolbar);
        like=findViewById(R.id.info_like);
        title=findViewById(R.id.info_title);
        language=findViewById(R.id.info_language);
        format=findViewById(R.id.info_format);
        recyclerView=findViewById(R.id.info_file_list);
        downloadBtn=findViewById(R.id.info_download);

    }

    private void initResult(){
        Intent intent=getIntent();
        if (intent==null){
            return;
        }

        Result result=intent.getParcelableExtra("result");

        initCard(result);

    }

    private void initCard(Result result){

        if (result==null){
            return;
        }

        this.result=result;//保存

        int id=result.getId();
        if (id>0){
            this.like.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else {
            this.like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        this.title.setText(result.getTitle());
        this.format.setText(result.getFormat());
        this.language.setText(result.getLanguage());

        presenter.changeUI(result);

        presenter.prepareGetData(result);//获取数据

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(result.getTitle());
        }
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar(){

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> {

            supportFinishAfterTransition();

        });

    }

    /**
     * 初始化文件列表
     */
    private void initFileList(){

        linearLayoutManager=new LinearLayoutManager(SubInfoActivity.this);

        adapter=new FileAdapter();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        /**
         * 文件点击事件，弹出预览窗口
         */
        adapter.setOnFileClickListener((subFile, view) -> {

        });
    }

    private void click(){

        /**
         * 点击收藏or取消
         */
        like.setOnClickListener(v -> {

            presenter.toggle(this.result);

        });

        downloadBtn.setOnClickListener(v -> {

            presenter.download(this.result);

        });
    }

    @Override
    public void setData(SubFile subFile) {
        adapter.addSubFiles(subFile);

        runLayoutAnimation(this.recyclerView,R.anim.layout_animation_fall);
    }

    @Override
    public void notifyChange() {
//        adapter.notifyDataSetChanged();
        int id=result.getId();
        if (id>0){
            this.like.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else {
            this.like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    @Override
    public void changeBtn(int s) {

        if (s<0){
            return;
        }

        this.downloadBtn.setImageResource(R.drawable.ic_done_black_24dp);

        int color=getResources().getColor(R.color.colorGreen);

        this.downloadBtn.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    /**
     * 下拉加载动画效果
     * @param recyclerView
     * @param item
     */
    private void runLayoutAnimation(final RecyclerView recyclerView, final int item) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, item);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}

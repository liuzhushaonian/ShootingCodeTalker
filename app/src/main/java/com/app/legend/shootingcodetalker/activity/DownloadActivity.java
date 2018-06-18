package com.app.legend.shootingcodetalker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.adapter.DownloadAdapter;
import com.app.legend.shootingcodetalker.bean.DownloadFile;
import com.app.legend.shootingcodetalker.interfaces.IDownloadActivity;
import com.app.legend.shootingcodetalker.presenter.DownloadPresenter;

import java.util.List;

public class DownloadActivity extends BaseActivity<IDownloadActivity,DownloadPresenter> implements IDownloadActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView textView;
    private LinearLayoutManager linearLayoutManager;
    private DownloadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getComponent();
        initToolbar();
        initList();
        getData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(toolbar);
    }

    @Override
    protected DownloadPresenter createPresenter() {
        return new DownloadPresenter(this);
    }

    @Override
    public void setData(List<DownloadFile> data) {

        if (data==null||data.isEmpty()){
            textView.setVisibility(View.VISIBLE);

        }else {
            textView.setVisibility(View.GONE);
            adapter.setFileList(data);

        }
    }

    private void getComponent(){

        textView=findViewById(R.id.no_download_info);
        toolbar=findViewById(R.id.download_toolbar);
        recyclerView=findViewById(R.id.download_file_list);

    }

    private void initToolbar(){

        toolbar.setTitle("已下载");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(v -> {

            finish();

        });
    }

    private void initList(){

        linearLayoutManager=new LinearLayoutManager(this);
        adapter=new DownloadAdapter();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }

    private void getData(){
        presenter.getData();
    }
}

package com.app.legend.shootingcodetalker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.adapter.SearchResultAdapter;
import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.interfaces.ILikeActivity;
import com.app.legend.shootingcodetalker.presenter.LikePresenter;
import com.app.legend.shootingcodetalker.utils.ItemSpace;

import java.util.List;

public class LikeActivity extends BaseActivity<ILikeActivity,LikePresenter> implements ILikeActivity{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView textView;
    private LinearLayoutManager linearLayoutManager;
    private SearchResultAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        getComponent();
        initToolbar();
        initList();
        getData();
        slideHelper.setSlideActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(toolbar);
    }

    @Override
    protected LikePresenter createPresenter() {
        return new LikePresenter(this);
    }

    @Override
    public void setData(List<Result> results) {

        if (results==null||results.isEmpty()){

            textView.setVisibility(View.VISIBLE);

        }else {

            textView.setVisibility(View.GONE);
            adapter.setResultList(results);
        }

    }

    private void getComponent(){

        toolbar=findViewById(R.id.like_toolbar);
        recyclerView=findViewById(R.id.like_list);
        textView=findViewById(R.id.like_info);

    }

    private void initToolbar(){

        toolbar.setTitle("收藏");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

    }

    private void initList(){

        linearLayoutManager=new LinearLayoutManager(this);

        adapter=new SearchResultAdapter();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemSpace());

        adapter.setListener(this::openInfo);
    }

    private void getData(){
        presenter.getData();
    }

    private void openInfo(View view,Result result){

        Intent intent = new Intent(this, SubInfoActivity.class);

        intent.putExtra("result", result);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, getResources().getString(R.string.card_trans));
        startActivity(intent, options.toBundle());
    }
}
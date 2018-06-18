package com.app.legend.shootingcodetalker.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.adapter.SearchResultAdapter;
import com.app.legend.shootingcodetalker.bean.Result;
import com.app.legend.shootingcodetalker.interfaces.ISearchResultActivity;
import com.app.legend.shootingcodetalker.presenter.SearchResultPresenter;
import com.app.legend.shootingcodetalker.utils.ItemSpace;

import java.util.List;

public class SearchResultActivity extends BaseActivity<ISearchResultActivity,SearchResultPresenter>
        implements ISearchResultActivity {


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SearchResultAdapter adapter;
    private Toolbar toolbar;
    private String content;
    private EditText editText;
    private ImageView load;
    private LinearLayout loadLayout;
    private TextView loadText;
    ObjectAnimator objectAnimator;
    boolean isNoNet=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        getComponent();

        initToolbar();

        initList();

        initSearch();//实例化搜索

        click();


    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(this.toolbar);

        if (adapter!=null){
            adapter.notifyItem();
        }

    }

    @Override
    protected SearchResultPresenter createPresenter() {
        return new SearchResultPresenter(this);
    }


    /**
     * 设置数据到其中
     *
     * @param result
     */
    @Override
    public void addResultItem(Result result) {
        if (this.adapter != null) {

            closeLoadAnim();

            this.adapter.addItems(result);

            runLayoutAnimation(this.recyclerView,R.anim.layout_animation_fall_down);
        }
    }

    @Override
    public void setResult(List<Result> results) {

    }

    @Override
    public void showLoadInfo() {
        showLoadAnim();
    }

    @Override
    public void showNoNet() {
        showNoNetInfo();
    }

    @Override
    public void closeInfo() {
        closeLoadAnim();
    }

    @Override
    public void setNetStatus(boolean status) {
        this.isNoNet=status;
    }

    @Override
    public void alertWindows(String content) {

        showDialog(content);
    }

    @Override
    public void closeActivity() {
        supportFinishAfterTransition();
    }

    /**
     * 没有搜索到任何结果，直接提示信息
     */
    @Override
    public void showNoResult() {

        runOnUiThread(()->{

            if (loadLayout.getVisibility()== View.GONE){

                loadLayout.setVisibility(View.VISIBLE);
            }

            if (objectAnimator!=null){
                objectAnimator.cancel();
                objectAnimator.reverse();
            }

            load.setImageResource(R.drawable.no_net);

            loadText.setText(getText(R.string.no_result));


        });

    }

    /**
     * 显示弹窗确认是否继续搜索
     * @param content
     */
    private void showDialog(String content){

        presenter.showDialog(content,this);

    }


    //准备搜索
    private void initSearch() {

        Intent intent = getIntent();

        String content = intent.getStringExtra("content");
        if (content == null) {
            return;
        }



        this.content = content;

        this.editText.setText(content);

        toSearch(this.content);

    }

    //开始搜索
    private void toSearch(String content){


        presenter.getSearchResult(content);

    }

    /**
     * 获取组件
     */
    private void getComponent() {

        recyclerView = findViewById(R.id.result_list);
        toolbar = findViewById(R.id.result_toolbar);
        editText = findViewById(R.id.search_edit_s);
        load=findViewById(R.id.anim_load);
        loadLayout=findViewById(R.id.load_info);
        loadText=findViewById(R.id.load_text);
    }

    private void initToolbar() {

//        toolbar.setTitle(this.content);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> {

//            finish();

            supportFinishAfterTransition();
        });

    }


    private void initList() {

        linearLayoutManager = new LinearLayoutManager(this);

        adapter = new SearchResultAdapter();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemSpace());

        //item点击事件，打开详情页
        adapter.setListener((view, result) -> {

            Intent intent = new Intent(SearchResultActivity.this, SubInfoActivity.class);

            intent.putExtra("result", result);

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, view, getResources().getString(R.string.card_trans));
            startActivity(intent, options.toBundle());


        });


    }



    private void runLayoutAnimation(final RecyclerView recyclerView, final int item) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, item);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void showLoadAnim(){

        if (loadLayout.getVisibility()== View.GONE){

            loadLayout.setVisibility(View.VISIBLE);
        }

        load.setImageResource(R.drawable.load_anim);

        loadText.setText(getText(R.string.load_info));

        objectAnimator=ObjectAnimator.ofFloat(load,"rotation",360).setDuration(2000);

        objectAnimator.setRepeatMode(ValueAnimator.RESTART);

        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);

        objectAnimator.start();

    }

    /**
     * 显示断网信息
     */
    private void showNoNetInfo(){

        if (loadLayout.getVisibility()== View.GONE){

            loadLayout.setVisibility(View.VISIBLE);
        }

        if (objectAnimator!=null){
            objectAnimator.cancel();
        }

        load.setImageResource(R.drawable.no_net);

        loadText.setText(getText(R.string.no_network));

    }

    private void closeLoadAnim(){
        if (loadLayout.getVisibility()==View.VISIBLE){
            loadLayout.setVisibility(View.GONE);
        }

        if (objectAnimator!=null){
            objectAnimator.cancel();

        }
    }

    private void click(){

        loadLayout.setOnClickListener(v -> {

            if (!isNoNet){//非断网状态则不需要处理点击事件
                return;
            }

            if (this.content==null||this.content.isEmpty()){

                return;
            }

            toSearch(this.content);


        });

        editText.setOnEditorActionListener((v, actionId, event) -> {


            /**
             * 搜索事件
             * 点击输入法的搜索后执行
             */
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                String content = v.getText().toString();

                if (TextUtils.isEmpty(content)){
                    return false;
                }

                //消除之前的搜索结果，重新载入数据

                adapter.clearList();

                toSearch(content);//搜索

                editText.clearFocus();//取消焦点

                return true;
            }

            return false;
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                closeSoftKeybord();
            }

        });
    }


    /**
     * 关闭软键盘
     */
    private void closeSoftKeybord() {

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

    }

}
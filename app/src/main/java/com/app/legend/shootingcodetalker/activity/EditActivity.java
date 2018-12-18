package com.app.legend.shootingcodetalker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.IEditActivity;
import com.app.legend.shootingcodetalker.presenter.EditPresenter;
import com.app.legend.shootingcodetalker.utils.SlideHelper;

public class EditActivity extends BaseActivity<IEditActivity,EditPresenter> implements IEditActivity {

    private SubFile subFile;
    private Toolbar toolbar;
    private EditText editText;
    public static final String SUB="sub_file";//传输标志

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getComponent();

        initToolbar();

        getSubInfo();

        SlideHelper.getInstance().setSlideActivity(this);
    }

    private void getComponent(){

        toolbar=findViewById(R.id.edit_toolbar);
//        editText=findViewById(R.id.edit_view);

        textView=findViewById(R.id.text_info);

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(this.toolbar);
    }

    @Override
    protected EditPresenter createPresenter() {
        return new EditPresenter(this);
    }



    //解析后返回解析结果并显示
    @Override
    public void showPreview(String content) {

//        editText.setText(content);
//        editText.append(content+"\n");

        textView.append(content+"\n");

    }

    //获取传递过来的sub文件然后交给presenter解析并显示
    private void getSubInfo(){

        Intent intent=getIntent();
        if (intent==null){
            return;
        }

        this.subFile=intent.getParcelableExtra(SUB);

        if (toolbar!=null){
            toolbar.setTitle(subFile.getName());
        }

        presenter.preview(subFile);


    }

    private void initToolbar(){

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.preview_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.pre_download:

                if (this.subFile!=null){

                    presenter.download(subFile,this);

                }else {

                    Toast.makeText(this, "该文件无法下载", Toast.LENGTH_SHORT).show();
                }

                break;

        }

        return true;
    }
}

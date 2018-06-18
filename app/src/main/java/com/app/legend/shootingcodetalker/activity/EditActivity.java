package com.app.legend.shootingcodetalker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.SubFile;
import com.app.legend.shootingcodetalker.interfaces.IEditActivity;
import com.app.legend.shootingcodetalker.presenter.EditPresenter;

public class EditActivity extends BaseActivity<IEditActivity,EditPresenter> implements IEditActivity {

    private SubFile subFile;
    private Toolbar toolbar;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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



    @Override
    public void showPreview(String content) {

    }


}

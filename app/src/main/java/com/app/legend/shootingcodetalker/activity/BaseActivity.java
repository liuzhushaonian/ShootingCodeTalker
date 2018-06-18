package com.app.legend.shootingcodetalker.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.presenter.BasePresenter;
import com.app.legend.shootingcodetalker.utils.Conf;


public abstract class BaseActivity<V,T extends BasePresenter<V>> extends AppCompatActivity {


    protected T presenter;
    protected Toolbar toolbar;
    protected SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        sharedPreferences=getSharedPreferences(Conf.SHARE_NAME,MODE_PRIVATE);

        presenter=createPresenter();
        presenter.attachView((V) this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(toolbar);//自动获取颜色并设置上
    }

    protected abstract T createPresenter();


    protected void autoChangeColor(Toolbar toolbar){

        int defaultColor=getResources().getColor(R.color.colorTeal);

        int color=sharedPreferences.getInt(Conf.COLOR,defaultColor);

        if (toolbar!=null) {
            toolbar.setBackgroundColor(color);
        }

    }

    protected void saveColor(int color){

        sharedPreferences.edit().putInt(Conf.COLOR,color).apply();

    }

    protected int getThemeColor(){

        int defaultColor=getResources().getColor(R.color.colorTeal);

        int color=sharedPreferences.getInt(Conf.COLOR,defaultColor);

        return color;
    }


}

package com.app.legend.shootingcodetalker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.presenter.BasePresenter;
import com.app.legend.shootingcodetalker.utils.Conf;
import com.app.legend.shootingcodetalker.utils.SlideHelper;


public abstract class BaseActivity<V,T extends BasePresenter<V>> extends AppCompatActivity {


    protected T presenter;
    protected Toolbar toolbar;
    protected SharedPreferences sharedPreferences;
    protected SlideHelper slideHelper;


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

        slideHelper=SlideHelper.getInstance();

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

    protected void startCropImage(Uri uri, int w, int h,int code) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为1:1
        intent.putExtra("aspectX", w);
        intent.putExtra("aspectY", h);
        //输出图片的宽高均为150
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);

        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", false);

        intent.putExtra("outImage", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection",true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent, code);
    }

}

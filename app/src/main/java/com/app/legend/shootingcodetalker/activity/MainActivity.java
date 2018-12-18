package com.app.legend.shootingcodetalker.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.interfaces.IMainActivity;
import com.app.legend.shootingcodetalker.presenter.BasePresenter;
import com.app.legend.shootingcodetalker.presenter.MainPresenter;
import com.app.legend.shootingcodetalker.utils.Conf;
import com.app.legend.shootingcodetalker.utils.SlideHelper;

import java.io.File;

public class MainActivity extends BaseActivity<IMainActivity, MainPresenter> implements IMainActivity {


    private Toolbar toolbar;
    private EditText editText;
    private LinearLayout linearLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String keyword = "";
    private ImageView searchIcon;
    private long mExitTime = 0;
    private ImageView header;
    private TextView hongbao;

    private static final String[] permissionStrings =
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getComponent();

        initToolbar();

        initSearchView();

        initHeader();

        click();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(this.toolbar);
        changeSearchUi();
    }


    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }


    /**
     * 获取组件
     */
    private void getComponent() {

        toolbar = findViewById(R.id.main_toolbar);

        editText = findViewById(R.id.search_edit);

        linearLayout = findViewById(R.id.trans);

        drawerLayout = findViewById(R.id.main_draw_layout);

        navigationView = findViewById(R.id.left_menu);
        searchIcon = findViewById(R.id.search_icon);
        this.header = navigationView.getHeaderView(0).findViewById(R.id.header_image);

        hongbao=findViewById(R.id.hongbao);
    }

    /**
     * 实例化toolbar
     */
    private void initToolbar() {

        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {

            drawerLayout.openDrawer(Gravity.START,true);



        });

        navigationView.setItemIconTintList(null);
    }

    /**
     * 实例化搜索框
     */
    private void initSearchView() {

        editText.setOnEditorActionListener((v, actionId, event) -> {


            /**
             * 搜索事件
             * 点击输入法的搜索后执行
             */
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                String content = v.getText().toString();

                presenter.query(content);

                editText.clearFocus();

                return true;
            }

            return false;
        });

    }

    /**
     * 实例化头部图片
     */
    private void initHeader() {

        String path = getApplicationContext().getFilesDir().getAbsolutePath();

        File file = new File(path + "/" + Conf.HEADER);

        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            this.header.setImageBitmap(bitmap);
        }

    }

    /**
     * 检查一波权限
     *
     * @param content
     */
    @Override
    public void startActivity(String content) {

        this.keyword = content;
        checkPermission(content);
    }

    //检查完权限后才可以打开下一个搜索页面
    private void openSearch(String string) {

        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);

        intent.putExtra("content", string);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, linearLayout, getResources().getString(R.string.search));
        startActivity(intent, options.toBundle());

        this.keyword = "";//清除

    }

    private void click() {

        navigationView.setNavigationItemSelectedListener(item -> {


            switch (item.getItemId()) {


                case R.id.change_color:

                    openColorActivity();//打开颜色选择界面

                    break;

                case R.id.exit:

                    exitApp();

                    break;

                case R.id.setting:

                    openPreference();
                    break;

                case R.id.about_app:

                    showAbout();


                    break;

                case R.id.download_file:

                    openDownload();

                    break;

                case R.id.menu_like:

                    openLike();

                    break;


            }

            drawerLayout.closeDrawer(GravityCompat.START);


            return true;
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {

            if (!hasFocus) {
                closeSoftKeybord();
            }

        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                closeSoftKeybord();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        this.header.setOnClickListener(v -> {


            checkForOpenFile();

        });

        this.header.setOnLongClickListener(v -> {

            this.header.setImageResource(R.drawable.bg);

            Toast.makeText(this, "已恢复默认图~", Toast.LENGTH_SHORT).show();

            return true;
        });

        hongbao.setOnClickListener(v->{

            presenter.showHongbao(this);

        });

    }

    /**
     * 打开相册权限
     */
    private void checkForOpenFile() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, permissionStrings[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionStrings[0]}, 601);

        } else {
            openAlbum();
        }


    }


    /**
     * 打开相册
     */
    private void openAlbum() {


        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 201);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 201://获取图片

                if (data == null) {
                    return;
                }

                startCropImage(data.getData(), this.header.getWidth(), this.header.getHeight(), 301);

                break;
            case 301://剪切图片

                if (data == null) {
                    return;
                }

                presenter.saveAndSetImage(data.getData(), this.header);

                break;

            default:

                super.onActivityResult(requestCode, resultCode, data);

                break;
        }


    }

    /**
     * 检查权限
     */
    private void checkPermission(String content) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, permissionStrings[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionStrings[0]}, 1000);

        } else {
            openSearch(content);//已经拥有权限，则进行搜索

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (!this.keyword.isEmpty()) {


                        new Thread() {

                            @Override
                            public void run() {
                                super.run();

                                try {
                                    sleep(100);

                                    runOnUiThread(() -> {
                                        openSearch(keyword);
                                    });

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                    }

                } else {


                    Toast.makeText(this, "无法获取权限，请赋予相关权限", Toast.LENGTH_SHORT).show();
                }


                break;

            case 601://打开相册权限

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();

                } else {

                    Toast.makeText(this, "无法获取权限，请赋予相关权限", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * 打开颜色选择
     */
    private void openColorActivity() {

        Intent intent = new Intent(MainActivity.this, ColorActivity.class);

        startActivity(intent);
    }

    /**
     * 修改边框颜色与图标颜色，与主题颜色一致
     */
    private void changeSearchUi() {

        int color = getThemeColor();

        int s = getResources().getDimensionPixelOffset(R.dimen.stroke);

        GradientDrawable drawable = (GradientDrawable) linearLayout.getBackground();

        drawable.setStroke(s, color);

        searchIcon.setImageTintList(ColorStateList.valueOf(color));

    }

    private void closeSoftKeybord() {

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (drawerLayout.isDrawerOpen(Gravity.START)){
                drawerLayout.closeDrawers();
            }else if ((System.currentTimeMillis() - mExitTime) > 2000) {//
                // 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    /**
     * 关闭APP
     */
    private void exitApp() {

        finish();

        System.exit(0);
    }

    /**
     * 打开设置界面
     */
    private void openPreference() {

        Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);

        startActivity(intent);

    }

    private void openDownload() {

        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);

        startActivity(intent);
    }

    private void openLike() {

        Intent intent = new Intent(MainActivity.this, LikeActivity.class);

        startActivity(intent);
    }

    private void showAbout() {

        presenter.showAbout(this);

    }

}

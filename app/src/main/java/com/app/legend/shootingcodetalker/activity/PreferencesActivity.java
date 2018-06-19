package com.app.legend.shootingcodetalker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.fragment.ShootingPreferenceFragment;
import com.app.legend.shootingcodetalker.interfaces.IPreferencesActivity;
import com.app.legend.shootingcodetalker.presenter.PreferencesPresenter;



public class PreferencesActivity extends BaseActivity<IPreferencesActivity,PreferencesPresenter> implements IPreferencesActivity{


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getComponent();

        initToolbar();

        initFragment();


        slideHelper.setSlideActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoChangeColor(toolbar);
    }

    @Override
    protected PreferencesPresenter createPresenter() {
        return new PreferencesPresenter(this);
    }

    private void getComponent(){

        this.toolbar=findViewById(R.id.pre_toolbar);

    }

    private void initToolbar(){

        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

    }

    /**
     * 添加fragment设置界面
     */
    private void initFragment(){

        ShootingPreferenceFragment fragment=new ShootingPreferenceFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_content,fragment)
                .commit();

    }


}

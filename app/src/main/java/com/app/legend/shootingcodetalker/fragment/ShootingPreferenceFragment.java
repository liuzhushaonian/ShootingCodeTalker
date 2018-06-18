package com.app.legend.shootingcodetalker.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.utils.Conf;

public class ShootingPreferenceFragment extends PreferenceFragment {


    private SwitchPreference switchPreference;
    private EditTextPreference downloadPathEditTextPreference,searchCountEditTextPreference;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        switchPreference= (SwitchPreference) findPreference("switch_net");

        searchCountEditTextPreference= (EditTextPreference) findPreference("search_count");

        downloadPathEditTextPreference= (EditTextPreference) findPreference("local");

        sharedPreferences=getActivity().getSharedPreferences(Conf.SHARE_NAME, Context.MODE_PRIVATE);

        initPreference();

        preference();
    }

    /**
     * 初始化设置，修改显示内容
     */
    private void initPreference(){

        String path=sharedPreferences.getString(Conf.DOWNLOAD_PATH_NAME,Conf.DEFAULT_DOWNLOAD_PATH);

        downloadPathEditTextPreference.setSummary(path);

        int count=sharedPreferences.getInt(Conf.SEARCH_COUNT,15);

        searchCountEditTextPreference.setSummary(""+count);

        boolean can=sharedPreferences.getBoolean(Conf.IF_NOT_WIFI_CAN_USE,true);

        switchPreference.setDefaultValue(can);
    }


    private void preference(){
        switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {

            saveWifi((Boolean) newValue);

            return true;
        });


        searchCountEditTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {


            return saveSearchCount((String) newValue);
        });

        downloadPathEditTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {

            return savePath((String) newValue);
        });

    }

    private boolean savePath(String path){

        if (path==null|| TextUtils.isEmpty(path)){

            Toast.makeText(getActivity(),"路径格式不正确",Toast.LENGTH_SHORT).show();

            return false;
        }

        if (!path.startsWith("/")||!path.endsWith("/")){

            Toast.makeText(getActivity(),"路径格式不正确",Toast.LENGTH_SHORT).show();

            return false;
        }

        sharedPreferences.edit().putString(Conf.DOWNLOAD_PATH_NAME,path).apply();

        downloadPathEditTextPreference.setSummary(path);//修改底部显示内容

        Toast.makeText(getActivity(), "下载路径已修改", Toast.LENGTH_SHORT).show();

        return true;

    }

    private void saveWifi(boolean can){

        sharedPreferences.edit().putBoolean(Conf.IF_NOT_WIFI_CAN_USE,can).apply();

    }

    private boolean saveSearchCount(String s){

        Integer count=Integer.parseInt(s);

        if (count<=0||count>100){

            Toast.makeText(getActivity(), "数字不合理", Toast.LENGTH_SHORT).show();

            return false;
        }

        sharedPreferences.edit().putInt(Conf.SEARCH_COUNT,count).apply();

        searchCountEditTextPreference.setSummary(""+count);

        return true;
    }

}

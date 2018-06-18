package com.app.legend.shootingcodetalker.presenter;

import com.app.legend.shootingcodetalker.interfaces.IPreferencesActivity;

public class PreferencesPresenter extends BasePresenter<IPreferencesActivity> {

    private IPreferencesActivity activity;

    public PreferencesPresenter(IPreferencesActivity activity) {
        attachView(activity);

        this.activity=getView();
    }


}

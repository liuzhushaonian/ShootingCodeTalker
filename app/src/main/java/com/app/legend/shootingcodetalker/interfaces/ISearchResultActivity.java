package com.app.legend.shootingcodetalker.interfaces;

import com.app.legend.shootingcodetalker.bean.Result;

import java.util.List;

public interface ISearchResultActivity {

    void addResultItem(Result result);

    void setResult(List<Result> results);

    void showLoadInfo();

    void showNoNet();

    void closeInfo();

    void setNetStatus(boolean status);

    void alertWindows(String content);

    void closeActivity();

    void showNoResult();

}

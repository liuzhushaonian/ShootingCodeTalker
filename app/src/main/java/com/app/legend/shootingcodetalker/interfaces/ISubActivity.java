package com.app.legend.shootingcodetalker.interfaces;

import com.app.legend.shootingcodetalker.bean.SubFile;

public interface ISubActivity {

    void setData(SubFile subFile);

    void notifyChange();

    void changeBtn(int s);

}

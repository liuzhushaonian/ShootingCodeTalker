package com.app.legend.shootingcodetalker.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.adapter.ColorAdapter;
import com.app.legend.shootingcodetalker.bean.Color;
import com.app.legend.shootingcodetalker.interfaces.IColorPresenter;
import com.app.legend.shootingcodetalker.presenter.ColorPresenter;
import com.app.legend.shootingcodetalker.utils.RippleView;

import java.util.List;

public class ColorActivity extends BaseActivity<IColorPresenter,ColorPresenter> implements IColorPresenter {



    private RecyclerView recyclerView;
    private ColorAdapter colorAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private RippleView rippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        getComponent();
        initList();
        initToolbar();

        getColorData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        rippleView.setBackgroundColor(getThemeColor());
    }

    @Override
    protected ColorPresenter createPresenter() {
        return new ColorPresenter(this);
    }


    private void getComponent(){

        recyclerView=findViewById(R.id.color_list);
        rippleView=findViewById(R.id.color_bg);
        toolbar=findViewById(R.id.color_toolbar);
    }

    private void initList(){
        linearLayoutManager=new LinearLayoutManager(this);
        colorAdapter=new ColorAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(colorAdapter);

        /**
         * 选择颜色
         */
        colorAdapter.setListener((v, color) -> {
            int[] position=new int[2];

            v.getLocationOnScreen(position);

            int x=position[0];
            int y=position[1];

            int w=getResources().getDisplayMetrics().widthPixels;

            double d=y*y+w*w;

            int limit= (int) Math.sqrt(d);

            rippleView.startRipper(x,y,color.getColor(),limit);

            saveColor(color.getColor());//保存颜色
        });

    }

    private void initToolbar(){

        toolbar.setTitle("选择颜色");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });


    }


    private void getColorData(){
        presenter.getColorData();
    }



    @Override
    public void setData(List<Color> colors) {

        this.colorAdapter.setColorList(colors);
    }
}

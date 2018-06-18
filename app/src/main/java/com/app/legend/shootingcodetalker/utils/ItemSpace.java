package com.app.legend.shootingcodetalker.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.legend.shootingcodetalker.R;

public class ItemSpace extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);

        int space=ShootingApp.getContext().getResources().getDimensionPixelSize(R.dimen.item_space);

        int position=parent.getChildAdapterPosition(view);

        if (position==0){

            outRect.top=ShootingApp.getContext().getResources().getDimensionPixelOffset(R.dimen.half_margin);
        }

        outRect.bottom=space;


    }
}

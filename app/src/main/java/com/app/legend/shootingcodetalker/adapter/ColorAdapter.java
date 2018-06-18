package com.app.legend.shootingcodetalker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.app.legend.shootingcodetalker.R;
import com.app.legend.shootingcodetalker.bean.Color;
import com.app.legend.shootingcodetalker.interfaces.ColorItemClickListener;
import com.app.legend.shootingcodetalker.utils.CircleView;

import java.util.List;

/**
 *
 * Created by legend on 2018/2/23.
 */

public class ColorAdapter extends BaseAdapter<ColorAdapter.ViewHolder> {


    private List<Color> colorList;
    private ColorItemClickListener listener;

    public void setListener(ColorItemClickListener listener) {
        this.listener = listener;
    }

    public void setColorList(List<Color> colorList) {
        this.colorList = colorList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View view=inflater.inflate(R.layout.color_list_item,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);

        viewHolder.view.setOnClickListener(v -> {
            if (this.listener!=null){

                int position=viewHolder.getAdapterPosition();
                Color color=colorList.get(position);
                changeInfo(color);
                listener.itemClick(v,color);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (this.colorList!=null){

            Color color=colorList.get(position);
            holder.circleView.setBackgroundColor(color.getColor());
            holder.textView.setText(color.getName());

            if (color.getIs_use()==1){
                holder.info.setVisibility(View.VISIBLE);
                holder.info.setTextColor(color.getColor());
            }else {
                holder.info.setVisibility(View.GONE);
            }
        }else {
            super.onBindViewHolder(holder, position);
        }


    }

    @Override
    public int getItemCount() {
        if (this.colorList!=null){
            return colorList.size();
        }
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleView circleView;
        TextView textView,info;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            this.circleView=itemView.findViewById(R.id.color_view);
            this.textView=itemView.findViewById(R.id.color_name);
            this.info=itemView.findViewById(R.id.color_info);
        }
    }


    private void changeInfo(Color color){
        for (Color c:colorList){
            if (!c.equals(color)){
                c.setIs_use(0);
            }

            if (c.equals(color)){
                c.setIs_use(1);
            }
        }

        notifyDataSetChanged();
    }


}

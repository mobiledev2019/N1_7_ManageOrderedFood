package com.example.omrproject.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omrproject.Interface.ItemClickListener;
import com.example.omrproject.R;

public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemClickListener itemClickListener;

    public TextView table_name;
    public LinearLayout table_layout;
    public ImageView table_img;
    public TableViewHolder(View itemView){
        super(itemView);

        table_name = (TextView) itemView.findViewById(R.id.table_name);
        table_layout = (LinearLayout) itemView.findViewById(R.id.table_layout);
        table_img = (ImageView) itemView.findViewById(R.id.table_img);

        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}

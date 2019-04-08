package com.example.omrproject.ViewHolder;



import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.omrproject.Interface.ItemClickListener;

import com.example.omrproject.Model.Order;
import com.example.omrproject.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    public List<Order> listFoods = new ArrayList<>();
    private Context context;

    public OrderAdapter(List<Order> listFoods, Context context){
        this.listFoods = listFoods;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.order_item, viewGroup, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int position) {

        TextDrawable drawable = TextDrawable.builder().buildRound("" + listFoods.get(position).getQuantity(), Color.RED);
        orderViewHolder.img_order_count.setImageDrawable(drawable);
        Locale locale = new Locale ("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listFoods.get(position).getPrice()))*(Integer.parseInt(listFoods.get(position).getQuantity()));
        orderViewHolder.order_item_price.setText(fmt.format(price));
        orderViewHolder.order_item_name.setText(listFoods.get(position).getFoodName());
    }

    @Override
    public int getItemCount() {
        return listFoods.size();
    }

    public void removeItem(int position) {
        listFoods.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position) {
        listFoods.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView order_item_name, order_item_price;
        public ImageView img_order_count;

        public RelativeLayout viewBackground, viewForeground;
        private ItemClickListener itemClickListener;

        public void setOrder_item_name(TextView order_item_name) {
            this.order_item_name = order_item_name;


        }

        public OrderViewHolder (View itemView){
            super(itemView);

            order_item_name = (TextView) itemView.findViewById(R.id.order_item_name);
            order_item_price = (TextView) itemView.findViewById(R.id.order_item_price);
            img_order_count = (ImageView) itemView.findViewById(R.id.img_order_count);

            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

        }
    }
}

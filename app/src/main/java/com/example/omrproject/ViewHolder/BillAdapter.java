package com.example.omrproject.ViewHolder;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omrproject.Model.Order;
import com.example.omrproject.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


class BillViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView txtBillFoodName, txtBillFoodPrice, txtBillFoodQuantity, txtBillFoodPay;

    public BillViewHolder(View itemView){

        super(itemView);
        txtBillFoodName = (TextView) itemView.findViewById(R.id.txtBillFoodName);
        txtBillFoodPrice = (TextView) itemView.findViewById(R.id.txtBillFoodPrice);
        txtBillFoodQuantity = (TextView) itemView.findViewById(R.id.txtBillFoodQuantity);
        txtBillFoodPay = (TextView) itemView.findViewById(R.id.txtBillFoodPay);

        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

    }
}

public class BillAdapter extends RecyclerView.Adapter<BillViewHolder>{

    private List<Order> listFoods = new ArrayList<>();
    private Context context;

    public BillAdapter(List<Order> listFoods, Context context){
        this.listFoods = listFoods;
        this.context = context;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int postition) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.order_bill_item, viewGroup, false);
        return new BillViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder billViewHolder, int position) {
        System.out.println(listFoods.size());
        billViewHolder.txtBillFoodName.setText(listFoods.get(position).getFoodName());
        billViewHolder.txtBillFoodQuantity.setText(listFoods.get(position).getQuantity());
        billViewHolder.txtBillFoodPrice.setText(listFoods.get(position).getPrice());

        Locale locale = new Locale ("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int pay = (Integer.parseInt(listFoods.get(position).getPrice()))*(Integer.parseInt(listFoods.get(position).getQuantity()));

        billViewHolder.txtBillFoodPay.setText(fmt.format(pay));
    }

    @Override
    public int getItemCount() {
        return listFoods.size();
    }
}

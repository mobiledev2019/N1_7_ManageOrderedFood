package com.example.omrproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.omrproject.Common.Common;
import com.example.omrproject.Database.DBOrder;
import com.example.omrproject.Model.Order;
import com.example.omrproject.ViewHolder.OrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListOrder extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    TextView txtTotalPrice;
    Button btnOrder;
    Button btnPay;

    String tableId = "";


    FirebaseDatabase database;
    DatabaseReference tables;

    @Override
    protected void onResume(){
        super.onResume();
        tableId = Common.currentTable;
        loadlistOrder(tableId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);

        database  = FirebaseDatabase.getInstance();
        tables = database.getReference("Tables");

        //init
        txtTotalPrice = (TextView) findViewById(R.id.total_price);
        btnOrder = (Button) findViewById(R.id.btnOrder);
        btnPay = (Button) findViewById(R.id.btnPay);

        tableId = Common.currentTable;
        //set event for button
        if(!tableId.isEmpty()) {
            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOptionDialog();
                }
            });
            btnPay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent billIntent = new Intent(ListOrder.this, BillView.class);
                    startActivity(billIntent);
                }
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.list_order);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadlistOrder(tableId);

    }

    private void showOptionDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListOrder.this);
        alertDialog.setTitle("Tìm món");
        alertDialog.setMessage("Chọn chế độ tìm món: ");

        alertDialog.setPositiveButton("THEO TÊN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent searchFood = new Intent(ListOrder.this, SearchFood.class);
                searchFood.putExtra("searchMode", "byname");
                startActivity(searchFood);
            }
        });

        alertDialog.setNeutralButton("DANH MỤC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent home = new Intent(ListOrder.this, Home.class);
                startActivity(home);
            }
        });

        alertDialog.setNegativeButton("THEO MÃ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent searchFood = new Intent(ListOrder.this, SearchFood.class);
                searchFood.putExtra("searchMode", "byid");
                startActivity(searchFood);
            }
        });
        alertDialog.show();
    }

    private void loadlistOrder(String tableId) {

        final DatabaseReference foods = tables.child(tableId).child("foods");
        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    List<Order> orders = new ArrayList<>();
                    OrderAdapter adapter;
                    int total = 0;
                    for(DataSnapshot foodSnapshot: dataSnapshot.getChildren()){
                        Order order = foodSnapshot.getValue(Order.class);
                        orders.add(order);
                        total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
                    }
                    adapter = new OrderAdapter(orders, ListOrder.this);
                    recyclerView.setAdapter(adapter);

                    //if nothing, disable button pay
                    if(total==0){
                        btnPay.setEnabled(false);
                        btnPay.setBackgroundResource(R.drawable.rectangle_rounded);
                        btnPay.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    }
                    else{
                        btnPay.setEnabled(true);
                        btnPay.setBackgroundResource(R.drawable.rectangle_rounded);
                        btnPay.setBackgroundColor(Color.parseColor("#f17e7e"));
                    }
                    //set total
                    Locale locale = new Locale ("en", "US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}

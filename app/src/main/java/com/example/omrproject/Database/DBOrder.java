package com.example.omrproject.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.omrproject.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBOrder {
    FirebaseDatabase database;
    DatabaseReference tables;
    String lastOrderId = "";

    public DBOrder(){
        this.database  = FirebaseDatabase.getInstance();
        this.tables = database.getReference("Tables");
    }

    public List<Order> getOrders(String tableId){
        final List<Order> listFoods = new ArrayList<>();
        final DatabaseReference foods = tables.child(tableId).child("foods");
        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                        for(DataSnapshot foodSnapshot: dataSnapshot.getChildren()){
                            Order order = foodSnapshot.getValue(Order.class);
                            listFoods.add(order);
                        }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return listFoods;
    }
    public void addOrder(String tableId, final Order order){
            final DatabaseReference foods = tables.child(tableId).child("foods");
            foods.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int size = (int) dataSnapshot.getChildrenCount() + 1;
                    foods.child(size+"").setValue(order);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }

    public void deleteOrder(String tableId){
        tables.child(tableId).child("foods").setValue(null);
    }


}

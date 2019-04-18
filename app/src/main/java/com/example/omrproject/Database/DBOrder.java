package com.example.omrproject.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.omrproject.Model.Order;
import com.example.omrproject.Model.Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    public void isUsed(final String tableId){
        tables.child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Table table = dataSnapshot.getValue(Table.class);
                    String newTCS = table.getType() + table.getNumberOfSeat() + "1";
                    String newCS = table.getNumberOfSeat() + "1";
                    dataSnapshot.getRef().child("typeCapSt").setValue(newTCS);
                    dataSnapshot.getRef().child("capSt").setValue(newCS);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void unUsed(String tableId){
        tables.child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Table table = dataSnapshot.getValue(Table.class);
                String newTCS = table.getType() + table.getNumberOfSeat() + "0";
                String newCS = table.getNumberOfSeat() + "0";
                dataSnapshot.getRef().child("typeCapSt").setValue(newTCS);
                dataSnapshot.getRef().child("capSt").setValue(newCS);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addOrder(final String tableId, final Order order){

        final DatabaseReference foods = tables.child(tableId).child("foods");
        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exist = false;
                for (DataSnapshot food : dataSnapshot.getChildren()){
                    Order orderedFood = food.getValue(Order.class);
                    if(orderedFood.getFoodId().equalsIgnoreCase(order.getFoodId())){
                        int newQuantity = Integer.parseInt(food.child("quantity").getValue().toString()) + Integer.parseInt(order.getQuantity());
                        food.getRef().child("quantity").setValue(newQuantity+"");
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    int size = (int) dataSnapshot.getChildrenCount() + 1;
                    if(size==1) isUsed(tableId);
                    foods.child(size+"").setValue(order);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void deleteOrder(String tableId){
        unUsed(tableId);
        tables.child(tableId).child("foods").setValue(null);
    }

    public void deleteOrderedFood(final String tableId, int position){
        tables.child(tableId).child("foods").child(position+"").setValue(null)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tables.child(tableId).child("foods").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //update
                        int id = 1;
                        if(dataSnapshot.getChildrenCount()==0){
                            unUsed(tableId);
                            return;
                        }
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            tables.child(tableId).child("foods").child(id+"").setValue(snapshot.getValue(Order.class));
                            id++;
                            tables.child(tableId).child("foods").child(id+"").setValue(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


}

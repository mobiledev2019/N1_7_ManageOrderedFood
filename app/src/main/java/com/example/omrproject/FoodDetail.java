package com.example.omrproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.omrproject.Common.Common;
import com.example.omrproject.Database.DBOrder;
import com.example.omrproject.Model.Food;
import com.example.omrproject.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;

    CoordinatorLayout layout_fooddetail;
    Food currentFood;
    String tableId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");


        //Init view;
        layout_fooddetail = (CoordinatorLayout) findViewById(R.id.layout_fooddetail);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        tableId = Common.currentTable;
        btnCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new DBOrder().addOrder(tableId,new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));
                showSnackBar("Thêm" + currentFood.getName() + "x" + numberButton.getNumber()+". Xem danh sách món ăn đã đặt?");
//                Toast.makeText(FoodDetail.this, "Thêm" + currentFood.getName() + "x" + numberButton.getNumber()+". Xem danh sách món ăn đã đặt?", Toast.LENGTH_SHORT).show();
            }
        });
        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_img = (ImageView) findViewById(R.id.food_img);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleColor(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_food_detail);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Get FoodId

        if(getIntent()!=null){
            foodId = getIntent().getStringExtra("foodId");
        }
        if(!foodId.isEmpty() && !tableId.isEmpty()){
            getDetailFood(foodId);
        }
    }

    private void getDetailFood(String foodId){
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                //Set image
                Picasso.with(getBaseContext()).load(currentFood.getImg()).into(food_img);
//                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showSnackBar(String notify) {
        final Snackbar snackbar = Snackbar
                .make(layout_fooddetail, notify, Snackbar.LENGTH_LONG);
        snackbar.setAction("TỚI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                Intent listOrder = new Intent(FoodDetail.this, ListOrder.class);
                listOrder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(listOrder);
                finish();
                snackbar.dismiss();

            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}

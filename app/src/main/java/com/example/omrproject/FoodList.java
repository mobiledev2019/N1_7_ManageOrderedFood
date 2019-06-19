package com.example.omrproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.omrproject.Common.Common;
import com.example.omrproject.Database.DBOrder;
import com.example.omrproject.Interface.ItemClickListener;
import com.example.omrproject.Model.Food;
import com.example.omrproject.Model.Order;
import com.example.omrproject.ViewHolder.FoodViewHolder;
import com.example.omrproject.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    RelativeLayout relativeLayout;
    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Init database
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_list_food);

        //RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        int resId = R.anim.layout_animation_right_to_left;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(FoodList.this, resId);
        recyclerView.setLayoutAnimation(animation);
        //Get itent
//        if(getIntent()!=null){
//            categoryId = getIntent().getStringExtra("categoryId");
//        }
        categoryId = Common.currentCategory;
        if(!categoryId.isEmpty()){
            loadListFood(categoryId);
        }

    }

    private void loadListFood(String categoryId){
        //Get foodList by categoryId
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, final int position, @NonNull final Food model) {
                holder.food_name.setText(model.getName());
                final ElegantNumberButton numberButton = (ElegantNumberButton) holder.numberButton;
                holder.btnAddFood.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        try{
                            new DBOrder().addOrder(Common.currentTable,new Order(
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    numberButton.getNumber(),
                                    model.getPrice(),
                                    model.getDiscount()
                            ));
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }finally{
                            showSnackBar("Thêm" + model.getName() + "x" + numberButton.getNumber()+". Xem danh sách món ăn đã đặt?");
                        }

//                        showSnackBar("Thêm" + model.getName() + "x" + numberButton.getNumber()+". Xem danh sách món ăn đã đặt?");
//                        Toast.makeText(FoodList.this, "Thêm" + model.getName() + "x" + numberButton.getNumber(), Toast.LENGTH_SHORT).show();
                    }
                });
                Picasso.with(getBaseContext()).load(model.getImg()).into(holder.food_img);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start FoodDetail Activity
                        Intent foodDetail = new Intent (FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item, viewGroup, false);
                return new FoodViewHolder(view);
            }

        };
//        recyclerView.setAdapter(adapter);
        runLayoutAnimation(recyclerView, adapter);
    }

    private void showSnackBar(String notify) {
        final Snackbar snackbar = Snackbar
                .make(relativeLayout, notify, Snackbar.LENGTH_LONG);
        snackbar.setAction("TỚI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                Intent listOrder = new Intent(FoodList.this, ListOrder.class);
                listOrder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(listOrder);
                finish();
                snackbar.dismiss();

            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_right_to_left);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }



}

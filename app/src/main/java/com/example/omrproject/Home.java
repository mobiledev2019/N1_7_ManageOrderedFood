package com.example.omrproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omrproject.Common.Common;
import com.example.omrproject.Interface.ItemClickListener;
import com.example.omrproject.Model.Category;
import com.example.omrproject.Model.Food;
import com.example.omrproject.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = Home.class.getSimpleName();
    FirebaseDatabase database;
    DatabaseReference category;


    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    Button btnNoSignOut, btnDoSignOut;
    String tableId = "";

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
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set name for staff
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentStaff.getFullName());

        //Load menu
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        //Get table ID
        tableId = Common.currentTable;
        if(tableId!=null && tableId!=""){
            loadMenu();
        }

    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_signout, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.show();
        btnNoSignOut = (Button) dialogView.findViewById(R.id.btnNoSignOut);
        btnDoSignOut = (Button) dialogView.findViewById(R.id.btnDoSignOut);

        btnDoSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signIn = new Intent(Home.this, SignIn.class);
                signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signIn);
                finish();
                alertDialog.dismiss();
            }
        });
        btnNoSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNoSignOut.setTextColor(Color.parseColor("#ffffff"));
                btnNoSignOut.getBackground().setColorFilter(Color.parseColor("#f17e7e"), PorterDuff.Mode.SRC_ATOP);
                alertDialog.dismiss();
            }
        });

        btnNoSignOut.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnNoSignOut.setTextColor(Color.parseColor("#ffffff"));
                    btnNoSignOut.getBackground().setColorFilter(Color.parseColor("#f17e7e"), PorterDuff.Mode.SRC_ATOP);
                }
                return false;
            }
        });

    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(category, Category.class).build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                    holder.txtMenuName.setText(model.getName());
                    Uri img = Uri.parse("android.resource://com.example.omrproject/" + model.getImg());
                    Picasso.with(getBaseContext()).load(img).into(holder.imageView);
                    final Category clickItem = model;
                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            // Get categoryId and send to FoodlistActivity
                            Intent foodList = new Intent (Home.this, FoodList.class);
                            // Get key of this item
                            Common.currentCategory = adapter.getRef(position).getKey();
                            foodList.putExtra("categoryId" , adapter.getRef(position).getKey());
                            startActivity(foodList);
                        }
                    });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item, viewGroup, false);

                return new MenuViewHolder(view);
            }
        };
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showSignOutDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent tableList = new Intent(Home.this, TableList.class);
            startActivity(tableList);
        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if(id == R.id.nav_log_out) {
            showSignOutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

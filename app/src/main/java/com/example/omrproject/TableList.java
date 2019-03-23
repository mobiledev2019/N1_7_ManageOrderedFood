package com.example.omrproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omrproject.Common.Common;
import com.example.omrproject.Interface.ItemClickListener;
import com.example.omrproject.Model.Table;
import com.example.omrproject.ViewHolder.TableViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TableList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recycler_table;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Table, TableViewHolder> adapter;

    TextView txtFullName;

    FirebaseDatabase database;
    DatabaseReference tables;

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
        setContentView(R.layout.activity_tables);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_table);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //init Firebase
        database = FirebaseDatabase.getInstance();
        tables = database.getReference("Tables");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_table);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_table);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentStaff.getFullName());

        //load list Table
        recycler_table = (RecyclerView) findViewById(R.id.recycler_table);
        recycler_table.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recycler_table.setLayoutManager(layoutManager);

        loadTables();
    }

    private void loadTables(){
        FirebaseRecyclerOptions<Table> options = new FirebaseRecyclerOptions.Builder<Table>().setQuery(tables, Table.class).build();
        adapter = new FirebaseRecyclerAdapter<Table, TableViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TableViewHolder holder, int position, @NonNull Table model) {
                holder.table_name.setText(adapter.getRef(position).getKey());
                if(model.getFoods()!=null){
                    holder.table_layout.setBackground(ContextCompat.getDrawable(TableList.this, R.drawable.circle_green));
                    holder.table_name.setBackground(ContextCompat.getDrawable(TableList.this, R.drawable.circle_white));
                    holder.table_name.setTextColor(Color.parseColor("#A9d440"));
                    holder.table_img.setImageDrawable(ContextCompat.getDrawable(TableList.this, R.drawable.ic_table_white));
                }
                else{
                    holder.table_layout.setBackground(ContextCompat.getDrawable(TableList.this, R.drawable.circle_green_stroke));
                    holder.table_name.setBackground(ContextCompat.getDrawable(TableList.this, R.drawable.circle_black));
                    holder.table_name.setTextColor(Color.WHITE);
                    holder.table_img.setImageDrawable(ContextCompat.getDrawable(TableList.this, R.drawable.ic_table));
                }
                final Table clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent homeIntent = new Intent(TableList.this, ListOrder.class);
                        Common.currentTable = adapter.getRef(position).getKey();
                        startActivity(homeIntent);
                    }
                });
            }

            @NonNull
            @Override
            public TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_item, viewGroup, false);
                return new TableViewHolder(view);
            }
        };
        recycler_table.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_table);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if(id == R.id.nav_log_out) {
            Intent signIn = new Intent(TableList.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_table);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

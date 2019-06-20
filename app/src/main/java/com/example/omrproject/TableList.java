package com.example.omrproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omrproject.Common.Common;
import com.example.omrproject.Interface.ItemClickListener;
import com.example.omrproject.Model.Table;
import com.example.omrproject.ViewHolder.TableViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TableList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recycler_table;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Table, TableViewHolder> adapter;
    TextView txtFullName;
    FloatingActionButton btnFilterTable;

    Button btnNoSignOut;
    Button btnDoSignOut;
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


        btnFilterTable = (FloatingActionButton) findViewById(R.id.btn_filter_table);
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

        btnFilterTable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showFilterDialog();
            }
        });

        //load list Table
        recycler_table = (RecyclerView) findViewById(R.id.recycler_table);
        recycler_table.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recycler_table.setLayoutManager(layoutManager);

        loadTables();
    }

    private void showFilterDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TableList.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_filter_table, null);
        alertDialog.setView(dialogView);

        //get Radio Group
        final RadioGroup radioType = (RadioGroup) dialogView.findViewById(R.id.radioTypeTable);
        final RadioGroup radioStatus = (RadioGroup) dialogView.findViewById(R.id.radioStatusTable);
        final RadioGroup radioCapacity = (RadioGroup) dialogView.findViewById(R.id.radioCapacity);



        alertDialog.setPositiveButton("LỌC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get radio button selected
                int selectedTypeId = radioType.getCheckedRadioButtonId();
                int selectedStatusId = radioStatus.getCheckedRadioButtonId();
                int selectedCapacityId = radioCapacity.getCheckedRadioButtonId();

                RadioButton radioType = (RadioButton) dialogView.findViewById(selectedTypeId);
                RadioButton radioStatus = (RadioButton) dialogView.findViewById(selectedStatusId);
                RadioButton radioCapacity = (RadioButton) dialogView.findViewById(selectedCapacityId);
                //get Value
                String type = (radioType!=null)? radioType.getText().toString():"";
                String status = (radioStatus!=null)? radioStatus.getText().toString():"";
                String capacity = (radioCapacity!=null)? radioCapacity.getText().toString():"";

                String statusTable = (!status.isEmpty())?((status.equalsIgnoreCase("Trống"))? "0":"1"):"";
                String query = type+capacity+statusTable;
                Toast.makeText(TableList.this, query,Toast.LENGTH_SHORT).show();
                //reset list
                resetTables(type,capacity,statusTable);

            }
        });

        alertDialog.setNeutralButton("TẤT CẢ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        loadTables();
            }
        });

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    private void loadTables(){

        FirebaseRecyclerOptions<Table> options = new FirebaseRecyclerOptions.Builder<Table>().setQuery(tables.orderByChild("location").equalTo(Common.currentStaff.getLocation()), Table.class).build();
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
        adapter.startListening();
        recycler_table.setAdapter(adapter);
    }

    protected void resetTables(final String type, final String capacity, final String status){
        String query = type+capacity+status;
        Query listTable;
        if(type.equalsIgnoreCase("")&&!status.equalsIgnoreCase("")){
             listTable = database.getReference("Tables").orderByChild("capSt").equalTo(query);
        }else if(!type.equalsIgnoreCase("")&&status.equalsIgnoreCase("")){
            listTable = database.getReference("Tables").orderByChild("typeCapSt").startAt(query).endAt(query+"\uf8ff");
        }else if(type.equalsIgnoreCase("")&&status.equalsIgnoreCase("")){
            listTable = database.getReference("Tables").orderByChild("numberOfSeat").equalTo(query);
        }else{
            listTable = database.getReference("Tables").orderByChild("typeCapSt").equalTo(query);
        }

        FirebaseRecyclerOptions<Table> options = new FirebaseRecyclerOptions.Builder<Table>().setQuery(listTable, Table.class).build();
        adapter = new FirebaseRecyclerAdapter<Table, TableViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TableViewHolder holder, int position, @NonNull Table model) {
//                boolean selectSpace = ((occupiedOption==0&&model.getFoods()==null)||(occupiedOption==1&&model.getFoods()!=null))? true:false;
//                if(model.getType().equalsIgnoreCase(type)&&model.getNumberOfSeat().equalsIgnoreCase(capacity)&&selectSpace){
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
//                }else{}
            }

            @NonNull
            @Override
            public TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_item, viewGroup, false);
                return new TableViewHolder(view);
            }
        };
        adapter.startListening();
        recycler_table.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_table);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showSignOutDialog();
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

                Intent signIn = new Intent(TableList.this, SignIn.class);
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
            Intent statisticIntent = new Intent(TableList.this, Statistic.class);
            startActivity(statisticIntent);
        } else if (id == R.id.nav_orders) {

        } else if(id == R.id.nav_log_out) {
                showSignOutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_table);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

package com.example.omrproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.omrproject.Common.Common;
import com.example.omrproject.Database.DBOrder;
import com.example.omrproject.Model.Bill;
import com.example.omrproject.Model.Order;
import com.example.omrproject.ViewHolder.BillAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillView extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    TextView txtBillId, txtBillTable, txtCreatedBill, txtTotalBill, txtStaffName;
    Button btnConfirmPay;

    FirebaseDatabase database;
    DatabaseReference bill, tables;

    List<Order> listFoods = new ArrayList<>();
    BillAdapter adapter;


    String tableId = "";
    String createdTime = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);



        //init firebase database
        database = FirebaseDatabase.getInstance();
        bill = database.getReference("Bills");
        tables = database.getReference("Tables");
        //init

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bill);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtBillId = (TextView) findViewById(R.id.txtBillId);
        txtCreatedBill = (TextView) findViewById(R.id.txtCreatedBill);
        txtTotalBill = (TextView) findViewById(R.id.txtTotalBill);
        txtStaffName = (TextView) findViewById(R.id.txtStaffName);
        txtBillTable = (TextView) findViewById(R.id.txtBillTable);
        btnConfirmPay = (Button) findViewById(R.id.btnConfirmPay);

        tableId = Common.currentTable;

        btnConfirmPay.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
               showAlertDialog();
            }
        });

        //format date
        createdTime = System.currentTimeMillis()+"";
        String dateFormat = "dd/MM/yyyy hh:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        //set value
        txtStaffName.setText(Common.currentStaff.getFullName());
        txtCreatedBill.setText(formatter.format(new Date(Long.parseLong(createdTime))));
        txtBillId.setText(createdTime);


        //List foods
        recyclerView = (RecyclerView) findViewById(R.id.recycler_bill);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadBill(tableId);
    }

    private void loadBill(String tableId) {

        final DatabaseReference foods = tables.child(tableId).child("foods");
        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int total = 0;
                    for(DataSnapshot foodSnapshot: dataSnapshot.getChildren()){
                        Order order = foodSnapshot.getValue(Order.class);
                        listFoods.add(order);
                        total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
                    }
                    adapter = new BillAdapter(listFoods, BillView.this);
                    recyclerView.setAdapter(adapter);
                    Locale locale = new Locale ("en", "US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalBill.setText(fmt.format(total));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BillView.this);
        alertDialog.setTitle("Xác nhận");
        alertDialog.setMessage("Bạn sẽ chịu trách nghiệm nếu hóa đơn bị sai lệch");
        alertDialog.setPositiveButton("Chấp nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog mDialog = new ProgressDialog(BillView.this);
                mDialog.setMessage("Đang xử lý, vui lòng chờ....");
                mDialog.show();
                try{
                    bill.child(createdTime).setValue(new Bill(
                            Common.currentStaffId,
                            txtStaffName.getText().toString(),
                            txtTotalBill.getText().toString(),
                            createdTime,
                            txtBillTable.getText().toString(),
                            listFoods
                    )).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDialog.dismiss();
                            Toast.makeText(BillView.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                            new DBOrder().deleteOrder(tableId);
                            Intent table = new Intent(BillView.this, TableList.class);
                            table.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(table);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BillView.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}

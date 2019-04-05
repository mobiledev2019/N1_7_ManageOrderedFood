package com.example.omrproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static final String LOG_TAG = BillView.class.getSimpleName();
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
                    createPdf();
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
                            Intent table = new Intent(BillView.this, SendBill.class);
//                            table.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void createPdf(){
        Document document = new Document();
        try{
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ORMApp";
            System.out.println(path);

            File dir = new File(path);
            if(!dir.exists()) dir.mkdir();

            File bill = new File(dir, "bill.pdf");

            FileOutputStream fOut = new FileOutputStream(bill);
            PdfWriter.getInstance(document, fOut);

            //open to write
            document.open();

            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("AnTPTIT");
            document.addCreator("AnhxTank");

            //text color
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;

            //Font
            BaseFont urName = BaseFont.createFont(BaseFont.HELVETICA, "UTF-8", BaseFont.EMBEDDED);
            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            // Creating Chunk
            Chunk mOrderDetailsTitleChunk = new Chunk("HÓA ĐƠN", mOrderDetailsTitleFont);
            // Creating Paragraph to add...
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            // Setting Alignment for Heading
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            document.add(mOrderDetailsTitleParagraph);

            //Draw line and down line
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            Font mOrderTitleFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Font mOrderValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);

            // Mã hóa đơn
            Chunk mOrderIdChunk = new Chunk("Mã hóa đơn", mOrderTitleFont);
            document.add(new Paragraph(mOrderIdChunk));

            Chunk mOrderIdValueChunk = new Chunk(txtBillId.getText().toString(), mOrderValueFont);
            document.add(new Paragraph(mOrderIdValueChunk));

            //Draw line and down line
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Mã bàn
            Chunk mOrderTableTitleChunk = new Chunk("Mã bàn", mOrderTitleFont);
            document.add(new Paragraph(mOrderTableTitleChunk));

            Chunk mOrderTableValueChunk = new Chunk(txtBillTable.getText().toString(), mOrderValueFont);
            document.add(new Paragraph(mOrderTableValueChunk));

            //Draw line and down line
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Thời gian
            Chunk mOrderTimeChunk = new Chunk("Thời gian", mOrderTitleFont);
            document.add(new Paragraph(mOrderTimeChunk));

            Chunk mOrderTimeValueChunk = new Chunk(txtCreatedBill.getText().toString(), mOrderValueFont);
            document.add(new Paragraph(mOrderTimeValueChunk));

            //Draw line and down line
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Danh sách món
            Chunk mOrderFoodChunk = new Chunk("MENU", mOrderTitleFont);
            Paragraph mOrderFoodParagraph = new Paragraph(mOrderFoodChunk);
            mOrderFoodParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderFoodParagraph);

            //Draw line and down line
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            for (Order food: listFoods){

                Chunk glue = new Chunk(new VerticalPositionMark());
                Paragraph p = new Paragraph(food.getFoodName());
                p.add(new Chunk(glue));
                p.add("("+food.getDiscount()+"%)");
                Paragraph p1 = new Paragraph(food.getQuantity()+"*"+food.getPrice());
                p1.add(new Chunk(glue));
                p1.add((Integer.parseInt(food.getPrice()))*(Integer.parseInt(food.getQuantity()))+"");
                document.add(p);
                document.add(p1);

                //Draw line and down line
                document.add(new Paragraph(""));
                document.add(new Chunk(lineSeparator));
                document.add(new Paragraph(""));
            }

            Chunk glue = new Chunk(new VerticalPositionMark());

            //Tổng hóa đơn
            Paragraph p = new Paragraph("Tổng: ");
            p.setFont(mOrderTitleFont);
            p.add(new Chunk(glue));
            p.add(txtTotalBill.getText().toString());
            document.add(p);

            //Nhân viên
            Paragraph p1 = new Paragraph("Nhân viên quầy ");
            p1.setFont(mOrderTitleFont);
            p1.add(new Chunk(glue));
            p1.add(txtStaffName.getText().toString());
            document.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally
        {
            document.close();
        }
    }
}

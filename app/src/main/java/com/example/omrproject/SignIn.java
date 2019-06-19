package com.example.omrproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omrproject.Common.Common;
import com.example.omrproject.Model.Staff;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.omrproject";
    private String isLogin = "false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        isLogin = mPreferences.getString("IS_LOGIN", isLogin);
        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_staff = database.getReference("Staff");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Đang đăng nhập....");
                mDialog.show();

                table_staff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check if staff not exist in database
                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()){
                            //Get staff information
                            mDialog.dismiss();
                            Staff staff = dataSnapshot.child(edtPhone.getText().toString()).getValue(Staff.class);
                            Common.currentStaffId = dataSnapshot.child(edtPhone.getText().toString()).getKey();
                            // if type correct password
                            if(staff.getPassword().equals(edtPassword.getText().toString())){
                                Common.loginTime = System.currentTimeMillis();
                                Intent homeIntent = new Intent(SignIn.this, TableList.class);
                                Common.currentStaff = staff;
                                startActivity(homeIntent);
                                finish();
                            }else{
                                Toast.makeText(SignIn.this, "Sign in failed!!!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "Staff not exist in Database", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(SignIn.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}

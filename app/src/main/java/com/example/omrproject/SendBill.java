package com.example.omrproject;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class SendBill extends AppCompatActivity {
    
    Button btnSendBill;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bill);
        
        btnSendBill = (Button) findViewById(R.id.btnSendBill);
        
        btnSendBill.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendPdf();
            }
        });
    }

    private void sendPdf() {

        if (btAdapter == null) {
            Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
                String path = Environment.getExternalStorageDirectory() + "/ORMApp";
                File dir = new File(path);
                dir.mkdirs();

                File bill = new File(dir, "bill.pdf");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bill));
                startActivity(intent);
        }
    }

    public void enableBluetooth() {
//        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
//        startActivityForResult(discoveryIntent, REQUEST_BLU);
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }
}

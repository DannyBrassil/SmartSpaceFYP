package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrScanner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);


        //bottom nav menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewCarpark);
        // home is selected
        bottomNavigationView.setSelectedItemId(R.id.qrscanner);
        //item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.qrscanner:
                        return true;
                    case R.id.homeMenuCarpark:
                        startActivity(new Intent(getApplicationContext(), QrScanner.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });


        //initalise IntentIntegrator for Qr scanner
        IntentIntegrator intentIntegrator = new IntentIntegrator(QrScanner.this);

        intentIntegrator.setPrompt("for flash use Volume up button");

        intentIntegrator.setBeepEnabled(true);

        intentIntegrator.setOrientationLocked(true);

        intentIntegrator.setCaptureActivity(Capture.class);

        intentIntegrator.initiateScan();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //initiate intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        //check condition
        if(intentResult.getContents()!=null){
            //when result is not null initate alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(QrScanner.this);
            builder.setMessage(intentResult.getContents());
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }else{
            //result is null
            Toast.makeText(getApplicationContext(),"no code found", Toast.LENGTH_LONG).show();
        }

    }
}
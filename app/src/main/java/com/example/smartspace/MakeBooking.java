package com.example.smartspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MakeBooking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_booking);

       // BookingDialog dialog = new BookingDialog();
       // dialog.show(getSupportFragmentManager(), "booking dialog");

        final Intent intent = getIntent();
        final String title = intent.getStringExtra("name");
        final String space = intent.getStringExtra("spacesAvailable");
        final String dist = intent.getStringExtra("distance");

        TextView t1 = (TextView) findViewById(R.id.textViewName);
        TextView t2 = (TextView) findViewById(R.id.textViewSpace);
        TextView t3 = (TextView) findViewById(R.id.textViewDist);
        t1.setText(title);
        t2.setText(space);
        t3.setText(dist);
    }

}
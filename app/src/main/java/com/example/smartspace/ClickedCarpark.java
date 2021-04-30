package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ClickedCarpark extends AppCompatActivity {
    Geocoder geocoder;
    List<Address> addresses;
    int space;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_carpark);


        final TextView location = (TextView)findViewById(R.id.CarparkLocationText);
        final TextView distance = (TextView)findViewById(R.id.carparkDistanceAway);
        final TextView reserve = (TextView)findViewById(R.id.carparkIsReservble);
        final TextView description = (TextView)findViewById(R.id.carparkDescription);

        final Button b = (Button) findViewById(R.id.buttonClickedCarpark);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String id = bundle.getString("id");
        final Double dist = bundle.getDouble("distance");

       //address of carpark from latlng in database

        geocoder = new Geocoder(this, Locale.getDefault());


        final DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("carparks");

        //assign carpark details to the textboxes in the layout
        fireDB.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//every time change data the event listener
                // will execute on datachange method for
               Carpark c = snapshot.getValue(Carpark.class);


                try {
                    addresses = geocoder.getFromLocation(c.getLatitude(), c.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                location.setText(c.getName()+ " in "+addresses.get(0).getLocality());
                description.setText(c.getD());
               distance.setText(dist.intValue()+" KM away");
               if(c.isSmartspace()==true){
                   reserve.setText("Reservable");
                   b.setText("choose a space");
               }
               else{
                   reserve.setText("Non-Reservable");
                   b.setText("make reservation");
               }




            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DBError", "Cancel Access DB");
            }
        });

        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(b.getText().toString().equalsIgnoreCase("choose a space")){
                    Intent intent = new Intent(ClickedCarpark.this, BookingSelectSpace.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
                else {

                }
            }
        });

    }
}
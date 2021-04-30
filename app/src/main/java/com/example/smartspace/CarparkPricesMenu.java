package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CarparkPricesMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String userId;
    DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("carparks");
    TextView hour1, hour2, hour3, hour4, hour5, hour6, price1, price2, price3, price4, price5, price6, smartPrice1, smartPrice2, smartPrice3, smartPrice4, smartPrice5, smartPrice6;
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_prices_menu);

        hour1 = (TextView) findViewById(R.id.hourEntry10);
        hour2 = (TextView) findViewById(R.id.hourEntry7);
        hour3 = (TextView) findViewById(R.id.hourEntry8);
        hour4 = (TextView) findViewById(R.id.hourEntry9);
        hour5 = (TextView) findViewById(R.id.hourEntry11);
        hour6 = (TextView) findViewById(R.id.hourEntry13);

        price1 = (TextView) findViewById(R.id.priceEntry10);
        price2 = (TextView) findViewById(R.id.priceEntry7);
        price3 = (TextView) findViewById(R.id.priceEntry8);
        price4 = (TextView) findViewById(R.id.priceEntry9);
        price5 = (TextView) findViewById(R.id.priceEntry11);
        price6 = (TextView) findViewById(R.id.priceEntry13);

        smartPrice1 = (TextView) findViewById(R.id.priceEntry);
        smartPrice2 = (TextView) findViewById(R.id.priceEntry2);
        smartPrice3 = (TextView) findViewById(R.id.priceEntry3);
        smartPrice4 = (TextView) findViewById(R.id.priceEntry4);
        smartPrice5 = (TextView) findViewById(R.id.priceEntry6);
        smartPrice6 = (TextView) findViewById(R.id.priceEntry12);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId= mUser.getUid();

        back = findViewById(R.id.gobackarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarparkPricesMenu.this, CarparkHomeMenu.class));
            }
        });

        final Switch smartPrice = findViewById(R.id.smartPriceSwitch);

        fireDB.child(userId).child("smartprice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean smartPriceOn = snapshot.getValue(Boolean.class);
                if(smartPriceOn) {
                    smartPrice.setChecked(true);
                    fillInSmartPrices();
                }
                else{
                    smartPrice1.setText("");
                    smartPrice2.setText("");
                    smartPrice3.setText("");
                    smartPrice4.setText("");
                    smartPrice5.setText("");
                    smartPrice6.setText("");
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            fillInPrices();


        smartPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    Toast.makeText(CarparkPricesMenu.this, "Smart prices activated", Toast.LENGTH_LONG).show();
                    startService();
                    fillInSmartPrices();
                }else{
                    Toast.makeText(CarparkPricesMenu.this, "Smart prices deactivated", Toast.LENGTH_LONG).show();
                    stopService();
                }
            }


        });

    }


    public void startService() {
        fireDB.child(userId).child("smartprice").setValue(true);
        Intent serviceIntent = new Intent(CarparkPricesMenu.this, SmartPriceService.class);
        serviceIntent.putExtra("inputExtra", userId);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        fireDB.child(userId).child("smartprice").setValue(false);
        Intent serviceIntent = new Intent(this, SmartPriceService.class);
        stopService(serviceIntent);
    }

    private void fillInSmartPrices() {

                    fireDB.child(userId).child("smartprices").addValueEventListener(new ValueEventListener() {
                        final ArrayList<CarParkPrice> smartPrices = new ArrayList<>();
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            smartPrices.clear();
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                CarParkPrice price = userSnapshot.getValue(CarParkPrice.class);
                                smartPrices.add(price);
                            }

                            if(smartPrices.size()==0){


                            }else if(smartPrices.size()==1){
                                smartPrice1.setText(smartPrices.get(0).price.toString());
                            }
                            else if(smartPrices.size()==2){
                                smartPrice1.setText(smartPrices.get(0).price.toString());
                                smartPrice2.setText(smartPrices.get(1).price.toString());
                            }
                            else if(smartPrices.size()==3){
                                smartPrice1.setText(smartPrices.get(0).price.toString());
                                smartPrice2.setText(smartPrices.get(1).price.toString());
                                smartPrice3.setText(smartPrices.get(2).price.toString());

                            }
                            else if(smartPrices.size()==4){
                                smartPrice1.setText(smartPrices.get(0).price.toString());
                                smartPrice2.setText(smartPrices.get(1).price.toString());
                                smartPrice3.setText(smartPrices.get(2).price.toString());
                                smartPrice4.setText(smartPrices.get(3).price.toString());
                            }
                            else if(smartPrices.size()==5){
                                smartPrice1.setText(smartPrices.get(0).price.toString());
                                smartPrice2.setText(smartPrices.get(1).price.toString());
                                smartPrice3.setText(smartPrices.get(2).price.toString());
                                smartPrice4.setText(smartPrices.get(3).price.toString());
                                smartPrice5.setText(smartPrices.get(4).price.toString());
                            }
                            else if(smartPrices.size()==6) {
                                smartPrice1.setText(smartPrices.get(0).price.toString());
                                smartPrice2.setText(smartPrices.get(1).price.toString());
                                smartPrice3.setText(smartPrices.get(2).price.toString());
                                smartPrice4.setText(smartPrices.get(3).price.toString());
                                smartPrice5.setText(smartPrices.get(4).price.toString());
                                smartPrice6.setText(smartPrices.get(5).price.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




    }

    private void fillInPrices() {
        Log.i("id", ""+userId);
        final ArrayList<CarParkPrice> prices = new ArrayList<>();
        fireDB.child(userId).child("prices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    CarParkPrice price = userSnapshot.getValue(CarParkPrice.class);
                    prices.add(price);
                }
                Log.i("price size",""+prices.size() );

                if(prices.size()==0){


                }else if(prices.size()==1){
                    price1.setText(prices.get(0).price.toString());
                    hour1.setText(String.valueOf(prices.get(0).hour));

                }
                else if(prices.size()==2){
                    price1.setText(prices.get(0).price.toString());
                    hour1.setText(String.valueOf(prices.get(0).hour));
                    price2.setText(prices.get(1).price.toString());
                    hour2.setText(String.valueOf(prices.get(1).hour));
                }
                else if(prices.size()==3){
                    price1.setText(prices.get(0).price.toString());
                    hour1.setText(String.valueOf(prices.get(0).hour));
                    price2.setText(prices.get(1).price.toString());
                    hour2.setText(String.valueOf(prices.get(1).hour));
                    price3.setText(prices.get(2).price.toString());
                    hour3.setText(String.valueOf(prices.get(2).hour));
                }
                else if(prices.size()==4){
                    price1.setText(prices.get(0).price.toString());
                    hour1.setText(String.valueOf(prices.get(0).hour));
                    price2.setText(prices.get(1).price.toString());
                    hour2.setText(String.valueOf(prices.get(1).hour));
                    price3.setText(prices.get(2).price.toString());
                    hour3.setText(String.valueOf(prices.get(2).hour));
                    price4.setText(prices.get(3).price.toString());
                    hour4.setText(String.valueOf(prices.get(3).hour));
                }
                else if(prices.size()==5){
                    price1.setText(prices.get(0).price.toString());
                    hour1.setText(String.valueOf(prices.get(0).hour));
                    price2.setText(prices.get(1).price.toString());
                    hour2.setText(String.valueOf(prices.get(1).hour));
                    price3.setText(prices.get(2).price.toString());
                    hour3.setText(String.valueOf(prices.get(2).hour));
                    price4.setText(prices.get(3).price.toString());
                    hour4.setText(String.valueOf(prices.get(3).hour));
                    price5.setText(prices.get(4).price.toString());
                    hour5.setText(String.valueOf(prices.get(4).hour));
                    price6.setText(prices.get(5).price.toString());
                    hour6.setText(String.valueOf(prices.get(5).hour));
                }
                else if(prices.size()==6) {
                    price1.setText(prices.get(0).price.toString());
                    hour1.setText(String.valueOf(prices.get(0).hour));
                    price2.setText(prices.get(1).price.toString());
                    hour2.setText(String.valueOf(prices.get(1).hour));
                    price3.setText(prices.get(2).price.toString());
                    hour3.setText(String.valueOf(prices.get(2).hour));
                    price4.setText(prices.get(3).price.toString());
                    hour4.setText(String.valueOf(prices.get(3).hour));
                    price5.setText(prices.get(4).price.toString());
                    hour5.setText(String.valueOf(prices.get(4).hour));
                    price6.setText(prices.get(5).price.toString());
                    hour6.setText(String.valueOf(prices.get(5).hour));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
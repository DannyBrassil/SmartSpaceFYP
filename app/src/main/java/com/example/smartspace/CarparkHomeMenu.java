package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CarparkHomeMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_home_menu);

        //bottom nav menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewCarpark);
        // home is selected
        bottomNavigationView.setSelectedItemId(R.id.homeMenuCarpark);
        //item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.homeMenuCarpark:
                        return true;
                    case R.id.qrscanner:
                        startActivity(new Intent(getApplicationContext(), QrScanner.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        CardView priceTab = (CardView)findViewById(R.id.cardView4);
        CardView profileTab = (CardView)findViewById(R.id.cardView);
        CardView LayoutTab = (CardView)findViewById(R.id.cardView2);

        priceTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarparkHomeMenu.this, CarparkPricesMenu.class));
            }
        });
        profileTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarparkHomeMenu.this, CarparkProfileMenu.class));
            }
        });
        LayoutTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarparkHomeMenu.this, CarparkLayoutMenu.class));
            }
        });
    }
}
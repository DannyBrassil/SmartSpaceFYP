package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CarparkProfileMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String userId;


    EditText carparkName, carparkEmail, carparkPassowrd;
    TextView back;
    DatabaseReference fireDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_profile_menu);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId= mUser.getUid();
        fireDB = FirebaseDatabase.getInstance().getReference("carparks").child(userId);
        carparkName=findViewById(R.id.carParkName2);
        carparkEmail=findViewById(R.id.carpParkEmail2);
        carparkPassowrd=findViewById(R.id.carParkPassword2);


        insertDetailsFromDB();

        Button save = findViewById(R.id.saveProfileDetailsButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

        back = findViewById(R.id.gobackarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarparkProfileMenu.this, CarparkHomeMenu.class));
            }
        });
    }

    private void insertDetailsFromDB() {
        fireDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Carpark carpark = snapshot.getValue(Carpark.class);
                carparkName.setText(carpark.getName());
                carparkEmail.setText(carpark.getEmail());
                carparkPassowrd.setText(carpark.getPassword());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void saveDetails() {
        fireDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fireDB.child("email").setValue(carparkEmail.getText().toString());
                fireDB.child("password").setValue(carparkPassowrd.getText().toString());
                fireDB.child("name").setValue(carparkName.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
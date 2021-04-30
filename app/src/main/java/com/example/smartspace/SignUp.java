package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        Button button = (Button) findViewById(R.id.signUp);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createAccount();
            }
        });
    }

    public void createAccount(){
        EditText e1 = (EditText) findViewById(R.id.emailText);
        EditText e2 = (EditText) findViewById(R.id.passwordText);
        EditText e3 = (EditText) findViewById(R.id.firstnametext);
        EditText e4 = (EditText) findViewById(R.id.secondnametext);

        final String firstname = e3.getText().toString();
        final String secondname = e4.getText().toString();
        final String email = e1.getText().toString();
        final String password = e2.getText().toString();
        final List<Booking> bookings = new ArrayList<Booking>();



        if(email.isEmpty()){
            e1.setError("Enter email");
            e1.requestFocus();
        }
        else if(password.isEmpty()){
            e2.setError("Enter password");
            e2.requestFocus();
        }
        else if(!(email.isEmpty() && password.isEmpty())){

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "signup unsuccessful", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUp.this, "sign up success. please login", Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();

                                //add user to realtime database
                                db= FirebaseDatabase.getInstance().getReference(); // get referencefrom root
                                User person = new User(email, password,firstname, secondname);

                                db.child("Users").child(uid).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUp.this, "Write is successful", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUp.this, "Write is not successful", Toast.LENGTH_LONG).show();
                                    }
                                });


                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                startActivity(intent);
                            }

                            // ...
                        }
                    });
        }




    }



}
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

public class CarparkSignup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_signup);

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


        final String email = e1.getText().toString();
        final String password = e2.getText().toString();


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
                                Toast.makeText(CarparkSignup.this, "signup unsuccessful", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CarparkSignup.this, "sign up success. please login", Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();


                                Intent intent = new Intent(CarparkSignup.this, scroll.class);
                                intent.putExtra("UserID", uid);
                                intent.putExtra("UserEmail", email);
                                intent.putExtra("UserPassword", password);
                                startActivity(intent);
                            }

                            // ...
                        }
                    });
        }




    }



}
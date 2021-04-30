package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Button button = (Button) findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                login();
            }
        });


        //signup button
        Button button2 = (Button) findViewById(R.id.signUpButton);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signup();
            }
        });

        Button button3 = (Button) findViewById(R.id.AdvertiseButton);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, CarparkSignup.class);// CreateCarparkAccount.class
                startActivity(intent);
            }
        });
    }

    //redirect to signup activity when signup button is clisked
    public void signup(){
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
    }



   //  login in password and email
    public void login(){
        EditText e1 = (EditText) findViewById(R.id.emailText);
        EditText e2 = (EditText) findViewById(R.id.passwordText);
        final String email = e1.getText().toString();
        final String password = e2.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "signup successful", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            //check if email is in users database
                            DatabaseReference dbusers;
                            dbusers= FirebaseDatabase.getInstance().getReference("Users");
                            dbusers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                        User user = snapshot2.getValue(User.class);
                                        if(email.equals(user.getEmail())){
                                            //if email is in users direct to the user homepage
                                            Intent intent = new Intent(MainActivity.this, HomeMenu.class);
                                            intent.putExtra("username",user.getEmail());
                                            startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //check if email is in carparks database
                            DatabaseReference dbcarparks;
                            dbcarparks= FirebaseDatabase.getInstance().getReference("carparks");
                            dbcarparks.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                        Carpark c = snapshot2.getValue(Carpark.class);
                                        if(email.equals(c.getEmail())){
                                            //if email is in carparks direct to the carpark homepage
                                            Intent intent = new Intent(MainActivity.this, CarparkHomeMenu.class);
                                            intent.putExtra("username",c.getEmail());
                                            startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            // If sign in fails, display message to the user.
                            Toast.makeText(MainActivity.this, "Log in unsuccessful, Please create an account", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}
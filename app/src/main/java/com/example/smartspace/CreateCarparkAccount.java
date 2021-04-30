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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class CreateCarparkAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public DatabaseReference db;

     String email;
     String password;
     String name;
    EditText e1;
    EditText e2;
    EditText e3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_carpark_account);

        e1 = (EditText) findViewById(R.id.CarpParkEmail);
        e2 = (EditText) findViewById(R.id.CarParkPassword);
        e3 = (EditText) findViewById(R.id.CarParkName);

        if(savedInstanceState!=null){
            String myString = savedInstanceState.getString("savedemail");
            e1.setText(myString);
            Toast.makeText(CreateCarparkAccount.this, "saved instance working", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(CreateCarparkAccount.this, "saved instance not working", Toast.LENGTH_LONG).show();
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("savedemail", e1.getText().toString());

        // etc.
    }
/*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        String myString = savedInstanceState.getString("savedemail");
        e1.setText(myString);
    }

    @Override
    public void onResume(){
        super.onResume();

        e1 = (EditText) findViewById(R.id.CarpParkEmail);
        e1.setText(this.email);

    }

    @Override
    public void onPause(){
        super.onPause();
        this.email = e1.getText().toString();
//        this.password = e2.getText().toString();
    //    this.name = e3.getText().toString();
    }
*/

    private void createAccount() {



        email = e1.getText().toString();
         password = e2.getText().toString();
         name = e3.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        if (email.isEmpty()) {
            e1.setError("Enter email");
            e1.requestFocus();
        } else if (password.isEmpty()) {
            e2.setError("Enter password");
            e2.requestFocus();
        } else if (!(email.isEmpty() && password.isEmpty())) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateCarparkAccount.this, "signup unsuccessful", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CreateCarparkAccount.this, "sign up success.", Toast.LENGTH_LONG).show();

                               /*  FirebaseUser user = mAuth.getCurrentUser();
                               String uid = user.getUid();

                                //add user to realtime database
                                //db = FirebaseDatabase.getInstance().getReference(); // get referencefrom root
                               // User person = new User(email, password, firstname, secondname);

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
                                */

                                Intent intent = new Intent(CreateCarparkAccount.this, SpaceRegister.class);
                                Bundle b = new Bundle();
                                b.putString("name", name);
                                b.putString("email", email);
                                b.putString("password", password);
                                intent.putExtra("bundle", b);
                                startActivity(intent);
                            }


                        }
                    });
        }
    }
}
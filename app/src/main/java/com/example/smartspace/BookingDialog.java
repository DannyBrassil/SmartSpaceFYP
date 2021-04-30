package com.example.smartspace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class BookingDialog extends AppCompatDialogFragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    User userObj = new User();
    private String name;
    private int space;
    private double dist;
    String hours; //hours for booking
    String mins; //mins for booking

    public BookingDialog(String n, int s, double d){
    this.name = n;
    this.space =s;
    this.dist=d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.activity_make_booking, null);

        final TextView n = (TextView) view.findViewById(R.id.textViewName);
        TextView s = (TextView) view.findViewById(R.id.textViewSpace);
        TextView a = (TextView) view.findViewById(R.id.textViewDist);
        final Spinner aSpinner=view.findViewById(R.id.spinnerHours);
        final Spinner aSpinner2=view.findViewById(R.id.spinnerMins);


        aSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                hours = aSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        aSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                  mins = aSpinner2.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        n.setText(name);
        s.setText(space +" spaces available");
        a.setText((int)dist+" Km away");



        builder.setView(view).setNegativeButton("Cancel         ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .setPositiveButton("Make Booking", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int h = parseInt(hours);
                        int m = parseInt(mins);
                        final int time = ((h*60)+m)*60; //time of booking in seconds


                        Date today = new Date();
                        today= Calendar.getInstance().getTime();
                        final String d = new SimpleDateFormat("dd-MM-yyyy").format(today);

                        final Date finalToday = today;
                        final Booking booking = new Booking("example",name, finalToday, time, true, false,false);

                        mAuth = FirebaseAuth.getInstance();
                        mUser = mAuth.getCurrentUser();

                        final DatabaseReference fireDB;
                        fireDB= FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

                        fireDB.child("Bookings").push().setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                });


        return builder.create();
    }
}

package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.stripe.android.PaymentConfiguration;

import okhttp3.Request;

import static java.lang.Integer.parseInt;

public class ConfirmBooking extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    User userObj = new User();
     String name=""; // name of carpark for booking
    private int space;// space of booking
    private double dist;
    String hours; //hours for booking
    String mins; //mins for booking
    String id;
    String d;//date
    String userId;
    String email;
    String selectedtime="";
    String bookingRefernece="";
    Bitmap bitmap;//qr code

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        space= intent.getIntExtra("space",0);





        final TextView n = (TextView) findViewById(R.id.textViewName);
        TextView s = (TextView) findViewById(R.id.textViewSpace);
        TextView a = (TextView) findViewById(R.id.textViewDist);
        final Spinner aSpinner=findViewById(R.id.duration);

        aSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedtime = aSpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Button pay = (Button)findViewById(R.id.payButton);

        final DatabaseReference fireDBCarpark;
        fireDBCarpark= FirebaseDatabase.getInstance().getReference("carparks").child(id);

        fireDBCarpark.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Carpark c = snapshot.getValue(Carpark.class);

                name= c.getName().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        s.setText(""+space);//space\picked
        n.setText(name);//name of carpark

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            /*    int h = parseInt(hours);
                int m = parseInt(mins);
                final int time = ((h*60)+m)*60; //time of booking in seconds



*/





            int time=0;

            if(selectedtime.equalsIgnoreCase("30 minutes")){
                time=30*60*60;//time in seconds
            }
            else if(selectedtime.equalsIgnoreCase("1 hour")){
                time=60*60*60;
            }
            else if(selectedtime.equalsIgnoreCase("2 hours")){
                time=120*60*60;
            }
            else if(selectedtime.equalsIgnoreCase("3 hours")){
                time=180*60*60;
            }
            else if(selectedtime.equalsIgnoreCase("5 hours")){
                time=300*60*60;
            }

                Date today = new Date();
                today= Calendar.getInstance().getTime();
                d = new SimpleDateFormat("dd-MM-yyyy").format(today);

                final Date finalToday = today;
                final Booking booking = new Booking(id, name, finalToday, time, true,false,false);

                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser();
               userId= mUser.getUid();

                final DatabaseReference fireDBuser;
                fireDBuser= FirebaseDatabase.getInstance().getReference("Users").child(userId);

                //add the new booking to the database
                fireDBuser.child("Bookings").push().setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //get the key from this new booking pushed to the database. this will act as a booking refernce for the QR code
                        fireDBuser.child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ss: snapshot.getChildren()){
                                    Booking b = ss.getValue(Booking.class);
                                    if(b.getActive()==true){
                                       bookingRefernece= ss.getKey();
                                       Log.i("booking key",bookingRefernece);
                                       generateQrCode();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        sendEmail();
                        startActivity(new Intent(ConfirmBooking.this, PayActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }

    private void generateQrCode() {
        MultiFormatWriter mfw = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = mfw.encode("1", BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
             bitmap = barcodeEncoder.createBitmap(bitMatrix);

        }catch (Exception e){
            e.printStackTrace();
        }

        uploadImage();



    }

    protected void sendEmail() {
        //getUser();
        Log.i("user email", ""+mUser.getEmail());
        String mail = mUser.getEmail();
        String message ="Booking confirmd at "+name+" on ";
        String subject = "Booking Confirmation";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);

        javaMailAPI.execute();
    }

    private void uploadImage() {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (bitmap != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            String childref="images/bookings/"+bookingRefernece;
            Log.i("ref",childref);
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(childref);

            // adding listeners on upload

            //covert bitmap image of qr code to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });


        }
    }


    private void getUser(){

        final DatabaseReference fireDBUser;
        fireDBUser= FirebaseDatabase.getInstance().getReference("Users").child(userId);

        fireDBUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);

                email= u.getEmail();
                Log.i("user email", ""+email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
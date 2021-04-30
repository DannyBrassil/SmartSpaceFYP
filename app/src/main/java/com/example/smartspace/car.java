package com.example.smartspace;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentResult;

public class car extends AppCompatActivity {

    ProgressBar mProgressBar, mProgressBar1;
    int time;
    int max = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("Users");
    DatabaseReference fireDBCarpark = FirebaseDatabase.getInstance().getReference("carparks");
    FirebaseStorage storage;
    StorageReference storageReference;
    String reference="";
    Bitmap bm;//bitmap of qr code
    int stars=0;
    long seconds;//timer seconds left
    String carparkID;
    Boolean allInactive=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        final TextView ShowTime = (TextView) findViewById(R.id.textView_timerview_time);



        //bottom nav menu
        //bottom nav menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // home is selected
        bottomNavigationView.setSelectedItemId(R.id.car);
        //item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.mapsActivity:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeMenu.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.car:
                        return true;
                }

                return false;
            }
        });
        //get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);

         final ImageView QRImage = (ImageView)findViewById(R.id.QrImage);

         //enlarge QR code when clicked
         QRImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                openDialog(bm);
             }
         });

         //access database
        fireDB.child(mUser.getUid()).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//every time change data the event listener
                // will execute on datachange method for
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    Booking b= userSnapshot.getValue(Booking.class);
                        //if the booking is active start the service

                        if (b.getActive() == true) {
                            allInactive=false;

                            reference = userSnapshot.getKey();
                            time = b.getTime();
                            carparkID = b.getCarparkid();

                            if (b.getReview() == true) {
                                LeaveReview();
                            }

                            mProgressBar1.setMax(time / 1000);
                            // Toast.makeText(car.this, "max progress bar" + time/1000 , Toast.LENGTH_LONG).show();
                            String childref = "images/bookings/" + reference;
                            Log.i("Booking ref key", childref);
                            // Defining the child of storageReference
                            StorageReference ref = storageReference.child(childref);

                            final long ONE_MEGABYTE = 1024 * 1024;
                            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    DisplayMetrics dm = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                                    QRImage.setImageBitmap(bm);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors

                                }

                            });


                            // if started =false then start service and set started to true
                            if (b.getStarted() == false) {
                                Intent i = new Intent(car.this, BroadcastService.class);
                                i.putExtra("time", time);
                                startService(i);


                                //   Toast.makeText(car.this, "Started Service", Toast.LENGTH_LONG).show();

                                fireDB.child(mUser.getUid()).child("Bookings").child(reference).child("started").setValue(true);
                            }
                        } else {

                        }

                }

                if(allInactive==true){
                    noBookingsDialog();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DBError", "Cancel Access DB");
            }


        });


      //  Toast.makeText(car.this, "pgress bar max " + time * 1000, Toast.LENGTH_LONG).show();

    }

    private void noBookingsDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.no_bookings_dialog, null);
        dialogBuilder.setView(dialogView).setPositiveButton("go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(car.this, MapsActivity.class);
                startActivity(i);
            }
        });

        dialogBuilder.create();
        dialogBuilder.show();


    }



    private void LeaveReview() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_review_dialog, null);
        final TextView star1 = (TextView)dialogView.findViewById(R.id.Star1);
        final TextView star2 = (TextView)dialogView.findViewById(R.id.Star2);
        final TextView star3 = (TextView)dialogView.findViewById(R.id.Star3);
        final TextView star4 = (TextView)dialogView.findViewById(R.id.Star4);
        final TextView star5 = (TextView)dialogView.findViewById(R.id.Star5);

        final TextView words = (TextView)dialogView.findViewById(R.id.wordCountReview);
        final EditText review = (EditText) dialogView.findViewById(R.id.reviewText);



        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                words.setText(String.valueOf(s.length())+"/300");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        };

        review.addTextChangedListener(mTextEditorWatcher);

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star2.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                star3.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                star4.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                star5.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                stars=1;
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star2.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star3.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                star4.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                star5.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                stars=2;
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star2.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star3.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star4.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                star5.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                stars=3;
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star2.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star3.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star4.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star5.setBackground(getResources().getDrawable(R.drawable.star_reviews));
                stars=4;
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star2.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star3.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star4.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                star5.setBackground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                stars=5;
            }
        });


        dialogBuilder.setView(dialogView).setPositiveButton("Submit Feedback", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if(stars!=0){
                    Review aReview = new Review(stars, review.getText().toString());
                    fireDBCarpark.child(carparkID).child("reviews").push().setValue(aReview);
                    fireDB.child(mUser.getUid()).child("Bookings").child(reference).child("active").setValue(false);
                    dialog.dismiss();
                }
                else {
                    star1.setError("choose a star rating first");
                    star1.requestFocus();
                }
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fireDB.child(mUser.getUid()).child("Bookings").child(reference).child("active").setValue(false);
                dialog.dismiss();
            }
        });

        dialogBuilder.create();
        dialogBuilder.show();
    }

    private void openDialog(Bitmap bm) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.qr_dialog, null);
        ImageView QRImagedialog = (ImageView) dialogView.findViewById(R.id.qrImageDialog);
        QRImagedialog.setImageBitmap(bm);
        dialogBuilder.setView(dialogView).setPositiveButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
    });

        dialogBuilder.create();
        dialogBuilder.show();


    }


    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);

            updateGUI(intent, millisUntilFinished); // update timer text
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
      //  Toast.makeText(car.this, "Registered brodcast reciver", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
       // unregisterReceiver(br);
        //Toast.makeText(car.this, "unregistered reciver", Toast.LENGTH_LONG).show();
    }

  /*   @Override
    public void onStop() {
        try {
            //unregisterReceiver(br);
        } catch (Exception e) {

        }
        super.onStop();
    }

   @Override
    public void onDestroy() {
        stopService(new Intent(this, BroadcastService.class));
        Toast.makeText(car.this, "stopped Service", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }*/

    private void updateGUI(Intent intent, long millisUntilFinished) {
       // mProgressBar1.setMax(time/1000);
        if (intent.getExtras() != null) {

            if (millisUntilFinished != 0) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressBar1.setVisibility(View.VISIBLE);

                //print seconds left in 00:00:00 format
                 seconds = millisUntilFinished / 1000;
                TextView ShowTime = (TextView) findViewById(R.id.textView_timerview_time);
                ShowTime.setText(String.format("%02d", seconds / 3600)+ ":" + String.format("%02d", (seconds % 3600) / 60)
                        + ":" + String.format("%02d", seconds % 60));
                mProgressBar1.setProgress((int) (millisUntilFinished / 1000));
               // Toast.makeText(car.this, "seconds remain progress:  " + millisUntilFinished / 1000, Toast.LENGTH_LONG).show();
                if(seconds==1){
                    fireDB.child(mUser.getUid()).child("Bookings").child(reference).child("review").setValue(true);
                    Toast.makeText(car.this, "make finish true" , Toast.LENGTH_LONG).show();
                }
            }
            else{
               // Toast.makeText(car.this, "m till finihsed:  " + millisUntilFinished , Toast.LENGTH_LONG).show();
            }
        }
        else{
            //hide countdown progress bar and show original

           // finished=true;
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar1.setVisibility(View.GONE);


        }

    }

    private void setQRImage(){

    }






}
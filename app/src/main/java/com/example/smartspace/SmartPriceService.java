package com.example.smartspace;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SmartPriceService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("carparks");
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Smart pricing activated")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);


        //

        fireDB.child(input).child("spaceAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



        fireDB.child(input).addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<CarParkPrice> smartPricesArrayList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Carpark carpark = snapshot.getValue(Carpark.class);
                if (carpark.isSmartprice()){
                    Log.i("smartprice", "is on");
                    final double percentage= (carpark.getSpaces()-carpark.getSpaceAvailable())*.01;
                    fireDB.child(input).child("prices").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double smartPrice;
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                CarParkPrice price = userSnapshot.getValue(CarParkPrice.class);
// if 90% of carpark if empty reduce the prices, else raise the prices
                                if((carpark.getSpaceAvailable()/carpark.getSpaces())*100<90){
                                    //raise prices
                                     smartPrice =  price.getPrice()+(price.getPrice()*percentage);
                                }
                                else{
                                    //reduce prices
                                     smartPrice =  price.getPrice()-(price.getPrice()*percentage);
                                }

                                CarParkPrice NewPrice = new CarParkPrice(price.getHour(), smartPrice);
                                smartPricesArrayList.add(NewPrice);



                            }

                            Log.i("arraysize",""+smartPricesArrayList.size());
                            fireDB.child(input).child("smartprices").setValue(smartPricesArrayList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                   // setValuetodb(input, smartPricesArrayList);

                }
                else{
                    Log.i("smartprice", "is off");
                }
            }







            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//first
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
        //stopSelf();
        return START_NOT_STICKY;
    }

    private void setValuetodb(String input, ArrayList<CarParkPrice> smartPricesArrayList) {
        fireDB.child(input).child("smartprices").setValue(smartPricesArrayList);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
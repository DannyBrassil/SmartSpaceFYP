package com.example.smartspace;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SortByDistance  {
    DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("carparks");
    ArrayList<Carpark> carparks = new ArrayList<>();
    HashMap<String, Double> carparksHashmap = new HashMap<String, Double>();//record lowest carpark price


    public void sort(final LatLng latLng, final SortingCallback myCallback) {
        fireDB.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Carpark c = dataSnapshot.getValue(Carpark.class);
                    carparks.add(c);//add to arraylist
                }

                //bubble sort on carparks distance
                for (int i = 0; i < carparks.size() - 1; i++) {
                    for (int j = 0; j < carparks.size() - i - 1; j++) {
                        Log.i("dist recommend", "" + carparks.get(j).distance(carparks.get(j).getLatitude(), carparks.get(j).getLongitude(), latLng.latitude, latLng.longitude));
                        Log.i("latlng", "" + latLng.toString());
                        if (carparks.get(j).distance(carparks.get(j).getLatitude(), carparks.get(j).getLongitude(), latLng.latitude, latLng.longitude) > carparks.get(j + 1).distance(carparks.get(j + 1).getLatitude(), carparks.get(j + 1).getLongitude(), latLng.latitude, latLng.longitude)) {
                            Carpark temp = carparks.get(j);

                            carparks.set(j, carparks.get(j + 1));

                            carparks.set(j + 1, temp);
                        }
                    }

                }
                myCallback.onCallback(carparks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

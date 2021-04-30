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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SortByCheapest implements SortingStrategy {
    DatabaseReference fireDBUser = FirebaseDatabase.getInstance().getReference("carparks");
    ArrayList<Carpark> carparks2 = new ArrayList<>();
    HashMap<String, Double> carparksHashmap = new HashMap<String, Double>();//record lowest carpark price


    @Override
    public void sort(final SortingCallback myCallback) {
        fireDBUser.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //go through each carpark in the database
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    Carpark carParkObj = userSnapshot.getValue(Carpark.class);
                    //find smallest price in arraylist of prices for that carpark
                    Double min = carParkObj.getPrices().get(0).getPrice();

                    for(int i = 0; i<carParkObj.getPrices().size();i++){
                        if(carParkObj.getPrices().get(i).getPrice()<min){
                            min = carParkObj.getPrices().get(i).getPrice();
                        }
                    }

                    carparksHashmap.put(carParkObj.getId(),min);

                }

                //create list from hashmap
                List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(carparksHashmap.entrySet());
                // sort the list
                Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
                {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return (o1.getValue()).compareTo(o2.getValue());
                    }
                });
                HashMap<String, Double> ha = new LinkedHashMap<String, Double>();
                for(Map.Entry<String, Double> me : list)
                {
                    ha.put(me.getKey(), me.getValue());
                }
                Log.i("hashmap ascedning", ha.toString());


                /*go through each carpark in the database
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    Carpark carParkObj = userSnapshot.getValue(Carpark.class);
                    //find smallest price in arraylist of prices for that carpark
                    Iterator it = carparksHashmap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        if(carParkObj.getId().equals(pair.getKey())){
                            carparks2.add(carParkObj);
                        }
                    }
                }
                */
                //add carparks from hashmap to an arraylist of carpark objects in ascending order



                    fireDBUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Iterator it = carparksHashmap.entrySet().iterator();
                            while (it.hasNext()) {

                                Map.Entry pair = (Map.Entry) it.next();

                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                Carpark c = userSnapshot.getValue(Carpark.class);
                                if(c.getId().equals(pair.getKey())){
                                    carparks2.add(c);
                                }
                            }

                            }

                            for(int i=0;i<carparks2.size(); i++){
                                Log.i("test2", carparks2.get(i).getName().toString());
                            }
                            myCallback.onCallback(carparks2);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void sort(LatLng latLng, String id, SortingCallback myCallback) {

    }
}

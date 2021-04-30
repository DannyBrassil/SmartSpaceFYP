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
import java.util.Iterator;
import java.util.Map;

//weighted algorithm to recommend a carpark to a specific user
public class Recommended {
    HashMap<String, Integer> carparks = new HashMap<String, Integer>();//record weights
    ArrayList<Carpark> carparks2 = new ArrayList<>();
    Carpark c = new Carpark();
    String maxcarparkID=null;

    DatabaseReference fireDBCarpark = FirebaseDatabase.getInstance().getReference("carparks");
    DatabaseReference fireDBUsers= FirebaseDatabase.getInstance().getReference("Users");

    public void recommend(final LatLng latLng, final String id, final MyCallback myCallback ) {

        Log.i("latlng user  ", ""+latLng.toString());

        fireDBCarpark.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Carpark c = dataSnapshot.getValue(Carpark.class);

                    carparks.put(c.getId(), 0);//add to hashmap and set value as 0 and key as carpark id
                    carparks2.add(c);//add to arraylist
                    Log.i("css  ", "id" + c.getId());
                }

                Iterator CarparkIterator = carparks.entrySet().iterator();
                while (CarparkIterator.hasNext()) {
                    Map.Entry mapElement = (Map.Entry) CarparkIterator.next();
                    String id = (String) mapElement.getKey();
                    Log.i("carpark id r:  ", "" + id);
                }
                Log.i("carpark1 ",  carparks2.get(0).getName());
                //bubble sort on carparks distance
                for (int i = 0; i < carparks2.size() - 1; i++) {
                    for (int j = 0; j < carparks2.size() - i - 1; j++) {
                        Log.i("dist recommend", ""+carparks2.get(j).distance(carparks2.get(j).getLatitude(), carparks2.get(j).getLongitude(), latLng.latitude, latLng.longitude));
                        Log.i("latlng",""+latLng.toString());
                        if (carparks2.get(j).distance(carparks2.get(j).getLatitude(), carparks2.get(j).getLongitude(), latLng.latitude, latLng.longitude) > carparks2.get(j + 1).distance(carparks2.get(j + 1).getLatitude(), carparks2.get(j + 1).getLongitude(), latLng.latitude, latLng.longitude)) {
                            Carpark temp = carparks2.get(j);

                            carparks2.set(j, carparks2.get(j + 1));

                            carparks2.set(j + 1, temp);
                        }
                    }


                }

                //assign points to carparks
                //max points is the number of carparks, the closest carpark gets the max number and then it decreases by one for the second closest carpark etc.
                int points=carparks2.size();

                for(int i=0; i<carparks2.size(); i++){
                    if (points>0) {
                        carparks.put(carparks2.get(i).getId(), carparks.get(carparks2.get(i).getId()) + points);
                        points--;
                    }

                }

                Log.i("carpark2 ",  carparks2.get(0).getName());

                for (Map.Entry mapElement : carparks.entrySet()) {
                    String key = (String)mapElement.getKey();

                    Log.i("hashmap",(key + " : " + mapElement.getValue()));
                }



                //look through users history of boooking and add points based on that
                fireDBUsers.child(id).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Booking b = dataSnapshot.getValue(Booking.class);

                            Iterator CarparkIterator = carparks.entrySet().iterator();
                            while (CarparkIterator.hasNext()) {
                                Map.Entry mapElement = (Map.Entry) CarparkIterator.next();
                                String CarparkId = (String) mapElement.getKey();

                                if(CarparkId.equals(b.getCarparkid())){
                                    carparks.put(CarparkId, carparks.get(CarparkId)+2);
                                }

                            }



                        }
                        for (Map.Entry mapElement : carparks.entrySet()) {
                            String key = (String)mapElement.getKey();

                            Log.i("hashmap 2",(key + " : " + mapElement.getValue()));
                        }

                        int max=0;
                        //find biggest value in the hashmap
                        for (Map.Entry mapElement : carparks.entrySet()) {
                            if((int)mapElement.getValue()>max){
                                max = (int) mapElement.getValue();
                                maxcarparkID = (String) mapElement.getKey();
                            }
                        }

                        myCallback.onCallback(maxcarparkID);
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


}

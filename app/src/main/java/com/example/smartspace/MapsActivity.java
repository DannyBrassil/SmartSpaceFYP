package com.example.smartspace;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;


import java.util.ArrayList;
import java.util.Collections;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    public static LatLng latLng;
    DatabaseReference fireDBUser = FirebaseDatabase.getInstance().getReference("carparks");

    View myView;
    boolean isUp;

     AdapterLocationList mAdapter;

     ArrayList<Carpark> carparks2 = new ArrayList<>();
    int freeSpaces=0;
     TextView title;
     TextView recommendTxt;
     TextView spacesLeft;
    String userId;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        title = (TextView) findViewById(R.id.RecommendedTitle);
         recommendTxt = (TextView)findViewById(R.id.WeRecommedText);
        spacesLeft =  (TextView)findViewById(R.id.spacesLeft);

        //  ParseCarParkMultistorey spaces = new ParseCarParkMultistorey();
        //    spaces.execute();

/* getting xml
        try {
            readCarParkdata();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
*/


//recycler view for list of carparks
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // prepare the dataset which sources the recycler view
        //
        final ArrayList<Carpark> myDataset = new ArrayList<Carpark>();

        // specify an adapter
         mAdapter = new AdapterLocationList(myDataset, this);

        //        //Add the divider line
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //        // set adapter for the recycler view
        mRecyclerView.setAdapter(mAdapter);




        populateRecyclerView();



        //sort by spinner
        final Spinner sortBySpinner = (Spinner)findViewById(R.id.sortbyspinner);
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1://sort by cheapest
                        //call class that sorts carparks in ascending price
                        SortByCheapest s = new SortByCheapest();
                        s.sort( new SortingCallback() {
                            @Override
                            public void onCallback(final ArrayList<Carpark> carparks) {
                                recyclerView(carparks);
                            }
                        });
                        break;
                    case 2://sort by expensive
                        //call class that sorts carparks in ascending price then reverse it
                        SortByCheapest s2 = new SortByCheapest();
                        s2.sort( new SortingCallback() {
                            @Override
                            public void onCallback(final ArrayList<Carpark> carparks) {
                                Collections.reverse(carparks);
                                recyclerView(carparks);
                            }
                        });
                        break;
                    case 3://sort by distance
                        SortByDistance s3 = new SortByDistance();
                        s3.sort(latLng,  new SortingCallback() {
                        @Override
                        public void onCallback(final ArrayList<Carpark> carparks) {
                        recyclerView(carparks);
                            }
                        });
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




         final RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.SlideLayout);
    // initialize slide up as invisible
        layout.setVisibility(View.INVISIBLE);
        slideUp(layout);
        isUp=true;

        Button close = findViewById(R.id.buttonSlideDown);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(layout);
                isUp=false;
            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        displayPricesOnMap();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
         userId= mUser.getUid();


        // TextView e1 = (TextView) findViewById(R.id.textView1)
//        e1.setText(mUser.getDisplayName());

        //when a marker is clicked bring up a popup showing details of that carpark
         mapFragment.getMapAsync(new OnMapReadyCallback() {
                                     @Override
                                     public void onMapReady(GoogleMap googleMap) {
                                         googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                             @Override
                                             public boolean onMarkerClick(Marker m) {
                                                 if (m.getPosition()==latLng) {

                                                 } else {

                                                     freeSpaces = 0;
                                                     final String position = m.getTag().toString();

                                                     fireDBUser.addValueEventListener(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot snapshot) {//every time change data the event listener
                                                             // will execute on datachange method for
                                                             for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                                 final Carpark carParkObj = userSnapshot.getValue(Carpark.class);

                                                                 if (carParkObj.getId().equals(position)) {
                                                                     if (isUp) {
                                                                         slideDown(layout);
                                                                     }


                                                                     //count number of free spaces in carpark
                                                                     if (carParkObj.isSmartspace() == true) {

                                                                         fireDBUser.child(carParkObj.getId()).child("Layout").addValueEventListener(new ValueEventListener() {
                                                                             @Override
                                                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                 for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                                                     Layout l = userSnapshot.getValue(Layout.class);
                                                                                     Log.i("spaces size", "" + l.getSpaces().size());
                                                                                     for (int i = 0; i < l.getSpaces().size(); i++) {
                                                                                         Log.i("spaces" + l.getSpaces().get(i).layoutposition, "");
                                                                                         if (l.getSpaces().get(i).isActive() == false) {
                                                                                             freeSpaces = freeSpaces + 1;
                                                                                         }
                                                                                     }
                                                                                 }
                                                                                 spacesLeft.setText("" + freeSpaces + " free spaces");
                                                                             }

                                                                             @Override
                                                                             public void onCancelled(@NonNull DatabaseError error) {

                                                                             }
                                                                         });
                                                                     }

                                                                     recommendTxt.setText("");
                                                                     title.setText(carParkObj.getName());
                                                                     slideUp(layout);
                                                                     isUp = true;

                                                                 }
                                                             }
                                                         }

                                                         @Override
                                                         public void onCancelled(@NonNull DatabaseError error) {

                                                         }
                                                     });



                                                 }return false;
                                             }

                                         });
                                     }
                                 });





        //bottom nav menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // home is selected
        bottomNavigationView.setSelectedItemId(R.id.mapsActivity);
        //item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.mapsActivity:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeMenu.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.car:
                        startActivity(new Intent(getApplicationContext(), car.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

    }



    //slide up recommended
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the down recommeded
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


    //disply horizonatal list of carparks
    private void recyclerView(ArrayList<Carpark> carparks) {
        mAdapter.clear();
        for(int i = 0; i<carparks.size(); i++){
            mAdapter.addItemtoend(carparks.get(i));
        }
    }

//enter all carparks into list when on create is called
    private void populateRecyclerView(){
        final ArrayList<Carpark> carparks = new ArrayList<>();
        fireDBUser.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//every time change data the event listener
                // will execute on datachange method for
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Carpark carParkObj = userSnapshot.getValue(Carpark.class);
                    carparks.add(carParkObj);
                }
                recyclerView(carparks);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DBError", "Cancel Access DB");
            }
        });

    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location != null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            MapsActivity.this, R.raw.style_json));



                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions mo= new MarkerOptions();
                            mo.position(latLng);
                            mo.title("Current Location");
                            mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                          //  googleMap.addMarker(mo); marker for google map crrent location
                            recommended();



                        }
                    });
                }
            }
        });
    }

    private void displayPricesOnMap() {

        //find the lowest price in each car park and add it to the map
        double smallest=0;
        fireDBUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//every time change data the event listener
                // will execute on datachange method for
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    final Carpark carParkObj = userSnapshot.getValue(Carpark.class);
                    Double min = carParkObj.getPrices().get(0).getPrice();

                    for(int i = 0; i<carParkObj.getPrices().size();i++){
                        if(carParkObj.getPrices().get(i).getPrice()<min){
                            min = carParkObj.getPrices().get(i).getPrice();
                        }
                    }

                    final Double finalMin = min;
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //adds icon to the map with the lowest price in the carpark as the text

                            IconGenerator iconGen = new IconGenerator(getApplicationContext());

                            iconGen.setTextAppearance(R.style.myStyleText);

                            int spacesLeftPercentage = (carParkObj.getSpaceAvailable()/carParkObj.getSpaces())*100;

                            if(spacesLeftPercentage<30){
                                iconGen.setColor(getResources().getColor(R.color.SpacesLeftRed));
                            }
                            else if(spacesLeftPercentage<60){
                                iconGen.setColor(getResources().getColor(R.color.SpacesLeftAmber));
                            }
                            else{
                                iconGen.setColor(getResources().getColor(R.color.SpacesLeftGreen));
                            }

                           // iconGen.setBackground(getResources().getDrawable(R.drawable.login_round));
                            MarkerOptions mo = new MarkerOptions();

                            LatLng latLngCarpark = new LatLng(carParkObj.getLatitude(), carParkObj.getLongitude());

                            mo.icon(BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon("€ "+finalMin.toString())));
                            mo.position(latLngCarpark);
                            mo.anchor(iconGen.getAnchorU(), iconGen.getAnchorV());



                            googleMap.addMarker(mo).setTag(carParkObj.getId());


                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }



    /*
    public void readCarParkdata() throws ParserConfigurationException, IOException, SAXException {
        String url="https://data.gov.ie/dataset/multi-story-car-parking-space-availability/resource/a5012481-97a2-45f8-b735-1f7f0e7ba4ce/view/a52f3ec5-a4f1-4d5b-93d9-c492922de1e2";
        //URLConnection conn = url.openConnection();
     //   File xmlFile = new File("https://data.gov.ie/dataset/multi-story-car-parking-space-availability/resource/a5012481-97a2-45f8-b735-1f7f0e7ba4ce/view/a52f3ec5-a4f1-4d5b-93d9-c492922de1e2");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(url);

        NodeList carparks = doc.getElementsByTagName("carpark");

        for (int i = 0; i < carparks.getLength(); i++) {
            Node carpark = carparks.item(i);
            Log.w("carparks", carpark.getNodeName());

            if (carpark.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) carpark;

                Node name = elem.getElementsByTagName("carpark").item(0);
                String CarparkName = name.getTextContent();
                Log.w("carpark name", CarparkName);

            }
        }
    }
*/

    //distance between two Coordinates
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private ArrayList<Carpark> sortedCarparks(ArrayList<Carpark> c){
        ArrayList<Carpark> sortedlist = new ArrayList<Carpark>();
        for(int i=0; i<=c.size(); i++){

        }
        return sortedlist;
    }


private void recommended(){
        Recommended r = new Recommended();
         r.recommend(latLng, userId, new MyCallback() {
            @Override
            public void onCallback(final String value) {
                Log.i("final s", value);

                fireDBUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            final Carpark carParkObj = userSnapshot.getValue(Carpark.class);
                            if (carParkObj.getId().equals(value)) {
                                title.setText(carParkObj.getName());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });






        /*



        fireDBUser.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Carpark c = dataSnapshot.getValue(Carpark.class);

                    carparks2.add(c);
                    Log.i("carparks2 name", ""+ c.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.i("carparks2 size", ""+ carparks2.size());


    //bubble sort to sort carparks from asscending distance away from user
        for(int i =0;i<carparks2.size()-1;i++){
            for (int j = 0; j < carparks2.size() - i - 1; j++) {
                if(carparks2.get(j).distance(carparks2.get(j).getLatitude(), carparks2.get(j).getLongitude(), latLng.latitude, latLng.longitude) > carparks2.get(j+1).distance(carparks.get(j+1).getLatitude(), carparks2.get(j+1).getLongitude(), latLng.latitude, latLng.longitude)){
                    Carpark temp = carparks2.get(j);

                    carparks2.set(j, carparks2.get(j+1));

                    carparks2.set(j+1, temp);
                }
            }

        }

        //title.setText(""+carparks2.size());
    Log.i("carparks2 size after", ""+ carparks2.size());

         */
}


}

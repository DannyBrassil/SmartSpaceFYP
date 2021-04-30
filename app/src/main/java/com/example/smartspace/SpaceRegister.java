package com.example.smartspace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class SpaceRegister extends AppCompatActivity {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    SearchView searchview;


    LatLng coordinates;
    Address carparkAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_register);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // mapFragment.getMapAsync(this);

        // name, email and passowrd from create account
         final Bundle bundle = getIntent().getParcelableExtra("bundle");
        final String name = bundle.getString("name");

        TextView heading = (TextView) findViewById(R.id.textView1);
        heading.setText("Select the location of "+name);




        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            updatemap();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //go to space register 2
        Button buttonforward = (Button) findViewById(R.id.forwardbutton);
        buttonforward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i =new Intent(SpaceRegister.this, SpaceRegister2.class);
                Bundle args = new Bundle();
                args.putParcelable("Location", coordinates);
                args.putParcelable("Address", carparkAddress);
                i.putExtra("bundle", args);
                i.putExtra("bundle1", bundle);
                startActivity(i);
            }
        });

        //go back to create carpark account
        Button buttonback = (Button) findViewById(R.id.backbutton);
        buttonback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i =new Intent(SpaceRegister.this, CreateCarparkAccount.class);
                startActivity(i);
            }
        });



    }

    public void updatemap() {
        final EditText e1 = (EditText) findViewById(R.id.editTextTextPersonName2);
        final EditText e2 = (EditText) findViewById(R.id.editTextTextPersonName4);
        final EditText e3 = (EditText) findViewById(R.id.editTextTextPersonName3);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                searchview = findViewById(R.id.sv_location);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                searchview.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        String location = searchview.getQuery().toString();
                        List<Address> addressList = null;

                        if (location != null || !location.equals("")) {
                            googleMap.clear();
                            Geocoder geocoder = new Geocoder(SpaceRegister.this);
                            try {
                                addressList = geocoder.getFromLocationName(location, 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Address address = addressList.get(0);
                            carparkAddress= address;
                            e1.setText(address.getLocality());
                            e2.setText(address.getAdminArea());
                            e3.setText(address.getAddressLine(0));
                            LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                            coordinates= latlng;
                            googleMap.addMarker(new MarkerOptions().position(latlng).title(location).draggable(true));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 11));
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                }));//end serach view


                //on map click listener
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.clear();
                        List<Address> addresses = null;
                        Geocoder geocoder = new Geocoder(SpaceRegister.this);
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(addresses!=null) {
                            Address address = addresses.get(0);
                            e1.setText(address.getLocality());
                            e2.setText(address.getAdminArea());
                            e3.setText(address.getAddressLine(0));
                            carparkAddress= address;
                        }
                        MarkerOptions mo = new MarkerOptions();
                        mo.position(latLng);
                        coordinates= latLng;

                        mo.title(latLng.latitude+" : "+ latLng.longitude);
                     //   googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        googleMap.addMarker(mo);

                    }

                });

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.


     @Override public void onMapReady(GoogleMap googleMap) {
     mMap = googleMap;

     // Add a marker in Sydney and move the camera
     LatLng sydney = new LatLng(-34, 151);
     mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
     mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
     }
     */
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
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions mo= new MarkerOptions();
                            mo.position(latLng);
                            mo.title("Current Location");
                            mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));

                            //googleMap.addMarker(mo);


                        }
                    });
                }
            }
        });

    }


}
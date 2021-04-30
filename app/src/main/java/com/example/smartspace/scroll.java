package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class scroll extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference fireDB;

    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;

    //store an image
    Uri imageUri;
    ImageView i1;
    FirebaseStorage storage;
    StorageReference storageReference;


    boolean correctInput=true;

    //personal details variables
    String email;//email
    String password;//password
    String passwordConfirm;
    EditText e1;//carpark email
    EditText e2;//carpark password
    EditText e3;//carpark confrim password

    //carpark details
    String carParkName, carParkSpaces, carParkFloors;
    int numOfSpaces;
    int numOfFloors;
    EditText e4;//carpark name
    EditText e5;//carpark spaces
    EditText e7;//carpark floors
    ToggleButton smartspaceButton;//smartspace toggle button
    boolean smartspace=true;

    //carpark description
    EditText e8;//carpark description
    String carParkDescription;
   public static ArrayList<String> SelectedAmmenity;
   public static EditText ammenityBox;

    //catpark location
    EditText AddressLine1;
    EditText AddressLine2;
    EditText AddressLine3;
    EditText AddressLine4;
    LatLng carparkLocation = null;
    List<Address> address;
    
    //carpark prices
    EditText TextPrice1,TextPrice2,TextPrice3,TextPrice4,TextPrice5,TextPrice6;
    EditText hour1,hour2,hour3,hour4,hour5,hour6;
    String price1, price2, price3, price4, price5, price6;
    String time1,time2,time3,time4,time5,time6;
    double Price1, Price2,Price3,Price4,Price5,Price6;
    int Time1, Time2, Time3,Time4,Time5,Time6;
    ArrayList<CarParkPrice> prices = new ArrayList<CarParkPrice>();

    //carparktimes
    Spinner mondayStart, mondayfinish, tuesdayStart, tuesdayFinish, wednesdayStart, wednesdayFinish, thursdayStart, thursdayFinish, fridayStart, fridayFinish, saturdayStart, saturdayFinish, sundayStart, sundayFinish;

    String MondayStartTime, TuesdayStartTime, WednesdayStartTime, ThursdayStartTime, FridayStartTime, SaturdayStartTime, SundayStartTime;
    String MondayEndTime, TuesdayEndTime, WednesdayEndTime, ThursdayEndTime, FridayEndTime, SaturdayEndTime, SundayEndTime;

    CheckBox mondayClosed, tuesdayClosed, wednesdayClosed, thursdayClosed, fridayClosed, saturdayClosed, sundayClosed;
    CheckBox monday24, tuesday24, wednesday24, thursday24, friday24, saturday24, sunday24;

    ArrayList<CarParkTime> times = new ArrayList<CarParkTime>();

    Button saveDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

       final String email = getIntent().getStringExtra("UserEmail");
        final String password = getIntent().getStringExtra("UserPassword");
        final String mUserUid = getIntent().getStringExtra("UserID");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

         ammenityBox = (EditText)findViewById(R.id.ammenity_textbox);

         AddressLine1 = (EditText)findViewById(R.id.address1);
       AddressLine2 = (EditText)findViewById(R.id.address2);
        AddressLine3 = (EditText)findViewById(R.id.address3);
        AddressLine4 = (EditText)findViewById(R.id.address4);
         saveDetails = (Button)findViewById(R.id.RegisterCarparkButton);


         //onclick listeners for times
        //monday
        mondayStart = (Spinner)findViewById(R.id.MondayTimeFrom);


        mondayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mondayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MondayStartTime = mondayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mondayfinish = (Spinner)findViewById(R.id.MondayTimeTill);
        mondayfinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MondayEndTime = mondayfinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //tuesday
        tuesdayStart = (Spinner)findViewById(R.id.TuesdayTimeFrom);
        tuesdayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TuesdayStartTime = tuesdayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tuesdayFinish = (Spinner)findViewById(R.id.TuesdayTimeTill);
        tuesdayFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TuesdayEndTime = tuesdayFinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //wednesday
        wednesdayStart = (Spinner)findViewById(R.id.WednesdayTimeFrom);
        wednesdayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WednesdayStartTime = wednesdayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        wednesdayFinish = (Spinner)findViewById(R.id.WednesdayTimeTill);
        wednesdayFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WednesdayEndTime = wednesdayFinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //thursday
        thursdayStart = (Spinner)findViewById(R.id.ThursdayOpenFrom);
        thursdayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThursdayStartTime = thursdayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        thursdayFinish = (Spinner)findViewById(R.id.ThursdayOpenTill);
        thursdayFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThursdayEndTime = thursdayFinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //friday
        fridayStart = (Spinner)findViewById(R.id.FridayOpenFrom);
        fridayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FridayStartTime = fridayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fridayFinish = (Spinner)findViewById(R.id.FridayOpenTill);
        fridayFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MondayEndTime = mondayfinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //saturday
        saturdayStart = (Spinner)findViewById(R.id.SaturdayOpenFrom);
        saturdayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SaturdayStartTime = saturdayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        saturdayFinish = (Spinner)findViewById(R.id.SaturdayOpenTill);
        saturdayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SaturdayEndTime = saturdayFinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //sunday
        sundayStart = (Spinner)findViewById(R.id.SundayOpenFrom);
        sundayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MondayStartTime = mondayStart.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sundayFinish = (Spinner)findViewById(R.id.SundayOpenFrom);
        sundayStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SundayEndTime = sundayFinish.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //on map click listener
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                //on map click listener
                //get cooradinates of where user click on map and set the address text to the address of the
                //coordiates using geocoder
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.clear();
                        List<Address> addresses = null;
                        Geocoder geocoder = new Geocoder(scroll.this);
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(addresses!=null) {
                            Address address = addresses.get(0);
                            AddressLine1.setText(address.getLocality());
                            AddressLine2.setText(address.getAdminArea());
                            AddressLine3.setText(address.getAddressLine(0));
                            AddressLine4.setText(address.getAddressLine(1));
                        }
                        MarkerOptions mo = new MarkerOptions();
                        mo.position(latLng);


                        mo.title(latLng.latitude+" : "+ latLng.longitude);
                        //   googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        googleMap.addMarker(mo);

                    }

                });

            }
        });

        //add an image
        Button addImage = (Button) findViewById(R.id.add_image_button3);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(i,1);
            }
        });

        //smartspace
        smartspaceButton =  (ToggleButton)findViewById(R.id.SmartSpaceToggle);
        final String ss = smartspaceButton.getText().toString();

        smartspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smartspaceButton.isChecked()){
                    smartspace=true;
                }
                else{
                    smartspace=false;
                }
            }
        });





        //add ameniteies
        Button addAmeinity = (Button) findViewById(R.id.add_ammenities2);
        addAmeinity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAmmenitiesDialog dialog = new AddAmmenitiesDialog(mUser.getUid());
                dialog.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "Amenity dialog");


            }
        });




        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  getPersonalDetails();
                getCarParkDetails();
                getAddress();
                getCarparkDescription();
                getCarparkPrices();
                getCarparkTimes();


                if (correctInput) {
                    fireDB = FirebaseDatabase.getInstance().getReference();



                    ArrayList<Layout> l;
                    l=null;

                    Carpark carpark = new Carpark(mUserUid, email, password, carParkName, carparkLocation.longitude, carparkLocation.latitude, numOfSpaces, numOfSpaces, numOfFloors, smartspace, false, prices, null, times, carParkDescription, SelectedAmmenity,null);
                    fireDB.child("carparks").child(mUserUid).setValue(carpark).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(scroll.this, "Write is successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(scroll.this, CreateCarparkLayout.class);
                            i.putExtra("id",mUserUid);
                            Log.i("scroll intent", "id before intent in scroll"+mUserUid);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(scroll.this, "Write is not successful", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //not the correct input
                else {
                    Toast.makeText(scroll.this, "There was a problem. Ensure all details are correct", Toast.LENGTH_LONG).show();
                    correctInput=true;
                }
            }
        });







    }

    private void getAddress() {
        //List<Address> addresses2 = null;
        //Address carparkAddress = addresses2.get(0);
        String Addressline="";


        if(AddressLine1.getText().toString().isEmpty() && AddressLine2.getText().toString().isEmpty() && AddressLine3.getText().toString().isEmpty() && AddressLine4.getText().toString().isEmpty()){
            AddressLine1.setError("Enter Address");
            AddressLine1.requestFocus();
        }
        else{
            if(!(AddressLine1.getText().toString().isEmpty())){
               // carparkAddress.setAddressLine(0,AddressLine1.getText().toString());
                Addressline=Addressline+AddressLine1.getText().toString();
            }
            if(!(AddressLine2.getText().toString().isEmpty())){
               // carparkAddress.setAddressLine(1,AddressLine2.getText().toString());
                Addressline=Addressline+AddressLine2.getText().toString();
            }
            if(!(AddressLine3.getText().toString().isEmpty())){
               // carparkAddress.setAddressLine(2,AddressLine3.getText().toString());
                Addressline=Addressline+AddressLine3.getText().toString();
            }
            if(!(AddressLine4.getText().toString().isEmpty())){
                //carparkAddress.setAddressLine(3,AddressLine4.getText().toString());
                Addressline=Addressline+AddressLine4.getText().toString();
            }
        }
        Geocoder coder = new Geocoder(scroll.this);



        try {
            // May throw an IOException
            address = coder.getFromLocationName(Addressline, 5);
            if (address == null) {

            }

            Address location = address.get(0);
            carparkLocation = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }


        //carparkAddress.setLocality(AddressLine1.getText().toString());
        //carparkAddress.setAdminArea(AddressLine1.getText().toString());
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

    private void getCarparkTimes() {
        CarParkTime ct1 = new CarParkTime();
        CarParkTime ct2 = new CarParkTime();
        CarParkTime ct3 = new CarParkTime();
        CarParkTime ct4 = new CarParkTime();
        CarParkTime ct5 = new CarParkTime();
        CarParkTime ct6 = new CarParkTime();
        CarParkTime ct7 = new CarParkTime();

        mondayStart = (Spinner)findViewById(R.id.MondayTimeFrom);
        MondayStartTime = mondayStart.getSelectedItem().toString();
        mondayfinish = (Spinner)findViewById(R.id.MondayTimeTill);
        MondayEndTime = mondayfinish.getSelectedItem().toString();

        tuesdayStart = (Spinner)findViewById(R.id.TuesdayTimeFrom);
        TuesdayStartTime = tuesdayStart.getSelectedItem().toString();
        tuesdayFinish = (Spinner)findViewById(R.id.TuesdayTimeTill);
        TuesdayEndTime = tuesdayFinish.getSelectedItem().toString();

        wednesdayStart = (Spinner)findViewById(R.id.WednesdayTimeFrom);
        WednesdayStartTime = wednesdayStart.getSelectedItem().toString();
        wednesdayFinish = (Spinner)findViewById(R.id.WednesdayTimeTill);
        WednesdayEndTime = wednesdayFinish.getSelectedItem().toString();

        thursdayStart = (Spinner)findViewById(R.id.ThursdayOpenFrom);
        ThursdayStartTime = thursdayStart.getSelectedItem().toString();
        thursdayFinish = (Spinner)findViewById(R.id.ThursdayOpenTill);
        ThursdayEndTime = thursdayFinish.getSelectedItem().toString();

        fridayStart = (Spinner)findViewById(R.id.FridayOpenFrom);
        FridayStartTime = fridayStart.getSelectedItem().toString();
        fridayFinish = (Spinner)findViewById(R.id.FridayOpenTill);
        FridayEndTime = fridayFinish.getSelectedItem().toString();

        saturdayStart = (Spinner)findViewById(R.id.SaturdayOpenFrom);
        SaturdayStartTime = saturdayStart.getSelectedItem().toString();
        saturdayFinish = (Spinner)findViewById(R.id.SaturdayOpenTill);
        SaturdayEndTime = saturdayFinish.getSelectedItem().toString();

        sundayStart = (Spinner)findViewById(R.id.SundayOpenFrom);
        SundayStartTime = sundayStart.getSelectedItem().toString();
        sundayFinish = (Spinner)findViewById(R.id.SundayOpenTill);
        SundayEndTime = sundayFinish.getSelectedItem().toString();

        mondayClosed= (CheckBox)findViewById(R.id.MondayClosed);
        tuesdayClosed= (CheckBox)findViewById(R.id.TuesdayClosed);
        wednesdayClosed= (CheckBox)findViewById(R.id.WednesdayClosed);
        thursdayClosed= (CheckBox)findViewById(R.id.ThursdayClosed);
        fridayClosed= (CheckBox)findViewById(R.id.FridayClosed);
        saturdayClosed= (CheckBox)findViewById(R.id.SaturdayClosed);
        sundayClosed= (CheckBox)findViewById(R.id.SundayClosed);

        monday24= (CheckBox)findViewById(R.id.Monday24);
        tuesday24= (CheckBox)findViewById(R.id.Tuesday24);
        wednesday24= (CheckBox)findViewById(R.id.Wednesday24);
        thursday24= (CheckBox)findViewById(R.id.Thursday24);
        friday24= (CheckBox)findViewById(R.id.Friday24);
        saturday24= (CheckBox)findViewById(R.id.Saturday24);
        sunday24= (CheckBox)findViewById(R.id.Sunday24);

        addCarparkTimes(MondayStartTime, MondayEndTime, mondayClosed, monday24, ct1);
        addCarparkTimes(TuesdayStartTime, TuesdayEndTime, tuesdayClosed, tuesday24, ct2);
        addCarparkTimes(WednesdayStartTime, WednesdayEndTime, wednesdayClosed, wednesday24, ct3);
        addCarparkTimes(ThursdayStartTime, ThursdayEndTime, thursdayClosed, thursday24, ct4);
        addCarparkTimes(FridayStartTime, FridayEndTime, fridayClosed, friday24, ct5);
        addCarparkTimes(SaturdayStartTime, SaturdayEndTime, saturdayClosed, saturday24, ct6);
        addCarparkTimes(SundayStartTime, SundayEndTime, sundayClosed, sunday24, ct7);



    }

    //adds carpark time object to an arraylist for the final carpark object
    private void addCarparkTimes(String start, String end, CheckBox closed, CheckBox open, CarParkTime cpt){
        if(start.equalsIgnoreCase("00:00") && end.equalsIgnoreCase("00:00")){
            if(closed.isChecked()){
                cpt.setStart(null);
                cpt.setEnd(null);
                cpt.setClosed(true);
                cpt.setHours24(false);
            }
            else if(open.isChecked()){
                cpt.setStart(null);
                cpt.setEnd(null);
                cpt.setClosed(false);
                cpt.setHours24(true);
            }
            else if(open.isChecked() && closed.isChecked()){
                open.setError("Only one box can be ticked");
                open.requestFocus();
            }
            else{
                open.setError("Select an option");
                open.requestFocus();
            }
        }
        else{
            cpt.setStart(MondayStartTime);
            cpt.setEnd(MondayEndTime);
            cpt.setClosed(false);
            cpt.setHours24(false);
        }
        times.add(cpt);
    }

    private void getCarparkPrices() {
        //prices
        TextPrice1 = (EditText) findViewById(R.id.PriceEntry1);
        TextPrice2 = (EditText) findViewById(R.id.PriceEntry2);
        TextPrice3 = (EditText) findViewById(R.id.PriceEntry3);
        TextPrice4 = (EditText) findViewById(R.id.PriceEntry4);
        TextPrice5 = (EditText) findViewById(R.id.PriceEntry5);
        TextPrice6 = (EditText) findViewById(R.id.PriceEntry6);

        //hours
        hour1=  (EditText) findViewById(R.id.HourEntry1);
        hour2=  (EditText) findViewById(R.id.HourEntry2);
        hour3=  (EditText) findViewById(R.id.HourEntry3);
        hour4=  (EditText) findViewById(R.id.HourEntry4);
        hour5=  (EditText) findViewById(R.id.HourEntry5);
        hour6=  (EditText) findViewById(R.id.HourEntry6);

       // get textas string fro price and hours
        price1 = TextPrice1.getText().toString();
        price2 = TextPrice2.getText().toString();
        price3 = TextPrice3.getText().toString();
        price4 = TextPrice4.getText().toString();
        price5 = TextPrice5.getText().toString();
        price6 = TextPrice6.getText().toString();

        time1 = hour1.getText().toString();
        time2 = hour2.getText().toString();
        time3 = hour3.getText().toString();
        time4 = hour4.getText().toString();
        time5 = hour5.getText().toString();
        time6 = hour6.getText().toString();

        if(!(price1.isEmpty() || time1.isEmpty())){
            Price1 =  Double.parseDouble(price1);
            Time1 = Integer.parseInt(time1);
            CarParkPrice cp1 = new CarParkPrice(Time1, Price1);
            prices.add(cp1);
        }

        if(!(price2.isEmpty() || time2.isEmpty())){
            Price2 = Double.parseDouble(price2);
            Time2 = Integer.parseInt(time2);
            CarParkPrice cp2 = new CarParkPrice(Time2, Price2);
            prices.add(cp2);
        }

        if(!(price3.isEmpty() || time3.isEmpty())){
            Price3 = Double.parseDouble(price3);
            Time3 = Integer.parseInt(time3);
            CarParkPrice cp3 = new CarParkPrice(Time3, Price3);
            prices.add(cp3);
        }
        if(!(price4.isEmpty() || time4.isEmpty())){
            Price4 = Double.parseDouble(price4);
            Time4 = Integer.parseInt(time4);
            CarParkPrice cp4 = new CarParkPrice(Time4, Price4);
            prices.add(cp4);
        }

        if(!(price5.isEmpty() || time5.isEmpty())){
            Price5 = Double.parseDouble(price5);
            Time5 = Integer.parseInt(time5);
            CarParkPrice cp5 = new CarParkPrice(Time5, Price5);
            prices.add(cp5);
        }

        if(!(price6.isEmpty() || time6.isEmpty())){
            Price6 = Double.parseDouble(price6);
            Time6 = Integer.parseInt(time6);
            CarParkPrice cp6 = new CarParkPrice(Time6, Price6);
            prices.add(cp6);
        }



    }

    private void getCarparkDescription() {
        e8=  (EditText) findViewById(R.id.CarParkDescription);
        carParkDescription = e8.getText().toString();
    }

    private void getCarParkDetails() {

        e4 = (EditText) findViewById(R.id.carParkName);
        e5 = (EditText) findViewById(R.id.carpParkSpaces);
        e7=  (EditText) findViewById(R.id.CarParkFloors);

        carParkName=e4.getText().toString();
         carParkSpaces =e5.getText().toString();
         carParkFloors =e7.getText().toString();

        if (carParkName.isEmpty()) {
            correctInput=false;
            e4.setError("Enter Carpark name");
            e4.requestFocus();
        } else if (carParkSpaces.isEmpty()) {
            correctInput=false;
            e5.setError("Enter password");
            e5.requestFocus();
        } else if (carParkFloors.isEmpty()) {
            correctInput=false;
            e7.setError("Enter Confirm Password");
            e7.requestFocus();
        }


        try {
            numOfSpaces = Integer.parseInt(carParkSpaces);
        } catch (NumberFormatException e) {
            correctInput=false;
            e5.setError("This must be a number");
            e5.requestFocus();
        }

        try {
            numOfFloors = Integer.parseInt(carParkFloors);
        } catch (NumberFormatException e) {
            correctInput=false;
            e7.setError("This must be a number");
            e7.requestFocus();
        }


    }

    private void getPersonalDetails() {

        e1 = (EditText) findViewById(R.id.CarpParkEmail);
        e2 = (EditText) findViewById(R.id.CarParkPassword);
        e3 = (EditText) findViewById(R.id.CarParkPasswordConfirm);

        email = e1.getText().toString();
        password = e2.getText().toString();
        passwordConfirm = e3.getText().toString();

        if (email.isEmpty()) {
            correctInput=false;
            e1.setError("Enter email");
            e1.requestFocus();
        } else if (password.isEmpty()) {
            correctInput=false;
            e2.setError("Enter password");
            e2.requestFocus();
        } else if (passwordConfirm.isEmpty()) {
            correctInput=false;
            e3.setError("Enter Confirm Password");
            e3.requestFocus();
        } else if (!passwordConfirm.equals(password)) {
            correctInput=false;
            e3.setError("password does not match");
            e3.requestFocus();
        } else if (!(email.isEmpty() && password.isEmpty() && passwordConfirm.isEmpty()) && passwordConfirm.equals(password)) {


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(scroll.this, "signup unsuccessful, personal details problem", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(scroll.this, "sign up success", Toast.LENGTH_LONG).show();


                            }

                        }
                    });
        }
    }

    private static Object tryParse(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException ex) { /* no-op */ }

        try {
            return Double.valueOf(str);
        } catch (NumberFormatException ex) { /* no-op */ }

        return str;
    }


    //when image is selected by user
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        try{
            InputStream is = getContentResolver().openInputStream(imageUri);
            final Bitmap selected_image = BitmapFactory.decodeStream(is);
            i1.setImageBitmap(selected_image);
            uploadImage();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //upload image to database
    private void uploadImage() {

        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(scroll.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(scroll.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
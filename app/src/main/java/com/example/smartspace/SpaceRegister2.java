package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class SpaceRegister2 extends AppCompatActivity {

    DatabaseReference fireDB;
    boolean smartspace=false;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Uri imageUri;
    ImageView i1;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_register2);

        Bundle bundle = getIntent().getParcelableExtra("bundle");// latlng coordinates, carpark address
        Bundle bundle1 = getIntent().getParcelableExtra("bundle1");//name, email and passowrd from create account

        final LatLng location = bundle.getParcelable("Location");
        Address address= bundle.getParcelable("Address");

        final String email= bundle1.get("email").toString();
        final String name= bundle1.get("name").toString();
        final String password= bundle1.get("password").toString();


        Button addImage = (Button) findViewById(R.id.add_image_button);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(i,1);
            }
        });


        i1= (ImageView)findViewById(R.id.imageView2);


        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(i,1);
            }
        });

         storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        Switch sw = (Switch) findViewById(R.id.smartSpaceSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    smartspace=true;
                } else {
                    // The toggle is disabled
                    smartspace=false;
                }
            }
        });

        //skip signup to test carpark layout
      /*  Button create = (Button) findViewById(R.id.createspacebutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(SpaceRegister2.this, CreateCarparkLayout.class);
                startActivity(i);
            }
        });



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Button create = (Button) findViewById(R.id.forwardbutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText et2 = (EditText) findViewById(R.id.numofspaces);
                int space = Integer.parseInt(et2.getText().toString());


                fireDB= FirebaseDatabase.getInstance().getReference();
                Carpark carpark = new Carpark(mUser.getUid(),email, password, name, location.longitude, location.latitude, space, space, smartspace);
                fireDB.child("carparks").child(mUser.getUid()).setValue(carpark).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SpaceRegister2.this, "Write is successful", Toast.LENGTH_LONG).show();
                        Intent i =new Intent(SpaceRegister2.this, CreateCarparkLayout.class);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SpaceRegister2.this, "Write is not successful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        Button addAmeinity = (Button) findViewById(R.id.add_ammenities);
        addAmeinity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAmmenitiesDialog dialog = new AddAmmenitiesDialog(mUser.getUid());
                dialog.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "Amenity dialog");


            }
        });

    }

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
                            Toast.makeText(SpaceRegister2.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SpaceRegister2.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
       */
    }

}
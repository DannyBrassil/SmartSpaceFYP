package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CreateCarparkLayout extends AppCompatActivity {

    String tile = "";
    String size = "";

    int number = 0;
    int spacesinDB=0;
    int spacesCreated=0;
    int floors=0;
    int currentFloor=0;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ArrayList<tile> tiles= new ArrayList<tile>();
    ArrayList<Space> spaces= new ArrayList<Space>();
    int col=0;
    int row=0;
    int height=0;
    int width=0;
    TextView spacesleft;
    TextView floorsleft;
    String id;

    DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("carparks");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_carpark_layout);

        Bundle bd;
        bd = getIntent().getExtras();
        if (bd != null) {
            spacesCreated = bd.getInt("SpacesCreated");
            currentFloor = bd.getInt("floor");
            id = bd.getString("id");
            Log.i("intent", "intent recieved from activity scroll");
            Log.i("id", ""+id);
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //save button
        final Button create = (Button) findViewById(R.id.save_layout_button);


         spacesleft = (TextView)findViewById(R.id.spacesLeftText);
         floorsleft = (TextView)findViewById(R.id.floorsLeftText);


        //spinner to get the tile selected
        Spinner aSpinner = findViewById(R.id.spinner_selected_tile);
        aSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                tile = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        //get number of spaces and floors in carpark

        fireDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Carpark c = userSnapshot.getValue(Carpark.class);

                    if(id.equalsIgnoreCase(c.getId())) {
                        spacesinDB = c.getSpaces();
                        floors = c.getFloors();
                        Log.i("db access", "spaces found"+spacesinDB);
                        spacesleft.setText("Spaces "+ spacesCreated+" / "+spacesinDB);
                        floorsleft.setText("Floors "+currentFloor+" / "+floors);
                        if(currentFloor<floors){
                            create.setText("Next Floor");
                        }
                        else{
                            create.setText("Save layout");
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        Spinner aSpinner2 = findViewById(R.id.spinner_grid_size);
        aSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                size = parentView.getItemAtPosition(position).toString();

                if (size.equalsIgnoreCase("small")) {
                    if(tiles!=null) {
                        tiles.clear();
                    }
                    number=0;
                    col=4;
                    row=6;
                    height=250;
                    width=250;
                    createGrid(col, row, height, width);

                } else if (size.equalsIgnoreCase("medium")) {
                    if(tiles!=null) {
                        tiles.clear();
                    }
                    number=0;
                    col=5;
                    row=8;
                    height=190;
                    width=190;
                    createGrid(col, row, height, width);

                } else if (size.equalsIgnoreCase("large")) {
                    if(tiles!=null) {
                        tiles.clear();
                    }
                    number=0;
                    col=8;
                    row=14;
                    height=120;
                    width=120;
                    createGrid(col, row, height, width);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




        //save grid to database
        create.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {

                Intent intent;

                for(int i =0; i<tiles.size();i++){
                    if(tiles.get(i).getType().equalsIgnoreCase("space") || tiles.get(i).getType().equalsIgnoreCase("disabled space")){
                        Space s = new Space(i, false);
                        spaces.add(s);
                    }
                }

            final Layout l = new Layout(col, row, height, width, tiles, spaces, currentFloor);

/*
                fireDB.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Carpark c = snapshot.getValue(Carpark.class);
                        c.getLayout().add(l);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

*/
         //add layout to database
                //.child(String.valueOf(currentFloor)).push
            fireDB.child(id).child("Layout").child(String.valueOf(currentFloor)).setValue(l).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CreateCarparkLayout.this, "Write is successful", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateCarparkLayout.this, "Write is not successful", Toast.LENGTH_LONG).show();
                }
            });



            if(create.getText().toString().equalsIgnoreCase("Save layout")){
                intent =new Intent(CreateCarparkLayout.this, MainActivity.class);
                startActivity(intent);
            }else{
                //else theres more floors so recall activity with updated data
                currentFloor++;
                intent =new Intent(CreateCarparkLayout.this, CreateCarparkLayout.class);
                intent.putExtra("SpacesCreated", spacesCreated);
                intent.putExtra("floor", currentFloor);
                intent.putExtra("id", id);
                startActivity(intent);
            }



        }
    });

}


    private void createGrid(int colCount, int rowCount, int height, int width) {
        GridLayout grid = (GridLayout) findViewById(R.id.Carparklayout);
        grid.removeAllViews();
        grid.setColumnCount(colCount);
        grid.setRowCount(rowCount);
        for (int j = 0; j < grid.getRowCount(); j++) {
            GridLayout.Spec row = GridLayout.spec(j);
            for (int i = 0; i < grid.getColumnCount(); i++) {
                GridLayout.Spec col = GridLayout.spec(i);

                TextView textView = new TextView(this);
                textView.setBackground(getResources().getDrawable(R.drawable.tile_border));
                textView.setText("" + number);


                //create tile object and add to arraylist of tiles
                tile t = new tile("empty tile", number);
                tiles.add(t);
                number++;
                textView.setPadding(25, 25, 25, 25);
                grid.addView(textView, new GridLayout.LayoutParams(row, col));

                GridLayout.LayoutParams params = (GridLayout.LayoutParams) textView.getLayoutParams();
                params.height = height;
                params.width = width;
                textView.setLayoutParams(params);


            }
        }
        int childCount = grid.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final TextView container = (TextView) grid.getChildAt(i);
            final int finalI = i;
            //onclick for each tile in grid
            container.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                public void onClick(View view) {


                    if (tile.equalsIgnoreCase("road")) {
                        if((tiles.get(finalI).getType().equalsIgnoreCase("space"))){
                            spacesCreated--;
                        }
                        container.setBackground(getResources().getDrawable(R.drawable.road));
                        tiles.get(finalI).setType("road");
                    } else if (tile.equalsIgnoreCase("grass")) {
                        if((tiles.get(finalI).getType().equalsIgnoreCase("space"))){
                            spacesCreated--;
                        }
                        container.setBackground(getResources().getDrawable(R.drawable.grass));
                        tiles.get(finalI).setType("grass");
                    } else if (tile.equalsIgnoreCase("disabled space")) {
                        if((tiles.get(finalI).getType().equalsIgnoreCase("space"))){
                            spacesCreated--;
                        }
                        container.setBackground(getResources().getDrawable(R.drawable.disabled));
                        tiles.get(finalI).setType("disabled space");
                    } else if (tile.equalsIgnoreCase("space")) {
                        container.setBackground(getResources().getDrawable(R.drawable.space_border));
                        tiles.get(finalI).setType("space");
                        container.setTextColor(getResources().getColor(R.color.parking_yellow));
                        spacesCreated++;
                        spacesleft.setText("spaces"+ spacesCreated+" / "+spacesinDB);
                    } else if (tile.equalsIgnoreCase("entrance")) {
                        if(!(tiles.get(finalI).getType().isEmpty())){
                            spacesCreated--;
                            spacesleft.setText("spaces"+ spacesCreated+" / "+spacesinDB);
                        }
                        container.setBackground(getResources().getDrawable(R.drawable.entrance));
                        tiles.get(finalI).setType("entrance");
                        container.setText("enter");
                    } else if (tile.equalsIgnoreCase("exit")) {
                        if((tiles.get(finalI).getType().equalsIgnoreCase("space"))){
                            spacesCreated--;
                            spacesleft.setText("spaces"+ spacesCreated+" / "+spacesinDB);
                        }
                        container.setBackground(getResources().getDrawable(R.drawable.exit));
                        tiles.get(finalI).setType("exit");
                        container.setText("exit");
                    } else if (tile.equalsIgnoreCase("empty tile")) {
                        if((tiles.get(finalI).getType().equalsIgnoreCase("space"))){
                            spacesCreated--;
                            spacesleft.setText("spaces"+ spacesCreated+" / "+spacesinDB);
                        }
                        container.setBackground(getResources().getDrawable(R.drawable.tile_border));
                        tiles.get(finalI).setType("empty tile");

                    }



                    Toast.makeText(CreateCarparkLayout.this, "position" + finalI, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
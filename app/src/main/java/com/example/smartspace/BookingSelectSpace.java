package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BookingSelectSpace extends AppCompatActivity {
    int col=0;
    int row=0;
    int height=0;
    int width=0;
    ArrayList<tile> tiles;

    DatabaseReference fireDB;
    String id;
    int space=0;
    int floorsInCarpark;
    int currentFloor=1;

    Button nextButton, previousButton;
    TextView currentFloorNumber, nextFloorNumber, previousFloorNumber;
    ArrayList<Layout> layouts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_select_space);



        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        id = b.getString("id");

        nextButton=findViewById(R.id.button3);
        previousButton=findViewById(R.id.button4);
        currentFloorNumber=findViewById(R.id.textView90);
        nextFloorNumber=findViewById(R.id.textView92);
        previousFloorNumber=findViewById(R.id.textView91);

        fireDB = FirebaseDatabase.getInstance().getReference("carparks");

       getGridDetails(currentFloor);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFloor=currentFloor+1;
                updateButtons(currentFloor);
                getGridDetails(currentFloor);
            }


        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFloor=currentFloor-1;
                updateButtons(currentFloor);
                getGridDetails(currentFloor);
            }
        });
    }

    private void getGridDetails(final int currentFloor) {
        fireDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Carpark c = userSnapshot.getValue(Carpark.class);
                    if (c.getId().equalsIgnoreCase(id)) {
                        floorsInCarpark = c.getFloors();

                        fireDB.child(id).child("Layout").addValueEventListener(new ValueEventListener() {


                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.i("Count " ,""+snapshot.getChildrenCount());


                                for( DataSnapshot postSnapshot : snapshot.getChildren()){
                                    Layout l = postSnapshot.getValue(Layout.class);
                                    layouts.add(l);
                                }

                                if(currentFloor-1<layouts.size()){
                                    int nextFloor = currentFloor+1;
                                    nextFloorNumber.setText("next");
                                    nextFloorNumber.setVisibility(View.VISIBLE);
                                    nextButton.setVisibility(View.VISIBLE);
                                }


                                Layout l = snapshot.child(String.valueOf(currentFloor-1)).getValue(Layout.class);
                                // Log.i("floor " ,""+snapshot.getChildrenCount());

                                row = l.getRows();
                                col = l.getColumns();
                                height = l.getHeight();
                                width = l.getWidth();
                                tiles = l.getTiles();

                                createGrid(col, row, height, width, tiles);
                            }
                            //}

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void updateButtons(int currentFloor) {
        currentFloorNumber.setText("Floor "+currentFloor);

        if(currentFloor <layouts.size()){
            int nextFloor = currentFloor ;
            nextFloorNumber.setText("Next");
            nextFloorNumber.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        } else{
            nextFloorNumber.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
        }

        if(currentFloor-1 >0){
            int previousFloor = currentFloor ;
            previousFloorNumber.setText("Previous");
            previousFloorNumber.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
        }
        else{
            previousFloorNumber.setVisibility(View.INVISIBLE);
            previousButton.setVisibility(View.INVISIBLE);
        }
    }


    private void createGrid(int colCount, int rowCount, int height, int width, final ArrayList<tile> tiles) {
        int number=0;
        GridLayout grid = (GridLayout) findViewById(R.id.Carparklayout2);
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


            if (tiles.get(finalI).getType().equalsIgnoreCase("road")) {
                container.setBackground(getResources().getDrawable(R.drawable.road));
            } else if (tiles.get(finalI).getType().equalsIgnoreCase("grass")) {
                container.setBackground(getResources().getDrawable(R.drawable.grass));
            } else if (tiles.get(finalI).getType().equalsIgnoreCase("disabled space")) {
                container.setBackground(getResources().getDrawable(R.drawable.disabled));
            } else if (tiles.get(finalI).getType().equalsIgnoreCase("space")) {

                container.setBackground(getResources().getDrawable(R.drawable.space_border));

                fireDB.child(id).child("Layout").child(String.valueOf(currentFloor-1)).child("spaces").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Space space = ds.getValue(Space.class);
                            if(finalI==space.getLayoutposition()){
                                if(space.isActive()==true){
                                    container.setBackground(getResources().getDrawable(R.drawable.redcar));
                                }
                            }


                        }
                       /*
                        GenericTypeIndicator<ArrayList<Space>> t = new GenericTypeIndicator<ArrayList<Space>>() {};
                        ArrayList<Space> spaces = snapshot.getValue(t);
                        snapshot.getChildren().iterator().
                        for(int i=0;i<spaces.size();i++){
                            //if the tile clicked is a space
                            if(finalI==spaces.get(i).layoutposition){

                                if(spaces.get(i).active==true){
                                    container.setBackground(getResources().getDrawable(R.drawable.redcar));
                                }
                            }
                        }
                        */
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                container.setTextColor(getResources().getColor(R.color.parking_yellow));
            } else if (tiles.get(finalI).getType().equalsIgnoreCase("entrance")) {
                container.setBackground(getResources().getDrawable(R.drawable.entrance));
                container.setText("enter");
            } else if (tiles.get(finalI).getType().equalsIgnoreCase("exit")) {
                container.setBackground(getResources().getDrawable(R.drawable.exit));
                container.setText("exit");
            } else if (tiles.get(finalI).getType().equalsIgnoreCase("empty tile")) {
                container.setBackground(getResources().getDrawable(R.drawable.tile_border));
            }


            //onclick for each tile in grid
            container.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                public void onClick(View view) {

                    fireDB = FirebaseDatabase.getInstance().getReference("carparks");

                    fireDB.child(id).child("Layout").child(String.valueOf(currentFloor-1)).child("spaces").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Space spaces = ds.getValue(Space.class);

                                //if the tile clicked is a space
                                if(finalI==spaces.layoutposition){

                                    if(spaces.isActive()==true){
                                        Toast.makeText(BookingSelectSpace.this, "Space is occupied, choose another", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        space= spaces.layoutposition;
                                        Intent intent = new Intent(BookingSelectSpace.this, ConfirmBooking.class);
                                        intent.putExtra("space", space);
                                        intent.putExtra("id",id);
                                        startActivity(intent);
                                    }
                                    Toast.makeText(BookingSelectSpace.this, "this is a space", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            });
        }
    }
}
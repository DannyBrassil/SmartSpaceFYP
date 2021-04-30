package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CarparkLayoutMenu extends AppCompatActivity {
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
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ArrayList<Layout> layouts = new ArrayList<>();

    Button nextButton, previousButton;
    TextView currentFloorNumber, nextFloorNumber, previousFloorNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_layout_menu);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        id= mUser.getUid();



        nextButton=findViewById(R.id.button8);
        previousButton=findViewById(R.id.button6);
        currentFloorNumber=findViewById(R.id.textView109);
        nextFloorNumber=findViewById(R.id.textView123);
        previousFloorNumber=findViewById(R.id.textView124);

        fireDB = FirebaseDatabase.getInstance().getReference("carparks");

        getGridDetails(currentFloor);
        fireDB.child(id).child("Layout").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for( DataSnapshot postSnapshot : snapshot.getChildren()){
                    Layout l = postSnapshot.getValue(Layout.class);
                    layouts.add(l);
                }

                Log.i("layouts",""+layouts.size());
                if(currentFloor-1<layouts.size()){
                    int nextFloor = currentFloor+1;
                    nextFloorNumber.setText("next");
                    nextFloorNumber.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    private void getGridDetails(final int currentFloor) {
        fireDB.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                Carpark c = userSnapshot.getValue(Carpark.class);
                if (c.getId().equalsIgnoreCase(id)) {
                    floorsInCarpark = c.getFloors();

                    fireDB.child(id).child("Layout").addValueEventListener(new ValueEventListener() {
                        ArrayList<Layout> layouts = new ArrayList<>();

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.i("Count " ,""+snapshot.getChildrenCount());

                            Layout l = snapshot.child(String.valueOf(currentFloor-1)).getValue(Layout.class);

                            row = l.getRows();
                            col = l.getColumns();
                            height = l.getHeight();
                            width = l.getWidth();
                            tiles = l.getTiles();

                            createGrid(col, row, height, width, tiles, currentFloor);

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


    private void createGrid(int colCount, int rowCount, int height, int width, final ArrayList<tile> tiles, int currentFloor) {
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
        }
    }
}
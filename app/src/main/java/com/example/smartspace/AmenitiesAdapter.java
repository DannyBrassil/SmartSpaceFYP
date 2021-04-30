package com.example.smartspace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.MyViewHolder> {
    private ArrayList<String> mylistvalues;
    Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String id;
    public static ArrayList<String> items = new ArrayList<>();



    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;   //textview of row layout
        public TextView icon;
        public CheckBox selected;



        public MyViewHolder(View itemView){
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.amenities_name);
            icon=(TextView) itemView.findViewById(R.id.amenities_picture);
            selected = (CheckBox)itemView.findViewById(R.id.checkBox);
        }
    }

    //constructor of MyAdapterclass-Provide the dataset to the Adapter
    // myDataset is passed when called to create an adapter object
    public AmenitiesAdapter(ArrayList<String> myDataset, Context context, String id) {
        mylistvalues= myDataset;
        this.context=context;
        this.id=id;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public AmenitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view –create a row –inflate the layout for the row
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.row_layout_amenities, parent, false);
        MyViewHolder viewHolder= new MyViewHolder(itemView);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final AmenitiesAdapter.MyViewHolder holder, final int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element

        final String name = mylistvalues.get(position).toString();  // name variable of amenity



        holder.name.setText(name);

        if(name.equalsIgnoreCase("security")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_security));
        }
        else if(name.equalsIgnoreCase("multi-storey")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_multistorey));
        }
        else if(name.equalsIgnoreCase("covid compliant")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_covid));
        }
        else if(name.equalsIgnoreCase("disabled access")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_wheelchair));
        }
        else if(name.equalsIgnoreCase("Customer Support")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_support));
        }
        else if(name.equalsIgnoreCase("Discounted rates")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_discount));
        }
        else if(name.equalsIgnoreCase("loyalty scheme")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_loyalty));
        }
        else if(name.equalsIgnoreCase("shuttle bus")){
            holder.icon.setBackground(context.getResources().getDrawable(R.drawable.amenity_bus));
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        final DatabaseReference fireDB;
        fireDB= FirebaseDatabase.getInstance().getReference("carparks").child(id);

        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedGreen));

                if(holder.selected.isChecked()){
                        items.add(holder.name.getText().toString());
                }
                else{
                    for(int i=0;i<items.size();i++){
                        if (items.get(i).equalsIgnoreCase(holder.name.getText().toString())){
                            items.remove(i);
                        }
                    }

                }
/*
                fireDB.child("Amenities").push().setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

*/


            }
        });
/*
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.cancel.setVisibility(View.INVISIBLE);

                fireDB.child("Amenities").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        });

 */
    }


   /* private void openDialog() {
        BookingDialog dialog = new BookingDialog();
        dialog.show(getSupportFragmentManager(), "booking dialog");
    }*/

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }

    public void addItemtoend(String nameOfAmenity){
        mylistvalues.add(nameOfAmenity);
        notifyItemInserted(mylistvalues.size());
    }
}

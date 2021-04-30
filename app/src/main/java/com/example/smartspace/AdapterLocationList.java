package com.example.smartspace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterLocationList extends RecyclerView.Adapter<AdapterLocationList.MyViewHolder> {
    private ArrayList<Carpark> mylistvalues;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;   //textview of row layout
        public TextView spacesAvailable;
        public TextView distance;
        public TextView price;
        public TextView bookButton;


        public MyViewHolder(View itemView){
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.textViewCarParkName);
            bookButton = (TextView) itemView.findViewById(R.id.BookNowButtonRow);
            spacesAvailable=(TextView) itemView.findViewById(R.id.textViewCarParkLocation);
            distance=(TextView) itemView.findViewById(R.id.textViewDistanceAway);
            price=(TextView) itemView.findViewById(R.id.carParkRowPrice);


        }
    }

    //constructor of MyAdapterclass-Provide the dataset to the Adapter
    // myDataset is passed when called to create an adapter object
    public AdapterLocationList(ArrayList<Carpark> myDataset, Context context) {
        mylistvalues= myDataset;
        this.context=context;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public AdapterLocationList.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view –create a row –inflate the layout for the row
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.row_layout, parent, false);
        MyViewHolder viewHolder= new MyViewHolder(itemView);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull AdapterLocationList.MyViewHolder holder, final int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element
        final String name = mylistvalues.get(position).getName();  // name variable of carpark
        final int spaceAvailable = mylistvalues.get(position).getSpaceAvailable();
        final double dist = mylistvalues.get(position).distance(mylistvalues.get(position).getLatitude(), mylistvalues.get(position).getLongitude(), MapsActivity.latLng.latitude,MapsActivity.latLng.longitude);
        Log.w("Carpark distance", String.valueOf(dist));
        holder.name.setText(name);
        holder.spacesAvailable.setText(""+spaceAvailable+" spaces available");
        holder.distance.setText(""+(int)dist+" Km");

        ArrayList<CarParkPrice> prices = mylistvalues.get(position).getPrices();

        double minPrice = prices.get(0).getPrice();
        for(int i = 0; i<prices.size();i++){
            if(prices.get(i).getPrice()<minPrice){
                minPrice = prices.get(i).getPrice();
            }
        }
        holder.price.setText("€ "+minPrice);

        holder.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  Intent intent= new Intent(view.getContext(), MakeBooking.class);
               // intent.putExtra("name",mylistvalues.get(position).getName() );
              //  intent.putExtra("spacesAvailable", mylistvalues.get(position).getSpaceAvailable() );
              //  intent.putExtra("distance", distance );
               // view.getContext().startActivity(intent);
                //openDialog();

              //  BookingDialog dialog = new BookingDialog(mylistvalues.get(position).getName(), mylistvalues.get(position).getSpaceAvailable(), dist);

            //    if(mylistvalues.get(position).isSmartspace()==true){


Log.i("Button", "Clicked");
                    Intent i =new Intent(context, ClickedCarpark.class);
                    Bundle b = new Bundle();
                    b.putString("id", mylistvalues.get(position).getId());
                    b.putDouble("distance", dist);
                    i.putExtras(b);
                    context.startActivity(i);
              //  }

                //dialog.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "booking dialog");

            }
        });
    }

   /* private void openDialog() {
        BookingDialog dialog = new BookingDialog();
        dialog.show(getSupportFragmentManager(), "booking dialog");
    }*/

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }

    public void addItemtoend(Carpark n){
        mylistvalues.add(n);
        notifyItemInserted(mylistvalues.size());
    }

    public void clear() {
        int size = mylistvalues.size();
        mylistvalues.clear();
        notifyItemRangeRemoved(0, size);
    }
}

package com.example.smartspace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class AddAmmenitiesDialog extends AppCompatDialogFragment {

    private String id;
    ArrayList<String> ammenities;

    public AddAmmenitiesDialog(String uid) {
        this.id=uid;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.activity_add_ammenities_dialog, null);


        recyclerView(view);

        builder.setView(view).setNegativeButton("Cancel         ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .setPositiveButton("Confirm Amenities", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                 ammenities = AmenitiesAdapter.items;
                       Log.i("selected:",ammenities.toString());

                       scroll.SelectedAmmenity=ammenities;

                        for (String element : ammenities) {
                            scroll.ammenityBox.append(element+"\n");
                        }


                    }
                });




        return builder.create();
    }


    private void recyclerView(View view) {
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.amenities_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // prepare the dataset which sources the recycler view
        //

        final ArrayList<String> myDataset = new ArrayList<String>();
        // specify an adapter
        final AmenitiesAdapter mAdapter = new AmenitiesAdapter(myDataset, view.getContext(), id);
        //        //Add the divider line
        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        //        // set adapter for the recycler view
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addItemtoend("security");
        mAdapter.addItemtoend("multi-storey");
        mAdapter.addItemtoend("covid compliant");
        mAdapter.addItemtoend("disabled access");
        mAdapter.addItemtoend("Customer Support");
        mAdapter.addItemtoend("Discounted rates");
        mAdapter.addItemtoend("loyalty scheme");
        mAdapter.addItemtoend("shuttle bus");

    }

}
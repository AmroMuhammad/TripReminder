package com.iti41g1.tripreminder.controller.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.Adapters.TripRecyclerAdapter;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.controller.activity.LoginActivity;
import com.iti41g1.tripreminder.database.Trip;
import com.iti41g1.tripreminder.controller.activity.AddTripActivity;
import com.iti41g1.tripreminder.database.TripDatabase;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment {
    ImageView emptyListImg;
    RecyclerView tripRecyclerView;
    FloatingActionButton floatingBtnAdd;

    private TripRecyclerAdapter tripRecyclerAdapter;
    private List tripsList=new ArrayList<Trip>(); ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsListPrepare();
        new Thread(new Runnable() {
            @Override
            public void run() {
      //           HomeActivity.database.tripDAO().insert(trip);
//                tripsList = HomeActivity.database.tripDAO().getAll();
                Log.i(Constants.LOG_TAG,"heerereeee");
            }
        }).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tripRecyclerView=view.findViewById(R.id.trip_recycleView);
   //     Trip trip = new Trip("123","maa","gize","pyramids",30.0594,31.2195,"2/10/2020","10:25",R.id.trip_img,"upcoming");

        TripRecyclerAdapter tripRecyclerAdapter =new TripRecyclerAdapter(getContext(),tripsList);
        tripRecyclerView.setAdapter(tripRecyclerAdapter);
        Log.i(Constants.LOG_TAG,"asdasdas");
        emptyListImg=view.findViewById(R.id.emptyList_img);
        floatingBtnAdd = view.findViewById(R.id.add_flout_btn);
        initializeAddTrip();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(tripsList.isEmpty())
        {
            emptyListImg.setVisibility(View.VISIBLE);
            emptyListImg.setImageResource(R.drawable.preview);

        }
        else
        {
            emptyListImg.setVisibility(View.GONE);
        }
    }
    private void tripsListPrepare() {

    }

    public void initializeAddTrip(){
        floatingBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddTripActivity.class));
            }
        });
    }
}
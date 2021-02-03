package com.iti41g1.tripreminder.controller.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.Adapters.TripUpcomingRecyclerAdapter;
import com.iti41g1.tripreminder.controller.activity.AddTripActivity;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment {
    ImageView emptyListImg;
    RecyclerView tripRecyclerView;
    FloatingActionButton floatingBtnAdd;

    private TripUpcomingRecyclerAdapter tripUpcomingRecyclerAdapter;
    private List tripsList=new ArrayList<Trip>(); ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsListPrepare();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tripRecyclerView=view.findViewById(R.id.trip_recycleView);
        //Trip trip = new Trip("123","maa","gize","pyramids",30.0594,31.2195,"2/10/2020","10:25",R.id.trip_img,"upcoming");

        tripUpcomingRecyclerAdapter =new TripUpcomingRecyclerAdapter(getContext(),tripsList);
        tripRecyclerView.setAdapter(tripUpcomingRecyclerAdapter);
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
       // tripsListPrepare();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
    private void tripsListPrepare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                Trip trip = new Trip(HomeActivity.fireBaseUseerId,"maa","gize","pyramids",30.0594,31.2195,"2/10/2020","10:25",R.id.trip_img,"upcoming");
                Trip trip2 = new Trip(HomeActivity.fireBaseUseerId,"asd","assuit","aswan",30.0594,31.2195,"2/10/2020","10:25",R.id.trip_img,"upcoming");
                Trip trip3 = new Trip(HomeActivity.fireBaseUseerId,"fgh","cairo","assuit",30.0594,31.2195,"2/10/2020","10:25",R.id.trip_img,"upcoming");

                HomeActivity.database.tripDAO().insert(trip);
                HomeActivity.database.tripDAO().insert(trip2);
                HomeActivity.database.tripDAO().insert(trip3);
*/
                tripsList = HomeActivity.database.tripDAO().selectUpcomingTrip(HomeActivity.fireBaseUseerId,"upcoming");
                Log.i(Constants.LOG_TAG,"heerereeee");
            }
        }).start();
    }

    public void initializeAddTrip(){
        floatingBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTripActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("KEY",1);
                bundle.putInt("ID",-1);
                intent.putExtras(bundle);
               // startActivity(intent);
                //startActivity(new Intent(getContext(), AddTripActivity.class));
            }
        });
    }
}
package com.iti41g1.tripreminder.controller.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.Adapters.TripRecyclerAdapter;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    RecyclerView tripRecyclerView;
    private TripRecyclerAdapter tripRecyclerAdapter;
    ImageView emptyListImg;
    private List tripsList =new ArrayList<Trip>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tripRecyclerView=view.findViewById(R.id.trip_recycleView);
        TripRecyclerAdapter tripRecyclerAdapter =new TripRecyclerAdapter(getContext(),tripsList);
        tripRecyclerView.setAdapter(tripRecyclerAdapter);
        emptyListImg=view.findViewById(R.id.emptyList_img);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
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
}
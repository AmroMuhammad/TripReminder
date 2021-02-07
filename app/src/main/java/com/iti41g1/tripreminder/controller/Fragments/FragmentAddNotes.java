package com.iti41g1.tripreminder.controller.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.iti41g1.tripreminder.Adapters.AdapterViewNote;
import com.iti41g1.tripreminder.R;

import java.util.ArrayList;
import java.util.List;

import com.iti41g1.tripreminder.Adapters.AdapterAddNote;
import com.iti41g1.tripreminder.Models.NoteModel;
import com.iti41g1.tripreminder.controller.activity.AddTripActivity;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

public class FragmentAddNotes extends Fragment {
    RecyclerView recyclerView;
    AdapterAddNote adapter;
    AdapterViewNote adapterView;
    LinearLayoutManager linearLayoutManager;
    Button   btnAddNote;
    Button   btnSaveNotes;
    ArrayList<String> notes;
    String date;
    String time;
    String date2;
    String time2;
    Bundle result;
    Trip selectedTrip;
    ArrayList<String> selectedNotes;
    public static final String TAG="Notes";

    public FragmentAddNotes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("datakey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                if(bundle!=null){
                    date = bundle.getString("date");
                 time=bundle.getString("time");
                 date2 = bundle.getString("date2");
                 time2=bundle.getString("time2");
                Log.i(TAG, "onFragmentResult:  AddNotes"+date+".."+time+".."+date2+".."+time2);
                // Do something with the result
            }}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_notes, container, false);
        btnAddNote=view.findViewById(R.id.btn_addNote);
        btnSaveNotes=view.findViewById(R.id.btn_saveNotes);
        recyclerView=view.findViewById(R.id.recyclerView);
        result = new Bundle();
        linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Trip trip=new Trip();
        if(AddTripActivity.key==3) {
            btnSaveNotes.setText("Edit");
            new FragmentAddNotes.LoadRoomData().execute();
            Log.i(TAG, "onCreateView: thread");
        }else {
            selectedNotes=new ArrayList<>();
            selectedNotes.add("");
            adapter=new AdapterAddNote(selectedNotes,getContext());
            recyclerView.setAdapter(adapter);

        }
        Log.i(TAG, "onCreateView: ");
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick:add button "+selectedNotes.toString());
                if(selectedNotes.size()<=10){
                    selectedNotes.add("");
                Log.i(TAG, selectedNotes.toString());
                adapter.notifyDataSetChanged();
            }else{
                    Toast.makeText(getContext(),"you can only add 10 notes",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnSaveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AddTripActivity.key==1){
                 result = new Bundle();
                if(selectedNotes.isEmpty()) {
                    for (int i = 0; i < selectedNotes.size(); i++) {
                        Log.i(TAG, "onClick:Savebutton " + selectedNotes.get(i));
                    }
                   result.putStringArrayList("bundleKey",selectedNotes);
                }
                }else if(AddTripActivity.key==3){
                    Log.i(TAG, "run: "+selectedNotes);
                   new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: "+selectedNotes);
                            trip.setNotes(selectedNotes);
                            HomeActivity.database.tripDAO().EditNotes(AddTripActivity.ID,selectedNotes.toString());
                            getActivity().finish(); //added by amr
                            Log.i(TAG, "run: "+selectedNotes);
                        }
                    }).start();
                }
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack ("name", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        //edit in all methods
        if(AddTripActivity.key==1){
        if(date!="")
            result.putString("date",date);
        if(time!="")
            result.putString("time",time);
        if(date2!="")
            result.putString("date2",date2);
        if(time2!="")
            result.putString("time2",time2);
        getParentFragmentManager().setFragmentResult("requestKey", result);
        Log.i(TAG, "onStop: "+result);
        }
    }
    private class LoadRoomData extends AsyncTask<Void, Void, Trip> {

        @Override
        protected Trip doInBackground(Void... voids) {
            return HomeActivity.database.tripDAO().selectById(AddTripActivity.ID);
        }
        @Override
        protected void onPostExecute(Trip trip) {
            super.onPostExecute(trip);
            selectedTrip = trip;
            if (selectedTrip.getNotes()!=null) {
                selectedNotes = selectedTrip.getNotes();
                adapter = new AdapterAddNote(selectedTrip.getNotes(), getContext());
                recyclerView.setAdapter(adapter);
                Log.i(TAG, "onPostExecute: " + selectedTrip.getNotes());
            }else {
                selectedNotes=new ArrayList<>();
                selectedNotes.add("");
                adapter=new AdapterAddNote(selectedNotes,getContext());
                recyclerView.setAdapter(adapter);
            }
        }
    }

}
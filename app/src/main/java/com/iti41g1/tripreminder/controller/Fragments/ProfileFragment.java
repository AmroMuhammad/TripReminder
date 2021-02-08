package com.iti41g1.tripreminder.controller.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iti41g1.tripreminder.Models.AlarmReceiver;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.controller.activity.LoginActivity;
import com.iti41g1.tripreminder.database.Trip;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class ProfileFragment extends Fragment {
    ImageView imgLogout;
    ImageView imgSync;
    TextView txtLogout;
    TextView txtSync;
public static final String TAG="profile";
    public static DatabaseReference databaseRef =FirebaseDatabase.getInstance().getReference();
     List<Trip>trips;
    List<Trip>tripsl;
    public ProfileFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgLogout = view.findViewById(R.id.imgLogout);
        imgSync = view.findViewById(R.id.imgSync);
        txtLogout = view.findViewById(R.id.txtLogout);
        txtSync = view.findViewById(R.id.txtSync);
        initializeLogOut();
        new readData().execute();
        imgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //  writeOnFireBase(trips);
             readOnFireBase();
              //  insertTripsINRoom(result);

            }
        });
    }
    public void initializeLogOut(){
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new UnregisterData().execute();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new UnregisterData().execute();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
            }
        });
    }
    public void initializeSync(){

    }
    private class UnregisterData extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return HomeActivity.database.tripDAO().selectUpcomingTrip(HomeActivity.fireBaseUseerId, "upcoming");
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            for(int i=0;i<trips.size();i++){
                if(trips.get(i).getTripStatus().equals("upcoming")){
                    unregisterAlarm(trips.get(i));
                }
            }
        }
    }
    public void unregisterAlarm(Trip trip) {
        Intent notifyIntent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (getContext(),trip.getId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(notifyPendingIntent);
        }
    }
    public void writeOnFireBase(List<Trip>trips){
        if(isOnline()) {
            Trip trip;
            databaseRef.child("TripReminder").child("userID").child(trips.get(0).getUserID()).child("trips").removeValue();

            for (int i = 0; i < trips.size(); i++) {
                trip = new Trip(trips.get(i).getUserID(), trips.get(i).getTripName(), trips.get(i).getStartPoint(),trips.get(i).getId(),trips.get(i).getNotes(),
                        trips.get(i).getEndPoint(), trips.get(i).getEndPointLat(), trips.get(i).getEndPointLong(),
                        trips.get(i).getDate(), trips.get(i).getTime(), trips.get(i).getTripImg(), trips.get(i).getTripStatus(), trips.get(i).getCalendar());
              //  trip.getId();
              //  trip.getNotes();
                Log.i(TAG, "writeOnFireBase: " + trip.getTripName() + trip.getId() + trip.getStartPoint()+trip.getNotes());
                databaseRef.child("TripReminder").child("userID").child(HomeActivity.fireBaseUseerId).child("trips").push().setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Success Store Data in FireBase", Toast.LENGTH_SHORT).show();
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failure Store Data in FireBase", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }else{
            Toast.makeText(getContext(), "No Internet ", Toast.LENGTH_SHORT).show();
        }
       Log.i(TAG, "writeOnFireBase: ");
    }
    public   void readOnFireBase(){
        tripsl=new ArrayList<>();
        databaseRef.child("TripReminder").child("userID").child(HomeActivity.fireBaseUseerId).child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                Trip[] tripList=new Trip[(int) dataSnapshot.getChildrenCount()];
                int i=0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                 Trip trip= postSnapshot.getValue(Trip.class);
                 tripList[i]=trip;
                 tripsl.add(trip);
                 i++;
                }
                    Log.i(TAG, "onDataChange: "+tripList.length);
                }
               insertTripsINRoom(tripsl);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }

        return false;
    }
    public void insertTripsINRoom(List<Trip> trips) {
        Log.i(TAG, "insertTripsINRoom: "+trips.size());
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = new Trip(trips.get(i).getUserID(), trips.get(i).getTripName(), trips.get(i).getStartPoint(),
                    trips.get(i).getEndPoint(), trips.get(i).getEndPointLat(), trips.get(i).getEndPointLong(),
                    trips.get(i).getDate(), trips.get(i).getTime(), trips.get(i).getTripImg(), trips.get(i).getTripStatus(), trips.get(i).getCalendar());
            FragmentAddTrip.insertRoom(trip);
            Log.i(TAG, "insertTripsINRoom: "+trip.getTripName());

        }
    }
    private class readData extends AsyncTask<Void, Void, List<Trip>> {
        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return  HomeActivity.database.tripDAO().selectAll(HomeActivity.fireBaseUseerId);

        }
        @Override
        protected void onPostExecute(List<Trip> tripsl) {
            super.onPostExecute(trips);
            trips=tripsl;
        }
    }

}
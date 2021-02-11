package com.iti41g1.tripreminder.controller.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iti41g1.tripreminder.Models.AlarmReceiver;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.database.Trip;
import com.iti41g1.tripreminder.database.TripDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static String userUID;
    private FirebaseAuth firebaseAuth;
    private List<AuthUI.IdpConfig> providers; //to get sign in  with email and google
    private FirebaseUser user;
    List<Trip> tripsl;
    public static final String TAG = "Login";
    private TripDatabase database;
    private   DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().setTitle("Tripinder");
        setContentView(R.layout.activity_login);
        Log.i(Constants.LOG_TAG, "onCreate");
        database = Room.databaseBuilder(this, TripDatabase.class, "tripDB").build();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        initializeSignInProcess();
    }

    //initialize FireBaseUI to take care of Sign in and sign up
    private void initializeSignInProcess() {
        Log.i(Constants.LOG_TAG, "initializeProcess");
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        firebaseAuth = FirebaseAuth.getInstance();
        Log.i(Constants.LOG_TAG, "checkUser");
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.i(Constants.LOG_TAG, "signed in + " + user.getEmail());  //already signed in
            Log.i(Constants.LOG_TAG, "signed in + " + user.getUid());  //already signed in
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else {
            Log.i(Constants.LOG_TAG, "sign in process");
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                    setIsSmartLockEnabled(false).setLogo(R.drawable.logo)
                    .setTheme(R.style.AuthenticationTheme)
                    .setAvailableProviders(providers).build(), Constants.AUTH_REQUEST_CODE);
        }
    }

    //result of FirebaseUI auth process
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(Constants.LOG_TAG, "onActivityResult");
        if (requestCode == Constants.AUTH_REQUEST_CODE) {
            Log.i(Constants.LOG_TAG, "Auth request code is correct");
            if (resultCode == RESULT_OK) {
                Log.i(Constants.LOG_TAG, "signed in successfully");
                //check room isEmpty
                new check().execute();
                readOnFireBase();

            } else {
                Log.i(Constants.LOG_TAG, "not signed in successfully");
                finish();
            }
        }
    }

    private void readOnFireBase() {

        tripsl=new ArrayList<>();
        databaseRef.child("TripReminder").child("userID").child(FirebaseAuth.getInstance().getUid()).child("trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "onDataChange: ");
                    //     Trip[] tripList = new Trip[(int) dataSnapshot.getChildrenCount()];
                    int i = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Trip trip = postSnapshot.getValue(Trip.class);
                        //           tripList[i] = trip;
                        tripsl.add(trip);
                        i++;
                    }
                    //         Log.i(TAG, "onDataChange: " + tripList.length);
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

    public void insertTripsINRoom(List<Trip> trips) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "insertTripsINRoom: " + trips.size());
                for (int i = 0; i < trips.size(); i++) {
                    if(Calendar.getInstance().getTimeInMillis() > trips.get(i).getCalendar() && trips.get(i).getTripStatus().equals(Constants.UPCOMING_TRIP_STATUS)){
                        trips.get(i).setTripStatus(Constants.MISSED_TRIP_STATUS);
                    }
                    Trip trip = new Trip(trips.get(i).getUserID(), trips.get(i).getTripName(), trips.get(i).getStartPoint(),
                            trips.get(i).getStartPointLat(), trips.get(i).getStartPointLong(), trips.get(i).getEndPoint(),
                            trips.get(i).getEndPointLat(), trips.get(i).getEndPointLong(), trips.get(i).getDate(),
                            trips.get(i).getTime(), trips.get(i).getTripImg(), trips.get(i).getTripStatus(),
                            trips.get(i).getCalendar(), trips.get(i).getNotes());


                    database.tripDAO().insert(trip);
                    if(trips.get(i).getTripStatus().equals(Constants.UPCOMING_TRIP_STATUS)){
                        initAlarm(trips.get(i));
                    }
                    Log.i(TAG, "insertTripsINRoom: " + trip.getTripName());
                }
            }
        }).start();
    }
    private class check extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.tripDAO().clear();
            //    readOnFireBase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    public void initAlarm(Trip trip) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        notifyIntent.putExtra(Constants.TRIP_NAME, trip.getTripName());
        notifyIntent.putExtra(Constants.TRIP_ID, trip.getId());
        notifyIntent.putExtra(Constants.TRIP_USER_ID, trip.getUserID());
        notifyIntent.putExtra(Constants.TRIP_LATITUDE, trip.getEndPointLat());
        notifyIntent.putExtra(Constants.TRIP_LONGITUDE, trip.getEndPointLong());

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, trip.getId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trip.getCalendar(), notifyPendingIntent);
    }
}
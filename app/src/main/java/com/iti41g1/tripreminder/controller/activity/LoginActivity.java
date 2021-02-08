package com.iti41g1.tripreminder.controller.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.Fragments.FragmentAddTrip;
import com.iti41g1.tripreminder.controller.Fragments.ProfileFragment;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class LoginActivity extends AppCompatActivity {
    public static String userUID;
    private FirebaseAuth firebaseAuth;
    private List<AuthUI.IdpConfig> providers; //to get sign in  with email and google
    private FirebaseUser user;
    List<Trip>tripsl;
    public static final String TAG="Login";
    public  static DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(Constants.LOG_TAG, "onCreate");
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
                    setIsSmartLockEnabled(false)
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

            } else {
                Log.i(Constants.LOG_TAG, "not signed in successfully");
                finish();
            }
        }
    }
    public  void readOnFireBase(){
        tripsl=new ArrayList<>();
        databaseRef.child("TripReminder").child("userID").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("trips").addValueEventListener(new ValueEventListener() {
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
    public void insertTripsINRoom(List<Trip> trips) {
        Log.i(TAG, "insertTripsINRoom: "+trips.size());
        for (int i = 0; i < trips.size(); i++) {
           Trip trip = new Trip(trips.get(i).getUserID(),trips.get(i).getTripName(),trips.get(i).getStartPoint(),
                    trips.get(i).getStartPointLat(),trips.get(i).getStartPointLong(),trips.get(i).getEndPoint(),
                    trips.get(i).getEndPointLat(),trips.get(i).getEndPointLong(),trips.get(i).getDate(),
                    trips.get(i).getTime(),trips.get(i).getTripImg(),trips.get(i).getTripStatus(),
                    trips.get(i).getCalendar(), trips.get(i).getNotes());

              FragmentAddTrip.insertRoom(trip);
            Log.i(TAG, "insertTripsINRoom: "+trip.getTripName());

        }
    }
    private class check extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HomeActivity.database.tripDAO().clear();
            readOnFireBase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }
}
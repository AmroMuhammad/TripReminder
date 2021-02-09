package com.iti41g1.tripreminder.controller.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HistoryMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = "so47492459";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new getFinishedTrips().execute();
    }

    public void drawTrips(double startPointLatitude, double startPointLongitude, double endPointLatitude, double endPointLongitude, String startPoint, String endPoint) {
        String originPoint = startPointLatitude + "," + startPointLongitude;
        String destination = endPointLatitude + "," + endPointLongitude;

        LatLng startPointName = new LatLng(startPointLatitude, startPointLongitude);
        mMap.addMarker(new MarkerOptions().position(startPointName).title(startPoint));

        LatLng endPointName = new LatLng(endPointLatitude, endPointLongitude);
        mMap.addMarker(new MarkerOptions().position(endPointName).title(endPoint));

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //center
        LatLng center = new LatLng(30.0434, 31.2468);

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(Constants.API_KEY)
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, originPoint, destination);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.i(Constants.LOG_TAG, "Error occurred");
        }

        //for randoom color
        Random rnd = new Random();
        int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(randomColor).width(7);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 6));
    }

    class getFinishedTrips extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return HomeActivity.database.tripDAO().selectUpcomingTrip(HomeActivity.fireBaseUseerId, Constants.FINISHED_TRIP_STATUS);
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            if (!trips.isEmpty()) {
                for (int i = 0; i < trips.size(); i++) {
                    drawTrips(trips.get(i).getStartPointLat(),trips.get(i).getStartPointLong(),
                            trips.get(i).getEndPointLat(),trips.get(i).getEndPointLong(),
                            trips.get(i).getStartPoint(),trips.get(i).getEndPoint());
                }
            }
        }
    }
}

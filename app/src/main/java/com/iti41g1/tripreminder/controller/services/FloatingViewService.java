package com.iti41g1.tripreminder.controller.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import androidx.annotation.Nullable;

import com.iti41g1.tripreminder.Adapters.TripUpcomingRecyclerAdapter;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloatingViewService extends Service {
    WindowManager.LayoutParams params;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private List<CheckBox> checkBoxes;
    private List<String> notes;
    private int tripId;

    public FloatingViewService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tripId = intent.getExtras().getInt(Constants.TRIP_ID);
        new LoadNotes().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        initializeCheckBoxes();
        notes = new ArrayList();

        //to run floating widget with different permissions and make pictures for floating widget not v24 for android Marshmallow or lower
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }

                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    //Detect if the floating view is collapsed or expanded
    //return true if the floating view is collapsed.
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.layoutCollapsed).getVisibility() == View.VISIBLE;
    }

    private void initializeCheckBoxes(){
        checkBoxes = new ArrayList();
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox1));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox2));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox3));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox4));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox5));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox6));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox7));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox8));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox9));
        checkBoxes.add(mFloatingView.findViewById(R.id.checkBox10));
    }

    private void attachNotesToCheckBoxes(){
        for(int i=0;i<notes.size();i++){
            checkBoxes.get(i).setText(notes.get(i).toString());
            checkBoxes.get(i).setVisibility(View.VISIBLE);
        }
    }

    private class LoadNotes extends AsyncTask<Void, Void,Trip> {

        @Override
        protected Trip doInBackground(Void... voids) {

            return HomeActivity.database.tripDAO().selectById(tripId);
        }

        @Override
        protected void onPostExecute(Trip trip) {
            super.onPostExecute(trip);
            notes = trip.getNotes();
            attachNotesToCheckBoxes();
        }
    }

}

package com.iti41g1.tripreminder.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.Fragments.HistoryFragment;
import com.iti41g1.tripreminder.controller.Fragments.ProfileFragment;
import com.iti41g1.tripreminder.controller.Fragments.UpcomingFragment;
import com.iti41g1.tripreminder.Adapters.ViewPagerAdaptor;
import com.iti41g1.tripreminder.database.TripDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public  ViewPager viewPager;
    private TabLayout tabLayout;
    private UpcomingFragment upcomingFragment;
    private ProfileFragment profileFragment;
    private HistoryFragment historyFragment;
    private List<Fragment> fragments;
    private List<String> fragmentTitles;
    public ViewPagerAdaptor adaptor;
    public static TripDatabase database;
    public static String fireBaseUseerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fireBaseUseerId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        //initalize DB
        database = Room.databaseBuilder(this, TripDatabase.class, "tripDB").build();
        //inflating views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        //initializing fragments
        upcomingFragment = new UpcomingFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();

        //initializing viewPager
        tabLayout.setupWithViewPager(viewPager);
        fragmentsinit();
        fragmentTitlesinit();
        adaptor = new ViewPagerAdaptor(getSupportFragmentManager(), 0, fragments, fragmentTitles);
        viewPager.setAdapter(adaptor);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
              adaptor.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private void fragmentTitlesinit() {
        fragmentTitles = new ArrayList<>();
        fragmentTitles.add("Upcoming");
        fragmentTitles.add("History");
        fragmentTitles.add("Profile");
    }

    private void fragmentsinit() {
        fragments = new ArrayList<>();
        fragments.add(upcomingFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);
    }
}
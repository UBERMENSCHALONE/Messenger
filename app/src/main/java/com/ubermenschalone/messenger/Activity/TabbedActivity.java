package com.ubermenschalone.messenger.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ubermenschalone.messenger.Adapter.TabbedActivityAdapter;
import com.ubermenschalone.messenger.R;

import java.util.HashMap;

public class TabbedActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        TabbedActivityAdapter sectionsPagerAdapter = new TabbedActivityAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setStatus("online");
    }

    private void setStatus(String status){
        databaseReferenceStatus = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userStatus", status);
        databaseReferenceStatus.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        setStatus("offline");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        setStatus("offline");
//    }
}
package com.gmail.agv_tag_tool_v6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Lock auto rotate screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        //
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.mipmap.ic_launcher);
        // Init variable
//        Anhxa();
        // Menu bar action select
        menubar_activity();
    }

    private void menubar_activity() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set mainscreen selected
        bottomNavigationView.setSelectedItemId(R.id.about);
        //perform Item selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.personal:
                        startActivity(new Intent(getApplicationContext(),PersonalActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.mainscreen:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        return true;
                }
                return false;
            }
        });
    }
}

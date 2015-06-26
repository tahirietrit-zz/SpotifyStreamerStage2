package com.tahirietrit.spotifystreamerstage2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import fragments.TopTracksFragment;


public class MainActivity extends ActionBarActivity {

    public static boolean isTablet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.artistTopTracks)!= null){
            isTablet = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.artistTopTracks, new TopTracksFragment())
                        .commit();
            }

        }else{
            isTablet = false;
        }
    }



}

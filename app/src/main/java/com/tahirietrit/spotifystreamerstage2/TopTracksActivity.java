package com.tahirietrit.spotifystreamerstage2;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import fragments.TopTracksFragment;

/**
 * Created by etrittahiri on 6/21/15.
 */
public class TopTracksActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.top_tracks);
        actionBar.setSubtitle(getIntent().getStringExtra("artistName"));

        TopTracksFragment newFragment = new TopTracksFragment();
        Bundle args = new Bundle();
        args.putString("spotifyId", getIntent().getStringExtra("spotifyId"));
        args.putString("artistName", getIntent().getStringExtra("artistName"));
        newFragment.setArguments(args);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.artistTopTracks, newFragment);
        transaction.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();

            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }
}

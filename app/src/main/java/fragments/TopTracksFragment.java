package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tahirietrit.spotifystreamerstage2.MainActivity;
import com.tahirietrit.spotifystreamerstage2.PlayerActivity;
import com.tahirietrit.spotifystreamerstage2.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import callbacks.TopTracksCallback;
import kaaes.spotify.webapi.android.models.Track;
import listadapters.RecyclerItemClickListener;
import listadapters.TopTracksAdapter;
import models.TopTrack;
import requests.TopTracksTask;

/**
 * Created by tahirietrit on 07/06/15.
 */
public class TopTracksFragment extends Fragment implements TopTracksCallback {

    public TopTracksFragment() {

    }

    @InjectView(R.id.my_recycler_view)
    RecyclerView topTracksRecyclerView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    private TopTracksAdapter topTracksAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private  ArrayList<TopTrack> tracksArrayList = new ArrayList<>();
    String artistId;
    String artistName;
    public static TopTracksFragment lastInstance;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View gv = inflater.inflate(R.layout.top_tracks_fragment, container, false);
        ButterKnife.inject(this, gv);
        lastInstance = this;
        if(!MainActivity.isTablet) {
            artistId = getArguments().getString("spotifyId");
        }

        topTracksRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        topTracksRecyclerView.setLayoutManager(mLayoutManager);
        topTracksRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (MainActivity.isTablet) {
                            showDialog(position);
                        } else {
                            openPlayerActivity(position);
                        }
                    }
                })
        );
        topTracksAdapter = new TopTracksAdapter(getActivity());
        topTracksRecyclerView.setAdapter(topTracksAdapter);


        if (savedInstanceState == null || !savedInstanceState.containsKey("tracksList")) {
            new TopTracksTask(TopTracksFragment.this, progressBar).execute(artistId);
        } else {
            tracksArrayList = savedInstanceState.getParcelableArrayList("tracksList");
            topTracksAdapter.setArtistArrayList(tracksArrayList);
        }

        return gv;
    }

    public void showSelectedArtsistsTracks(String artistId){
        new TopTracksTask(TopTracksFragment.this, progressBar).execute(artistId);
    }
    @Override
    public void onTopTracksResult(List<Track> tracks) {
        tracksArrayList.clear();
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).album.images.size() > 0)
                tracksArrayList.add(new TopTrack(tracks.get(i).artists.get(0).name,tracks.get(i).name, tracks.get(i).album.name, tracks.get(i).album.images.get(0).url, tracks.get(i).preview_url));
        }
        topTracksAdapter.setArtistArrayList(tracksArrayList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tracksList", tracksArrayList);
        super.onSaveInstanceState(outState);
    }
    void showDialog(int position) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("topTracks", tracksArrayList);
        args.putInt("position", position);


        DialogFragment newFragment = PlayerFragment.newInstance();
        newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), null);
    }
    private void openPlayerActivity(int position){
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putParcelableArrayListExtra("topTracks", tracksArrayList);
        intent.putExtra("position", position);
        startActivity(intent);

    }

}

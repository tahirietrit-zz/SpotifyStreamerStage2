package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.tahirietrit.spotifystreamerstage2.MainActivity;
import com.tahirietrit.spotifystreamerstage2.R;
import com.tahirietrit.spotifystreamerstage2.TopTracksActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import callbacks.SearchResponseCallback;
import kaaes.spotify.webapi.android.models.Artist;
import listadapters.RecyclerItemClickListener;
import listadapters.SearchListAdapter;
import requests.SearchTask;


/**
 * Created by tahirietrit on 07/06/15.
 */
public class SearchFragment extends Fragment implements SearchResponseCallback {
    public SearchFragment() {
    }

    @InjectView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.artistNameSearchView)
    SearchView artistSearchView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;


    private SearchListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Artist> artistArrayList = new ArrayList<>();
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View gv = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, gv);

        artistSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0)
                    new SearchTask(SearchFragment.this, progressBar).execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0)
                    new SearchTask(SearchFragment.this, progressBar).execute(newText);
                return false;
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (MainActivity.isTablet) {
                            TopTracksFragment.lastInstance.showSelectedArtsistsTracks(artistArrayList.get(position).id);
                        } else {
                            openTopTracksActivity(position);
                        }

                    }
                })
        );
        mAdapter = new SearchListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return gv;
    }

    @Override
    public void onSearchResult(List<Artist> artistArrayList) {
        this.artistArrayList.clear();
        this.artistArrayList.addAll(artistArrayList);
        mAdapter.setArtistArrayList(this.artistArrayList);
        if (artistArrayList.size() == 0) {
            cancelToast();
            showToast("The artist" + artistSearchView.getQuery().toString() + "was not found");
        }
    }

    public void openTopTracksActivity(int position) {

        Intent intent = new Intent(getActivity(), TopTracksActivity.class);
        intent.putExtra("spotifyId", artistArrayList.get(position).id);
        intent.putExtra("artistName", artistArrayList.get(position).name);
        startActivity(intent);
    }

    public void showToast(String text) {
        cancelToast();
        toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}

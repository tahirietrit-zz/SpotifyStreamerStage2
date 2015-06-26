package requests;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import callbacks.SearchResponseCallback;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by tahirietrit on 07/06/15.
 */
public class SearchTask extends AsyncTask<String, Void, ArtistsPager> {

    private SearchResponseCallback callback;
    private ProgressBar progressBar;

    public SearchTask(SearchResponseCallback callback, ProgressBar progressBar) {
        this.callback = callback;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {
        SpotifyApi api = new SpotifyApi();
        try {
            SpotifyService spotify = api.getService();
            return  spotify.searchArtists(params[0]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArtistsPager result) {
        if (result != null) {
            progressBar.setVisibility(View.GONE);
            callback.onSearchResult(result.artists.items);
        }
    }
}
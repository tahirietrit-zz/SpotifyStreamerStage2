package requests;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import callbacks.TopTracksCallback;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by tahirietrit on 07/06/15.
 */
public class TopTracksTask extends AsyncTask<String, Void, Tracks> {
    TopTracksCallback callback;
    ProgressBar progressBar;

    public TopTracksTask(TopTracksCallback callback, ProgressBar progressBar) {
        this.callback = callback;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Tracks doInBackground(String... params) {
        SpotifyApi api = new SpotifyApi();
        try {
            SpotifyService spotify = api.getService();
            return spotify.getArtistTopTrack(params[0]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Tracks result) {
        progressBar.setVisibility(View.GONE);
        if(result != null) {
            progressBar.setVisibility(View.GONE);
            callback.onTopTracksResult(result.tracks);
        }
    }
}
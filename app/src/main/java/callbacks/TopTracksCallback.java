package callbacks;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by tahirietrit on 07/06/15.
 */
public interface TopTracksCallback {
    public void onTopTracksResult(List<Track> tracks);
}

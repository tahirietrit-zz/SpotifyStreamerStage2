package callbacks;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by tahirietrit on 07/06/15.
 */
public interface SearchResponseCallback {
    public void onSearchResult(List<Artist> artistArrayList);
}

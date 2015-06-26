package models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tahirietrit on 13/06/15.
 */
public class TopTrack implements Parcelable {
    public String artistName;
    public String trackName;
    public String albumName;
    public String albumThumbnail;
    public String trackPreview;

    public TopTrack(String artisName,String trackName, String albumName, String albumThumbnail, String trackPreview){
        this.artistName = artisName;
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumThumbnail = albumThumbnail;
        this.trackPreview = trackPreview;
    }

    private TopTrack(Parcel in) {
        artistName = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        albumThumbnail = in.readString();
        trackPreview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(albumThumbnail);
        dest.writeString(trackPreview);
    }
    public static final Creator<TopTrack> CREATOR = new Creator<TopTrack>() {
        public TopTrack createFromParcel(Parcel in) {
            return new TopTrack(in);
        }

        public TopTrack[] newArray(int size) {
            return new TopTrack[size];
        }
    };

}

package listadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tahirietrit.spotifystreamerstage2.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import models.TopTrack;

/**
 * Created by tahirietrit on 07/06/15.
 */
public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.ViewHolder> {
    private ArrayList<TopTrack> tracksArrayList = new ArrayList<>();
    private Context ctx;
    public TopTracksAdapter(Context ctx){
        this.ctx = ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.trackNameItem)
        TextView trackNameTextView;

        @InjectView(R.id.albumNameItem)
        TextView albumNameTextView;

        @InjectView(R.id.trackThumbnailItem)
        ImageView trackThumbnailItem;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    @Override
    public TopTracksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.best_tracks_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.trackNameTextView.setText(tracksArrayList.get(position).trackName);
        holder.albumNameTextView.setText(tracksArrayList.get(position).albumName);
        Picasso.with(ctx).load(tracksArrayList.get(position).albumThumbnail).into(holder.trackThumbnailItem);

    }

    @Override
    public int getItemCount() {
        return tracksArrayList.size();
    }

    public void setArtistArrayList(ArrayList<TopTrack> tracksArrayList) {
        this.tracksArrayList = tracksArrayList;
        notifyDataSetChanged();
    }
}

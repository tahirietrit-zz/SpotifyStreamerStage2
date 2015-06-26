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
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by tahirietrit on 07/06/15.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder>
        implements View.OnClickListener {
    private ArrayList<Artist> artistArrayList = new ArrayList<>();
    private Context ctx;

    public SearchListAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.artistNameItem)
        TextView artistsNameTextView;
        @InjectView(R.id.artistThumbnailItem)
        ImageView artistsThumbImageView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(SearchListAdapter.this);
            ButterKnife.inject(this, v);
        }
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artists_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.artistsNameTextView.setText(artistArrayList.get(position).name);
        if (artistArrayList.get(position).images.size() > 0)
            Picasso.with(ctx).load(artistArrayList.get(position).images.get(0).url).into(holder.artistsThumbImageView);

    }

    @Override
    public int getItemCount() {
        return artistArrayList.size();
    }

    public void setArtistArrayList(ArrayList<Artist> artistArrayList) {
        this.artistArrayList = artistArrayList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
    }
}

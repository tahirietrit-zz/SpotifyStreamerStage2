package fragments;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tahirietrit.spotifystreamerstage2.R;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.TopTrack;

/**
 * Created by etrittahiri on 6/22/15.
 */
public class PlayerFragment extends DialogFragment implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        View.OnTouchListener {
    static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds;
    @InjectView(R.id.artistNamePlayer)
    TextView artistNamePlayer;
    @InjectView(R.id.trackNamePlayer)
    TextView trackNamePlayer;
    @InjectView(R.id.albumNamePlayer)
    TextView albumNamePlayer;
    @InjectView(R.id.albumThumbnailPlayer)
    ImageView albumThumbnailPlayer;
    @InjectView(R.id.trackSeekBar)
    SeekBar trackSeekBar;
    @InjectView(R.id.progresTextView)
    TextView progresTextView;
    @InjectView(R.id.lengthTextView)
    TextView lengthTextView;
    @InjectView(R.id.previousTrack)
    ImageButton previousTrack;
    @InjectView(R.id.playTrack)
    ImageButton playTrack;
    @InjectView(R.id.nextTrack)
    ImageButton nextTrack;

    private ArrayList<TopTrack> tracksArrayList = new ArrayList<>();
    private int position;

    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_player, container, false);
        ButterKnife.inject(this, v);
        position = getArguments().getInt("position");
        tracksArrayList = getArguments().getParcelableArrayList("topTracks");
        Picasso.with(getActivity()).load(tracksArrayList.get(position).albumThumbnail).into(albumThumbnailPlayer);
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        try {
            mediaPlayer.setDataSource(tracksArrayList.get(position).trackPreview);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        playTrack.setImageResource(android.R.drawable.ic_media_pause);
        primarySeekBarProgressUpdater();
        mediaFileLengthInMilliseconds = mediaPlayer.getDuration();


        trackSeekBar.setOnTouchListener(this);


        trackSeekBar.setMax(mediaFileLengthInMilliseconds);
        String seconds = String.valueOf((mediaFileLengthInMilliseconds % 60000) / 1000);

        String minutes = String.valueOf(mediaFileLengthInMilliseconds / 60000);
        lengthTextView.setText("" + minutes + ":" + seconds);
        artistNamePlayer.setText(tracksArrayList.get(position).artistName);
        trackNamePlayer.setText(tracksArrayList.get(position).trackName);
        albumNamePlayer.setText(tracksArrayList.get(position).albumName);

        return v;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        trackSeekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextTrack();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
            mediaPlayer.seekTo(playPositionInMillisecconds);
        }
        return false;
    }

    private void primarySeekBarProgressUpdater() {
        trackSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
       if(mediaPlayer != null) {
           if (mediaPlayer.isPlaying()) {
               Runnable notification = new Runnable() {
                   public void run() {
                       if(mediaPlayer != null) {
                           primarySeekBarProgressUpdater();
                           String seconds = String.valueOf((mediaPlayer.getCurrentPosition() % 60000) / 1000);
                           String minutes = String.valueOf(mediaPlayer.getCurrentPosition() / 60000);
                           if ((mediaPlayer.getCurrentPosition() % 60000) / 1000 < 10) {
                               progresTextView.setText("" + minutes + ":0" + seconds);
                           } else {
                               progresTextView.setText("" + minutes + ":" + seconds);
                           }
                       }
                   }
               };
               handler.postDelayed(notification, 1000);
           }
       }
    }

    @OnClick(R.id.playTrack)
    public void playPauseTrack() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playTrack.setImageResource(android.R.drawable.ic_media_pause);
            primarySeekBarProgressUpdater();
        } else {
            mediaPlayer.pause();
            playTrack.setImageResource(android.R.drawable.ic_media_play);
            primarySeekBarProgressUpdater();
        }
    }

    @OnClick(R.id.nextTrack)
    public void playNextTrack() {
        position++;
        position = (position == tracksArrayList.size()) ? 0 : position;
        changeTrack(position);
        primarySeekBarProgressUpdater();
    }

    @OnClick(R.id.previousTrack)
    public void playPreviewsTrack() {
        position--;
        position = (position < 0) ? tracksArrayList.size() - 1 : position;
        changeTrack(position);
        primarySeekBarProgressUpdater();

    }

    public void changeTrack(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(tracksArrayList.get(position).trackPreview);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Picasso.with(getActivity()).load(tracksArrayList.get(position).albumThumbnail).into(albumThumbnailPlayer);
        artistNamePlayer.setText(tracksArrayList.get(position).artistName);
        trackNamePlayer.setText(tracksArrayList.get(position).trackName);
        albumNamePlayer.setText(tracksArrayList.get(position).albumName);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }catch (Exception e){

        }
    }
}

package com.tahirietrit.spotifystreamerstage2;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import models.TopTrack;

/**
 * Created by etrittahiri on 6/23/15.
 */
public class PlayerActivity extends ActionBarActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        View.OnTouchListener {
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

    ArrayList<TopTrack> tracksArrayList = new ArrayList<>();
    int position;

    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.inject(this);

        position = getIntent().getIntExtra("position",0);
        tracksArrayList = getIntent().getParcelableArrayListExtra("topTracks");
        Picasso.with(getApplicationContext()).load(tracksArrayList.get(position).albumThumbnail).into(albumThumbnailPlayer);

        new InitPlayer().execute();
        trackSeekBar.setOnTouchListener(this);
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
        Picasso.with(getApplicationContext()).load(tracksArrayList.get(position).albumThumbnail).into(albumThumbnailPlayer);
        artistNamePlayer.setText(tracksArrayList.get(position).artistName);
        trackNamePlayer.setText(tracksArrayList.get(position).trackName);
        albumNamePlayer.setText(tracksArrayList.get(position).albumName);
    }
    private void setupPlayer(){

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


    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }catch (Exception e){

        }
    }
    class InitPlayer extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            setupPlayer();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            primarySeekBarProgressUpdater();
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();

            String seconds = String.valueOf((mediaFileLengthInMilliseconds % 60000) / 1000);

            String minutes = String.valueOf(mediaFileLengthInMilliseconds / 60000);
            lengthTextView.setText("" + minutes + ":" + seconds);
            artistNamePlayer.setText(tracksArrayList.get(position).artistName);
            trackNamePlayer.setText(tracksArrayList.get(position).trackName);
            albumNamePlayer.setText(tracksArrayList.get(position).albumName);
            playTrack.setImageResource(android.R.drawable.ic_media_pause);
        }
    }
}


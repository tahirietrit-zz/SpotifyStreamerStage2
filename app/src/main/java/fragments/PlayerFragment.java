package fragments;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

    private Handler handler ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_player, container, false);
        ButterKnife.inject(this, v);

        handler = new Handler();

        position = getArguments().getInt("position");
        tracksArrayList = getArguments().getParcelableArrayList("topTracks");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        updatePlayer(position, false);
        trackSeekBar.setOnTouchListener(this);

        return v;
    }

    public void updatePlayer(final int position, boolean isPlay) {

        if (!isPlay) {


            playTrack.setImageResource(android.R.drawable.ic_media_pause);

            artistNamePlayer.setText(tracksArrayList.get(position).artistName);
            albumNamePlayer.setText(tracksArrayList.get(position).albumName);
            trackNamePlayer.setText(tracksArrayList.get(position).trackName);

            Picasso.with(getActivity()).load(tracksArrayList.get(position).albumThumbnail).resize(400, 400).centerCrop().into(albumThumbnailPlayer);


            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {

                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(tracksArrayList.get(position).trackPreview);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
                        seekBarProgressUpdater();

                    } catch (Exception ex) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    lengthTextView.setText(getStringFormat(mediaFileLengthInMilliseconds));
                    progresTextView.setText("0:00");

                    super.onPostExecute(aVoid);
                }
            }.execute();


        } else {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                playTrack.setImageResource(android.R.drawable.ic_media_play);
                seekBarProgressUpdater();

            } else {

                mediaPlayer.start();
                playTrack.setImageResource(android.R.drawable.ic_media_pause);
                seekBarProgressUpdater();
            }
        }


    }

    private void seekBarProgressUpdater() {

        if (mediaPlayer != null) {
            trackSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));

            if (mediaPlayer.isPlaying()) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            seekBarProgressUpdater();
                            progresTextView.setText(getStringFormat(mediaPlayer.getCurrentPosition()));
                        }
                    }
                }, 100);
            }
        }
    }

    @OnClick(R.id.previousTrack)
    public void setOnClickListenerPrevious() {
        position--;
        position = (position < 0) ? tracksArrayList.size() - 1 : position;
        updatePlayer(position, false);
    }

    @OnClick(R.id.playTrack)
    public void setOnClickListenerPlayPause() {
        updatePlayer(position, true);
    }

    @OnClick(R.id.nextTrack)
    public void setOnClickListenerNext() {
        position++;
        position = (position == tracksArrayList.size()) ? 0 : position;
        updatePlayer(position, false);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        trackSeekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setOnClickListenerNext();
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

    private String getStringFormat(int milisecond) {
        return String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(milisecond) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisecond)),
                TimeUnit.MILLISECONDS.toSeconds(milisecond) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisecond)));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception ex) {

        }
    }
}
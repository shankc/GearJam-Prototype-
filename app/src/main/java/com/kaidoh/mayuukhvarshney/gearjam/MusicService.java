package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 13/03/16.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.util.List;


public class MusicService extends Service {
    private final IBinder musicBind = new MusicBinder();
    private List<Track> songs;
    private int songPosn;
    private boolean PrepStage=false;
    private static final int NOTIFY_ID=1;
    private final Handler handler=new Handler();
    Intent intent;
    private ExoPlayer exoPlayer;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    public boolean pause;

    public void onCreate(){
        super.onCreate();
        intent = new Intent();
        songPosn=0;
        exoPlayer=ExoPlayer.Factory.newInstance(1);

        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (exoPlayer.getPlaybackState() == exoPlayer.STATE_ENDED) {
                    exoPlayer.stop();
                    exoPlayer.seekTo(0);
                    playNext();

                } else {
                    if (exoPlayer.getPlaybackState() == exoPlayer.STATE_READY) {
                        PrepStage = true;
                        send();
                        //sendMessage();
                        Track theTitle = songs.get(getSongIndex());
                        String songTitle = theTitle.getTitle();
                        Intent notIntent = new Intent(MusicService.this, DisplayTrackActivity.class);
                        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        //notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                     PendingIntent pendInt = PendingIntent.getActivity(MusicService.this, PendingIntent.FLAG_CANCEL_CURRENT,
                        notIntent, 0);
                       // PendingIntent pendInt= PendingIntent.getActivity(MusicService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                        //  PendingIntent pendInt = PendingIntent.getActivity(MusicService.this,0,intent,0);

                        Notification.Builder builder = new Notification.Builder(MusicService.this);

                        builder.setContentIntent(pendInt)
                                .setSmallIcon(R.drawable.ic_play_circle_filled_white_24dp)
                                .setTicker(songTitle)
                                .setOngoing(true)
                                .setContentTitle("Playing")
                                .setContentText(songTitle);
                        Notification not = builder.build();
                        startForeground(NOTIFY_ID, not);
                    }


                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                exoPlayer.stop();
                exoPlayer.seekTo(0);
                PrepStage = false;
                playSong();

            }
        });

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Do what you need in onStartCommand when service has been started
        //Bundle extras= intent.getExtras();
        exoPlayer.stop();
        exoPlayer.seekTo(0);

        return START_NOT_STICKY;
    }
    public void initExoPlayer(){
        Track theSong=songs.get(songPosn);
        String url = theSong.getStreamURL()+ "?client_id=" + Config.CLIENT_ID;
        Uri radioUri = Uri.parse(url);
// Settings for exoPlayer
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        DefaultUriDataSource dataSource = new DefaultUriDataSource(this, null, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
// Prepare ExoPlayer
        exoPlayer.prepare(audioRenderer);
    }
    public void setList(List<Track> theSongs){
        this.songs=theSongs;
    }
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        exoPlayer.stop();
        exoPlayer.release();
        return false;
    }
public void playSong(){
    if(exoPlayer!=null && exoPlayer.getPlayWhenReady())
    {
        exoPlayer.stop();
        exoPlayer.seekTo(0);
        PrepStage=false;

    }
    else
    {
        if(PrepStage && pause)

        {   exoPlayer.stop();
            exoPlayer.seekTo(0);
            exoPlayer.setPlayWhenReady(false);
            PrepStage=false;
        }
    }
    initExoPlayer();
    exoPlayer.setPlayWhenReady(true);
}
    public void setSong(int songIndex){
        songPosn=songIndex;

    }
    public long getPosn(){
        return exoPlayer.getCurrentPosition();
    }
    public void StopPlayer(){
        exoPlayer.stop();
        exoPlayer.seekTo(0);
    }
    public boolean PrepState(){
        return this.PrepStage;
    }
    public long getDur(){
        return exoPlayer.getDuration();
    }

    public boolean isPng(){
        return exoPlayer.getPlayWhenReady();
    }
    public void go(){
        exoPlayer.setPlayWhenReady(true);
    }
    public void pausePlayer(){
        exoPlayer.setPlayWhenReady(false);
    }
    public void PauseState(boolean Pause_state){
        this.pause=Pause_state;
    }
    public int getSongIndex(){
        return songPosn;
    }
    public void ResetPlayer(){ exoPlayer.stop();exoPlayer.seekTo(0);}


    public void playNext(){
        {
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        PrepStage=false;
        playSong();
    }
    private void send(){
        Intent intent= new Intent("Prep");
        intent.putExtra("PrepState",PrepStage);
        intent.putExtra("SongIndex",songPosn);
        LocalBroadcastManager.getInstance(MusicService.this).sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

}

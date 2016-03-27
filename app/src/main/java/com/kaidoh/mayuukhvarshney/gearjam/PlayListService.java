package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 15/03/16.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.exoplayer.ExoPlayer;

import java.util.ArrayList;
import java.util.Random;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 *
 * Sue Smith - February 2014
 */

public class PlayListService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player

    public MediaPlayer player;
    //song list
    private ArrayList<String> songs;
    //current position
    private int songPosn;
    private boolean PrepStage=false;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    private String songTitle="";
    //notification id
    private static final int NOTIFY_ID=1;
    //shuffle flag and random
    private boolean shuffle=false;
    private Random rand;
    public boolean pause;
    private final Handler handler=new Handler();
    Intent intent;
    private ExoPlayer exoPlayer;
    private ArrayList<String> path;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    public void onCreate(){
        //create the service
        super.onCreate();
        intent=new Intent();
        //initialize position
        songPosn=0;
        //random
        rand=new Random();
        //create player
        player = new MediaPlayer();
        intent = new Intent();
        //initialize
        initMusicPlayer();




    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    private Runnable SendUpdates;

    //pass song list
    public void setList(ArrayList<String> theSongs){
        songs=theSongs;
    }


    //binder
    public class MusicBinder extends Binder {
        PlayListService getService() {
            return PlayListService.this;
        }

    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    //play a song
    public void playSong(String uri){
        //play
        if(player.isPlaying())
        {
            player.stop();
            player.reset();
            PrepStage=false;

        }
        else
        {
            if(PrepStage && pause)

            {   player.reset();
                PrepStage=false;
            }
        }

        //get song
        songTitle= songs.get(songPosn);
        //get title
          //get id
        // long currSong = playSong.getID();
        //set uri

        //set the data source
          try{
            player.setDataSource(uri);
        }
        catch(Exception e){
          Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
           player.prepareAsync();
    }





    //set the song
    public void setSong(int songIndex){
        songPosn=songIndex;

    }
    public int getSongIndex(){
        return songPosn;
    }
    @Override
    public void  onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }





    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        PrepStage=true;
        send();
        mp.start();
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_filled_white_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Now Playing")
                .setContentText(songTitle)
                .setAutoCancel(true);

        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }


    //playback methods
    public long getPosn(){
        return exoPlayer.getCurrentPosition();
    }
    public void StopPlayer(){
        player.stop();
    }
    public boolean PrepState(){
        return PrepStage;
    }
    public long getDur(){
        return exoPlayer.getDuration();
    }

    public boolean isPng(){
        return exoPlayer.getPlayWhenReady();
    }

    public void pausePlayer(){
        exoPlayer.setPlayWhenReady(false);
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        exoPlayer.setPlayWhenReady(true);
    }

    //skip to previous track
    public void PauseState(boolean Pause_state){
        pause=Pause_state;
    }
    public void Paths(ArrayList<String> thepaths){
        path=thepaths;
    }


    //skip to next
    public void playNext(){
        {
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        PrepStage=false;

        playSong(path.get(songPosn));
    }
    private void States(){
        intent.putExtra("Stage",PrepStage);
        intent.putExtra("Current",songPosn);
        sendBroadcast(intent);
    }
    private void send(){
        Intent intent= new Intent("Prep");
        intent.putExtra("message","get this shit over already");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }


    //toggle shuffle

}
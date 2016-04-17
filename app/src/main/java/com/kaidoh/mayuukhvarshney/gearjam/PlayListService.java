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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class PlayListService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player

    public MediaPlayer player;
    //song list
    private ArrayList<LinkedHashMap<String,String>> songs;
    private List<Track> TrackList;
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
    protected LinkedHashMap<Integer,String> theIDs;

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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Do what you need in onStartCommand when service has been started
        //Bundle extras= intent.getExtras();
        player.stop();
        player.reset();

        return START_NOT_STICKY;
    }
    //pass song list
    public void setList(final ArrayList<LinkedHashMap<String,String>> theSongs){
        songs=theSongs;
    }
    public void setTrackList(List<Track> tracks){

        TrackList=tracks;
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
    public void playSong(){
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

            {  player.stop();
                player.reset();
                PrepStage=false;
            }
        }
        //get song

        //get title
        //songTitle=songs.get(songPosn).get("SongTitle");
       Track track;
        track= TrackList.get(songPosn);
        songTitle=track.getTitle();
        String SongPath=theIDs.get(track.getID());
        //get id
        // long currSong = playSong.getID();
        //set uri

        //set the data source
        try{
            player.setDataSource(SongPath);
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
    public void setIDS(LinkedHashMap<Integer,String> IDS){
        theIDs=IDS;
    }
    public int getSongIndex(){
        return songPosn;
    }
    public void TestMethod(){
        if(theIDs!=null){
            Log.d("PlayListService ","Map sent and recieved");
        }
        else
        {
            Log.d("PlayListService","Unsuccesful map not recived");
        }
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
        Intent notIntent = new Intent(PlayListService.this, Playlist.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        //notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendInt = PendingIntent.getActivity(PlayListService.this, PendingIntent.FLAG_CANCEL_CURRENT,
                notIntent, 0);
        // PendingIntent pendInt= PendingIntent.getActivity(MusicService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        //  PendingIntent pendInt = PendingIntent.getActivity(MusicService.this,0,intent,0);

        Notification.Builder builder = new Notification.Builder(PlayListService.this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_filled_white_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);


    }


    //playback methods
    public int getPosn(){
        return player.getCurrentPosition();
    }
    public boolean PrepState(){
        return PrepStage;
    }
    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }



    public void go(){
        player.start();
    }

    public void stop(){
        player.stop();
        player.reset();
    }

    //skip to previous track
    public void PauseState(boolean Pause_state){
        pause=Pause_state;
    }

    //skip to next
    public void playNext(){
        {
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        PrepStage=false;
        playSong();
    }
    private void States(){
        intent.putExtra("Stage",PrepStage);
        intent.putExtra("Current",songPosn);
        sendBroadcast(intent);
    }
    private void send(){
        Intent intent= new Intent("Prep");
        intent.putExtra("Current",songPosn);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }



}
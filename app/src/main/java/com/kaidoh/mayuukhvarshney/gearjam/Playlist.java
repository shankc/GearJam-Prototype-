package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 14/03/16.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kaidoh.mayuukhvarshney.gearjam.PlayListService.MusicBinder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Playlist  extends AppCompatActivity implements PlayListFragment.PlayListPass {
    private SCTrackAdapter mAdapter;
    public static Activity playlistactivity;
    private ListView listView,lv;
    private SeekBar mSeek_Bar;
    protected TextView mSelectedTrackTitle,mArtistTitile;
    protected ImageView mSelectedTrackImage,mPlayerControl;
    private int mediapos;
    private int mediamax;
    private boolean flag=false;
    private List<Track> mPlayListItems;
    private int Next_position=0;
    private int mark=0;
    protected LinkedHashMap<Integer,String> mIDs= new LinkedHashMap<>();
    protected  ArrayList<LinkedHashMap<String,String>>SongList;
    private boolean Pause=false;
    private final Handler mHandler=new Handler();
    private boolean ConnectionFlag;
    private ListAdapter adapter;
    protected Intent playIntent;
    protected PlayListService musicSrv= new PlayListService();
    private boolean musicBound=false;
    private BroadcastReceiver mMessageReceiver;
    private int Current_position=0;
    protected DisplayTrackActivity display;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        playlistactivity=this;
        setContentView(R.layout.playlist_list);

        AppControl app= (AppControl)getApplication();
        app.play=this;

        AppControl appcontrolreverse= (AppControl)getApplication();
        display=appcontrolreverse.Display;
       // mPlayListItems= new ArrayList<Track>();
        //SongList= new ArrayList<HashMap<String,String>>();
        //listView = (ListView) findViewById(R.id.track_list_view);
        //mAdapter = new SCTrackAdapter(this, mPlayListItems);

        //listView.setAdapter(adapter);

       // adapter = new SimpleAdapter(Playlist.this, SongList,
         //       R.layout.playlist_item_offline, new String[] { "SongTitle" }, new int[] {
           //     R.id.track_title});
        //listView.setAdapter(mAdapter);
        PlayListFragment fragobj=new PlayListFragment();
        Bundle bundle=new Bundle();
        fragobj.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FragmentContainer, fragobj);
        ft.addToBackStack(null);
        ft.commit();

        mSeek_Bar = (SeekBar) findViewById(R.id.seek_bar);
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);
        ImageView Sc_icon=(ImageView)findViewById(R.id.soundcloud_icon);
        mArtistTitile=(TextView) findViewById(R.id.artist_name);
        mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
        Sc_icon.setImageResource(R.drawable.logo_sc_white);

        //SongPathList=new ArrayList<>();
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toggle();
            }
        });

            mMessageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();
                    int index = extras.getInt("Current");
                    Log.d("PlayList", "the index" + index);

                    if (Current_position != index) {
                        Track track = mPlayListItems.get(index);
                        //mSelectedTrackTitle.setText(SongList.get(index).get("SongTitle"));
                        mSelectedTrackTitle.setText(track.getTitle());
                        mArtistTitile.setText(track.getUser().getUsername());
                        Picasso.with(Playlist.this).load(track.getArtworkURL()).into(mSelectedTrackImage);

                    }
                    ToggleSwitch();
                    SeekInit();

                }
            };
        }





    private void SeekInit() {
        if (musicSrv.isPng()) {
            mediapos = musicSrv.getPosn();
            mediamax = musicSrv.getDur();
            mSeek_Bar.setMax((int) mediamax);
            mSeek_Bar.setProgress((int) mediapos);
            mHandler.removeCallbacks(moveSeekBarThread);
            mHandler.postDelayed(moveSeekBarThread, 100);

        }
    }

    private Runnable moveSeekBarThread=new Runnable() {
        @Override
        public void run() {
            if(musicSrv.isPng()){
                int newmediapos=musicSrv.getPosn();
                int newmediamax=musicSrv.getDur();
                mSeek_Bar.setMax(newmediamax);
                mSeek_Bar.setProgress(newmediapos);
                mHandler.postDelayed(this,100);
            }
        }
    };


    private void Toggle() {
        if (musicSrv.isPng()) {
            musicSrv.pausePlayer();
            Pause=true;
            mPlayerControl.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
        } else {

            musicSrv.go();
            Pause = false;
            mPlayerControl.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);

        }
        musicSrv.PauseState(Pause);
    }

    public void ToggleSwitch() {
        if(musicSrv.isPng()) {
            mPlayerControl.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
        }
        else
        {
            mPlayerControl.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(playIntent);
        //musicSrv.onUnbind(playIntent);
        musicSrv=null;

       // unbindService(musicConnection);
    }
    @Override
    public void onBackPressed(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d("PlayList", "pause state reached ");
      if(musicSrv!=null)
      {
          Log.d("PlayList","Music service not null");
      }
else
      {
          Log.d("Playlist", "Music Servide null");
      }
    }
    @Override
    protected void onResume(){
        super.onResume();
       if(musicBound)
       {



       }
        if (Pause) {

            Pause = false;
            musicSrv.PauseState(Pause);

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Prep"));
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, PlayListService.class);
            //  playIntent.putExtra("M",new Messenger(messageHandler));
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    protected ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder=(MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(SongList);
            musicSrv.setTrackList(mPlayListItems);
            musicSrv.setIDS(mIDs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;

        }

    };
    public boolean Detectplayer(){
        if(musicSrv!=null)
        {
            return true;
        }
        return false;
    }
    public void StopPlaying()
    {
       musicSrv.stop();

        stopService(playIntent);
        onStop();
        //musicSrv.onUnbind(playIntent);

       // Log.d("stopplaying "," it is really showing!!");

    }
    @Override
    public void onPlayListPass(ArrayList<LinkedHashMap<String,String>> tracks,List<Track> Songs,LinkedHashMap<Integer,String> IDS){
        SongList=tracks;
        mPlayListItems=Songs;
        mIDs=IDS;
    }


}


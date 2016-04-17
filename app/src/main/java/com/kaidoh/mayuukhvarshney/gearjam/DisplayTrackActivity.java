package com.kaidoh.mayuukhvarshney.gearjam;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kaidoh.mayuukhvarshney.gearjam.MusicService.MusicBinder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisplayTrackActivity extends AppCompatActivity implements SongList.DataPass{

    private static final String TAG = "DisplayTrackActivity";

    private List<Track> mPlayListItems;
    private Set<Track> Song_Set;
    private SCTrackAdapter mAdapter;
    protected TextView mSelectedTrackTitle,mArtistTitile;
    protected  ImageView mSelectedTrackImage,Sc_icon;
    protected MediaPlayer mMediaPlayer;
    private ImageView mPlayerControl;
    private SeekBar mSeek_Bar;
    private boolean Pause=false;
    protected int Current_position=0;
    protected long mediapos;
    protected long mediamax;
    private ListView listView;
    public MusicService musicSrv=new MusicService();
    private Track track=new Track();
    private boolean flag=false;
    private final Handler mHandler=new Handler();
    private boolean Preparation = true;
    private boolean musicBound=false;
    private BroadcastReceiver mMessageReceiver;
    protected String Inst,category,SuperInst,Supercategory;
    private Intent playIntent;
  protected Playlist  playlist;
    protected  File folder;
    Map<String, String> params = new HashMap<>();
    Map<String, Integer> offset = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_list);
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        AppControl app= (AppControl)getApplication();
        playlist=app.play;

        AppControl appcontrolreverse= (AppControl)getApplication();
        appcontrolreverse.Display=this;

        if(savedInstanceState==null)
        {
            Bundle extras=getIntent().getExtras();
            Inst=extras.getString("Instrument");
            category=extras.getString("Genre");
        }
        else
        {
            Inst=(String)savedInstanceState.getSerializable("Instrument");
            category=(String)savedInstanceState.getSerializable("Genre");
        }

        //Bundle bundle = new Bundle();
        //bundle.putString("Instrument", Inst);
        //bundle.putString("Genre",category);
        //SongList fragobj = new SongList();
        //fragobj.setArguments(bundle);
        //SongList SongFragment = new SongList();
        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.add(R.id.FragmentContainer, SongFragment);
        //ft.commit();
        SongList fragobj=new SongList();
        Bundle bundle=new Bundle();
        bundle.putString("Instrument", Inst);
        bundle.putString("Genre", category);
        fragobj.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FragmentContainer, fragobj);
        ft.addToBackStack(null);
        ft.commit();

      folder = new File(Environment.getExternalStorageDirectory()+File.separator+"GearJam");

        boolean success=true;
        if(!folder.exists())
        {
            success=folder.mkdir();

        }
        if(success){
            Log.d(TAG,"folder made!!");
        }
        //SongList SongFragment = new SongList();
        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //getSupportFragmentManager().beginTransaction().add(R.id.FragmentContainer, SongFragment).commit();
        // calling adapter here...
      /*  mPlayListItems = new ArrayList<Track>();
        Song_Set=new LinkedHashSet<Track>(mPlayListItems);
        listView = (ListView) findViewById(R.id.track_list_view);
        mAdapter = new SCTrackAdapter(this, mPlayListItems);
        listView.setAdapter(mAdapter); */

        // setting the buttons and icons via id's.

        mSeek_Bar =  (SeekBar)findViewById(R.id.seek_bar);
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);
        mArtistTitile=(TextView) findViewById(R.id.artist_name);
        Sc_icon=(ImageView)findViewById(R.id.soundcloud_icon);
       // mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
        Sc_icon.setImageResource(R.drawable.logo_sc_white);


// player control
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toggle();
            }
        });


// clicking of a list view.

        // download song as a playlist.

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                track = mPlayListItems.get(position);
                String path = "https://api.soundcloud.com/tracks/" + track.getID() + "/stream?client_id=" + Config.CLIENT_ID;
                Log.d(TAG, path);

                new DownloadFileFromURL(Integer.toString(track.getID()), track.getTitle()).execute(path);

                return true;
            }
        });*/
// Initializing the soundcloud to fill the list view with songs.
       /* SCService scService = SoundCloud.getService();

// Query parameters added into the app according to key value pairs.
        params.put("q",Inst);
        params.put("tags",category);
        // the key tags can be given the values of the pre decided genre values for each instruments.
        // the offset map DS has key value pair of limit and the value of anything between 10-200{max} according to the soundcloud rules. so the results on the page will display according to the value given here!!
        //offset.put("limit", 1);
        // offset.put("limit", 100);
        offset.put("limit", 125);
        // offset.put("linked_partitioning",1);
        scService.getRecentTracks(params, offset, new Callback<List<Track>>() {
            @Override
            public void success(List<Track> tracks, Response response) {
                loadTracks(tracks);

            }


            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Error: " + error);
                Toast.makeText(DisplayTrackActivity.this, "There was a Problem :(", Toast.LENGTH_SHORT).show();
            }

        });*/
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int index;
                boolean isplaying;
                Bundle extras=intent.getExtras();
                index=extras.getInt("SongIndex");
                Preparation=extras.getBoolean("PrepState");
                if(Current_position!=index){
                    Track track = mPlayListItems.get(index);
                    mSelectedTrackTitle.setText(track.getTitle());
                    mArtistTitile.setText(track.getUser().getUsername());
                    Picasso.with(DisplayTrackActivity.this).load(track.getArtworkURL()).into(mSelectedTrackImage);
                }
                ToggleSwitch();
                SeekInit();

            }
        };
        FragmentManager fm = getSupportFragmentManager();

//if you added fragment via layout xml



    }







    // methods for seekbar.
    private void SeekInit() {
        if(musicSrv!=null) {
            if (musicSrv.isPng()) {
                mediapos = musicSrv.getPosn();
                mediamax = musicSrv.getDur();
                mSeek_Bar.setMax((int) mediamax);
                mSeek_Bar.setProgress((int) mediapos);
                mHandler.removeCallbacks(moveSeekBarThread);
                mHandler.postDelayed(moveSeekBarThread, 100);

            }
        }
        else
        {
            Log.d("DisplayTrackAcivity","Came Crashing here in seekint"+musicBound);
        }
    }

// seekbar methods

    private Runnable moveSeekBarThread=new Runnable() {
        @Override
        public void run() {
            if(musicSrv!=null) {
                if (musicSrv.isPng()) {
                    long newmediapos = musicSrv.getPosn();
                    long newmediamax = musicSrv.getDur();
                    mSeek_Bar.setMax((int) newmediamax);
                    mSeek_Bar.setProgress((int) newmediapos);
                    mHandler.postDelayed(this, 100);
                }
            }
            else
            {
                Log.d("DisplayTrackActivity","Crashing here run"+musicBound );
            }
        }
    };

    // tracks get loaded to the adapter..
    private void loadTracks(final List<Track> tracks) {
        mPlayListItems.clear();
        mPlayListItems.addAll(tracks);
        Song_Set=new LinkedHashSet<Track>(mPlayListItems);
        mPlayListItems.clear();
        mPlayListItems.addAll(Song_Set);
        Collections.shuffle(mPlayListItems);
        mAdapter.notifyDataSetChanged();

    }


///toggle of play pause on a click.

    public void Toggle() {

        if (musicSrv.isPng()) {
            Pause = true;
            musicSrv.pausePlayer();
            mPlayerControl.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
        } else {
            Pause = false;
            musicSrv.go();
            mPlayerControl.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
        }
        musicSrv.PauseState(Pause);

    }
    //auto toggle of during auto play.
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
        musicSrv=null;
        unbindService(musicConnection);

        LocalBroadcastManager.getInstance(this).unregisterReceiver((mMessageReceiver));

    }


    @Override
    protected void onPause(){
        super.onPause();
        // Pause= true;
        //musicSrv.StopPlayer();
        Log.d("DisplayTrackActivity", "Pause statte Reached");
        if(musicSrv.equals(null)){
            Log.d("DisplayTrackActivty", "~True");
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            //  playIntent.putExtra("M",new Messenger(messageHandler));
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSeek_Bar.setMax(0);


        Log.d("DisplayTrackActivity", "the current instrument " + SuperInst);
        if(playlist==null)
        {
            Log.d("DisplayTrackActivity","playlist reference has become null");
        }
        else{
        Log.d("DisplayTrackActivity"," the playlist reference right now false");}
        SongList fragment = (SongList) getSupportFragmentManager().findFragmentById(R.id.FragmentContainer);

        // musicSrv= new MusicService();


        if(musicSrv.equals(null)){
            Log.d("DisplayTrackActivty","~True");
        }
        if(musicBound && SuperInst!=null)
        {
            SongList fragobj=new SongList();
            Bundle bundle=new Bundle();
            bundle.putString("Instrument", SuperInst);
            bundle.putString("Genre", Supercategory);
            fragobj.setArguments(bundle);
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentContainer, fragobj);
            ft.addToBackStack(null);
            ft.commit();

//          SongList fragment = (SongList) getSupportFragmentManager().findFragmentById(R.id.FragmentContainer);
           //musicSrv.setList(fragment.theSongList());
            //onDataPass(fragment.mPlayListItems);
        }
        else if(!musicBound && SuperInst!=null)
        {
            Log.d("DisplayTrackActivity","In else if method");
            onStart();
            SongList fragobj=new SongList();
            Bundle bundle=new Bundle();
            bundle.putString("Instrument", SuperInst);
            bundle.putString("Genre", Supercategory);
            fragobj.setArguments(bundle);
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentContainer, fragobj);
            ft.addToBackStack(null);
            ft.commit();

        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Prep"));
        Log.d("DisplayTrackACtivity", "On Resume STate reached " + musicBound);

        if (Pause) {

            Pause = false;
            musicSrv.PauseState(Pause);
        }



    }
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //SongList songfragment=(SongList)getSupportFragmentManager().
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(mPlayListItems);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;

        }

    };
    @Override
    public void onBackPressed()
    {
      if(SuperInst!=null){
          Log.d("DisplayTrackActivity","Null");

          if( SuperInst.equals("Trance")){
              Intent intent = new Intent(this,ElectronicGenre.class);

              Log.d("DsiplayTrackActivity","going back to ElectronicGenre S "+SuperInst);
              startActivity(intent);
          }
          else if(SuperInst.equals("guitar")){
              Intent intent = new Intent(this,GuitarGenre.class);

              Log.d("DsiplayTrackActivity","going back to GuitarGenre S "+SuperInst);
              startActivity(intent);
          }
          else if(SuperInst.equals("violin")){
              Intent intent = new Intent(this,ViolinGenre.class);

              Log.d("DsiplayTrackActivity","going back to Violin S "+SuperInst);
              startActivity(intent);

          }
          else if(SuperInst.equals("Sitar")){
              Intent intent = new Intent(this,SitarGenre.class);

              Log.d("DsiplayTrackActivity","going back to Sitar S"+SuperInst);
              startActivity(intent);
          }
          else if(SuperInst.equals("Piano")){
              Intent intent = new Intent(this,PianoGenre.class);
              Log.d("DsiplayTrackActivity","going back to Piano S "+SuperInst);
              startActivity(intent);
          }
      }
        else{
          Log.d("DisplayTrackActivity","Not null");
          if(Inst.equals("Trance")){
              Intent intent = new Intent(DisplayTrackActivity.this,ElectronicGenre.class);

              Log.d("DsiplayTrackActivity","going back to ElectronicGenre "+Inst);
              startActivity(intent);
          }
          else if(Inst.equals("guitar")){
              Intent intent = new Intent(DisplayTrackActivity.this,GuitarGenre.class);

              Log.d("DsiplayTrackActivity","going back to GuitarGenre "+Inst);
              startActivity(intent);
          }
          else if(Inst.equals("violin")){
              Intent intent = new Intent(DisplayTrackActivity.this,ViolinGenre.class);

              Log.d("DsiplayTrackActivity","going back to Violin "+Inst);
              startActivity(intent);

          }
          else if(Inst.equals("Sitar")){
              Intent intent = new Intent(DisplayTrackActivity.this,SitarGenre.class);

              Log.d("DsiplayTrackActivity","going back to Sitar "+Inst);

              startActivity(intent);
          }
          else if(Inst.equals("Piano")){
              Intent intent = new Intent(DisplayTrackActivity.this,PianoGenre.class);

              Log.d("DsiplayTrackActivity","going back to Piano "+Inst);
              startActivity(intent);
          }
      }


//         Log.d("DisplayTrackActivity","The current Instrument "+Inst+" "+SuperInst);
    }
    public boolean DetectPlayer(){
        Log.d("DisplayTrackActivity","detect player accesible");
        if(musicSrv!=null)
        {
            return true;

        }
        else
            return false;
    }
    public void StopPlaying(){
        musicSrv.ResetPlayer();
        stopService(playIntent);
        onStop();


    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;

        }
        else if(id==R.id.playlist_item){
            Intent intent= new Intent(this,Playlist.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

@Override

    public void onDataPass(List<Track> tracks){
    mPlayListItems=tracks;
}
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SuperInst=intent.getStringExtra("Instrument");
        Supercategory=intent.getStringExtra("Genre");

    }

}

package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 14/03/16.
 */

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Playlist  extends Activity {
    private SCTrackAdapter mAdapter;
    private ListView listView,lv;
    private SeekBar mSeek_Bar;
    private TextView mSelectedTrackTitle,mArtistTitile;
    private ImageView mSelectedTrackImage,mPlayerControl;
    private int mediapos;
    private int mediamax;
    private MediaPlayer mMediaPlayer;
    private boolean flag=false;
    private List<Track> mPlayListItems;
private int Next_position=0;
    private int mark=0;
    private List<String> SongPathList;
   private  ArrayList<HashMap<String,String>>SongList;
    private boolean Pause=false;
    private final Handler mHandler=new Handler();
    private boolean ConnectionFlag;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_list);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

       mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                flag = true;
                //Log.d(TAG, "prep state" + flag);
                togglePlayPause();
                mediapos = mMediaPlayer.getCurrentPosition();
                mediamax = mMediaPlayer.getDuration();
                mSeek_Bar.setMax(mediamax);
                mSeek_Bar.setProgress(mediapos);
                mHandler.removeCallbacks(moveSeekBarThread);
                mHandler.postDelayed(moveSeekBarThread, 100);
            }
        });
        mPlayListItems= new ArrayList<Track>();
        SongList= new ArrayList<HashMap<String,String>>();
        listView = (ListView) findViewById(R.id.track_list_view);
        mAdapter = new SCTrackAdapter(this, mPlayListItems);

        //listView.setAdapter(adapter);

        adapter = new SimpleAdapter(Playlist.this, SongList,
                R.layout.playlist_item_offline, new String[] { "SongTitle" }, new int[] {
                R.id.track_title});
listView.setAdapter(adapter);

        mSeek_Bar = (SeekBar) findViewById(R.id.seek_bar);
         mSeek_Bar.setMax(mMediaPlayer.getDuration());
        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);
        ImageView Sc_icon=(ImageView)findViewById(R.id.soundcloud_icon);
        mArtistTitile=(TextView) findViewById(R.id.artist_name);
        mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
        Sc_icon.setImageResource(R.drawable.logo_sc_white);

        SongPathList=new ArrayList<>();
        File home = new File("/sdcard/GearJam/");
        SCTrackService trackService= SoundCloud.getTrackService();

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
               HashMap<String,String> song= new HashMap<>();
                song.put("SongTitle", file.getName().substring(0, findbracket(file.getName())));
                song.put("SongPath",file.getPath());
                String txt=file.getName().substring(0,findbracket(file.getName()));
                SongList.add(song);
                int temp1=convert(filter(file.getName()));
                final String temp=file.getPath();
                trackService.getTrack(temp1, new Callback<Track>() {
                    @Override
                    public void success(Track track, Response response) {
                        mPlayListItems.add(track);

                        SongPathList.add(temp);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(Playlist.this, "Network Error ArtWork couldn't be Loaded :(", Toast.LENGTH_SHORT).show();
                        ConnectionFlag = true;


                    }

                });

            }


        }
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Next_position=position;
                //Track track = .get(position);
                String path = SongList.get(position).get("SongPath");
                Log.d("This","path"+path);
                // MediaPlayer mp=new MediaPlayer();
                //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mSelectedTrackTitle.setText(SongList.get(position).get("SongTitle"));
              //  mArtistTitile.setText(track.getUser().getUsername());
                //if (//track.getArtworkURL() != null) {
                    //Picasso.with(Playlist.this).load(track.getArtworkURL()).into(mSelectedTrackImage);
                 //else
                // {
                    mSelectedTrackImage.setImageResource(R.drawable.waiting_image);




                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    flag = false;
                } else {

                    if (Pause && flag) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        flag = false;

                    }
                }

                try {

                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.prepareAsync();

                } catch (Exception e) {
                    e.printStackTrace();

                }
                Next_position = position + 1;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);

                if (flag) {
                    gotoNextTrack();
                }


            }
        });

    }

    private Runnable moveSeekBarThread=new Runnable() {
        @Override
        public void run() {
            if(mMediaPlayer.isPlaying()){
                int newmediapos=mMediaPlayer.getCurrentPosition();
                int newmediamax=mMediaPlayer.getDuration();
                mSeek_Bar.setMax(newmediamax);
                mSeek_Bar.setProgress(newmediapos);
                mHandler.postDelayed(this,100);
            }
        }
    };


    private void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            Pause=true;
            mPlayerControl.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
        } else {
            if(flag) {
                mMediaPlayer.start();
                Pause = false;
                mPlayerControl.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
            }
        }
    }

    private void gotoNextTrack(){
        //  Next_position++;
        flag=false;
        Track next_track=mPlayListItems.get(Next_position);
        String path=SongList.get(Next_position).get("SongPath");
        mSelectedTrackTitle.setText(next_track.getTitle());
        //Picasso.with(Playlist.this).load(next_track.getArtworkURL()).into(mSelectedTrackImage);
        mSeek_Bar.setProgress(0);

        mMediaPlayer.reset();

        try{

            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();

        }

        catch(Exception e){
            e.printStackTrace();
            Log.e(" ","error is " + e);
        }

        Next_position++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        mMediaPlayer.pause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(!mMediaPlayer.isPlaying())
        {
            mMediaPlayer.start();
        }
    }
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
    public String filter(String txt){

        for(int i=0;i<txt.length();i++)
        {
            if(txt.charAt(i)=='[' ){
                mark = i;
                break;
            }
        }
        int start=mark+1;
        StringBuilder ID=new StringBuilder();
        while(txt.charAt(start)!=']')
        {
            ID.append(txt.charAt(start));
            start++;
        }

        return ID.toString();
    }
    public int findbracket(String txt){
         int flag=0;
        for(int i=0;i<txt.length();i++)
        {
            if(txt.charAt(i)=='['){
                flag=i;
                break;
            }
        }
        return flag;
    }
    public int convert(String s){
        int foo=Integer.parseInt(s);
        return foo;
    }
}

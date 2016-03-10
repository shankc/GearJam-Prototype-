package com.kaidoh.mayuukhvarshney.gearjam;


import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DisplayTrackActivity extends Activity {

    private static final String TAG = "MainActivity";

    private List<Track> mListItems;
    private Set<Track> Song_Set;
    private SCTrackAdapter mAdapter;
    private TextView mSelectedTrackTitle,mArtistTitile;
    private ImageView mSelectedTrackImage,Sc_icon;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerControl;
    private SeekBar mSeek_Bar;
    private int Page_Counter = 0;
    private int Start_Index;
    private boolean Pause=false;
    private int Next_position=0;
    private int mediapos;
    private int mediamax;
    private static final int Page_Limit = 30;
    private ListView listView;
    private boolean flag=false;
    private final Handler mHandler=new Handler();
    String Inst,category;
    Map<String, String> params = new HashMap<String, String>();
    Map<String, Integer> offset = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_list);
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




// calling adapter here...
        mListItems = new ArrayList<Track>();
        Song_Set=new LinkedHashSet<Track>(mListItems);
        listView = (ListView) findViewById(R.id.track_list_view);
        //footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progress, null, false);
        //listView.addFooterView(footer);
        mAdapter = new SCTrackAdapter(this, mListItems);
        listView.setAdapter(mAdapter);

        mSeek_Bar = (SeekBar) findViewById(R.id.seek_bar);
        mSeek_Bar.setMax(mMediaPlayer.getDuration());

        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) findViewById(R.id.player_control);
        mArtistTitile=(TextView) findViewById(R.id.artist_name);
        Sc_icon=(ImageView)findViewById(R.id.soundcloud_icon);
        mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
        Sc_icon.setImageResource(R.drawable.logo_sc_white);



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
                Track track = mListItems.get(position);

                // MediaPlayer mp=new MediaPlayer();
                //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mSelectedTrackTitle.setText(track.getTitle());
                mArtistTitile.setText(track.getUser().getUsername());
                if(track.getArtworkURL()!=null)
                { Picasso.with(DisplayTrackActivity.this).load(track.getArtworkURL()).into(mSelectedTrackImage);}
                else{
                    mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
                }
                //  mMediaPlayer.setDataSource(track.getStreamURL() +"?client_id" + Config.CLIENT_ID);


                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    flag = false;
                } else {

                    Log.d(TAG, "prep stage " + flag);
                    if (Pause && flag) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        flag = false;

                    }
                }

                try {

                    mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                    mMediaPlayer.prepareAsync();
                    //mMediaPlayer.reset();
                    // mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "error is " + e);
                }
                Next_position = position + 1;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                Log.d(TAG, "the position is=" + Next_position);
                Log.d(TAG, "the preparation state is " + flag);
                //   gotoNextTrack();
                if (flag) {
                    gotoNextTrack();
                }


            }
        });


        SCService scService = SoundCloud.getService();

// Query parameters added into the app according to key value pairs.
       params.put("q",Inst);
        params.put("tags",category);
        // the key tags can be given the values of the pre decided genre values for each instruments.
        // the offset map DS has key value pair of limit and the value of anything between 10-200{max} according to the soundcloud rules. so the results on the page will display according to the value given here!!
        //offset.put("limit", 1);
       // offset.put("limit", 100);
        offset.put("limit",125);
       // offset.put("linked_partitioning",1);
        scService.getRecentTracks(params, offset, new Callback<List<Track>>() {
            @Override
            public void success(List<Track> tracks, Response response) {
                loadTracks(tracks);

            }


            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Error: " + error);
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

    // tracks get loaded to the adapter..
    private void loadTracks(final List<Track> tracks) {
        mListItems.clear();
        mListItems.addAll(tracks);
        Song_Set=new LinkedHashSet<Track>(mListItems);
        mListItems.clear();
        mListItems.addAll(Song_Set);
        Collections.shuffle(mListItems);
        mAdapter.notifyDataSetChanged();
       /* for (int i = 0; i < 10; i++) {
            mListItems.add(tracks.get(i));
        }
        Page_Counter = 10;
        Start_Index=Page_Counter;
        //Collections.shuffle(mListItems.subList(0,9));
        mAdapter.notifyDataSetChanged();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() - listView.getFooterViewsCount()) >= (mAdapter.getCount() - 1)) {
                    Toast.makeText(DisplayTrackActivity.this, "no more results :(", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (Page_Counter < Page_Limit) {
                        for (int i = 0; i <10 ; i++) {
                            mListItems.add(tracks.get(Page_Counter));
                            Page_Counter++;
                        }
                       // Collections.shuffle(mListItems.subList(Start_Index,Page_Counter));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
   */ }





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
        Track next_track=mListItems.get(Next_position);

        mSelectedTrackTitle.setText(next_track.getTitle());
        Picasso.with(DisplayTrackActivity.this).load(next_track.getArtworkURL()).into(mSelectedTrackImage);
        mSeek_Bar.setProgress(0);

        mMediaPlayer.reset();

        try{

            mMediaPlayer.setDataSource(next_track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
            mMediaPlayer.prepareAsync();

        }

        catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,"error is " + e);
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

        return super.onOptionsItemSelected(item);
    }




}

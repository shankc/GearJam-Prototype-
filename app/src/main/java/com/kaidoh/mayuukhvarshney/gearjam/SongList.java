package com.kaidoh.mayuukhvarshney.gearjam;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

/**
 * Created by mayuukhvarshney on 28/03/16.
 */
public class SongList extends Fragment {
    Map<String, String> params = new HashMap<>();
    Map<String, Integer> offset = new HashMap<>();
    public  List<Track> mPlayListItems,AllSongs;
    protected Set<Track> Song_Set;
    protected SCTrackAdapter mAdapter;
    protected ListView listView;
    String Inst;
    String category;
    DataPass data;
    private Playlist playlist;
    protected int Current_position=0;
   MusicService mMusicService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
Bundle bundle=SongList.this.getArguments();
 Inst=bundle.getString("Instrument");
        category=bundle.getString("Genre");
        View view =inflater.inflate(R.layout.song_list, container, false);


mMusicService=((DisplayTrackActivity)getActivity()).musicSrv;

        //Inst = bundle.getString("Instrument");
        //category=bundle.getString("Genre");
       // Inst=((DisplayTrackActivity)getActivity()).SuperInst;
        //category=((DisplayTrackActivity)getActivity()).Supercategory;
        mPlayListItems = new ArrayList<Track>();
        Song_Set=new LinkedHashSet<Track>(mPlayListItems);
        listView = (ListView) view.findViewById(R.id.song_list_view);
        mAdapter = new SCTrackAdapter(getActivity(), mPlayListItems);
        listView.setAdapter(mAdapter);
        SCService scService = SoundCloud.getService();

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
                if(mMusicService!=null){
                    Log.d("SongList", "All good");
                    if(getActivity()==null)
                    {
                        Log.d("SongList","fucking class is fucking null now WTF!!!");
                    }
                    else
                    {
                        Log.d("Songlist","Dunno where the problems is ");
                    }
                    ((DisplayTrackActivity) getActivity()).musicSrv.setList(mPlayListItems);
                    AllSongs = tracks;

                }
                else
                {
                    Log.d("SongList","these is a problem with musicsrv becmoing null");
                    ((DisplayTrackActivity)getActivity()).onStart();
                    ((DisplayTrackActivity) getActivity()).musicSrv.setList(mPlayListItems);
                    AllSongs = tracks;
                }
                // Log.d("SongList","the songs right now "+mPlayListItems.get(4).getTitle());

            }


            @Override
            public void failure(RetrofitError error) {
                 Log.d("FRagment", "Error: " + error);
                Toast.makeText(getActivity(), "There was a Problem :(", Toast.LENGTH_SHORT).show();
            }

        });
        data.onDataPass(mPlayListItems);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Current_position = position;
                //when the playlist is not playing... or is in a pause state.
                if(((DisplayTrackActivity)getActivity()).playlist!=null)
                {
                    if(((DisplayTrackActivity)getActivity()).playlist.Detectplayer())
                    {
                        ((DisplayTrackActivity)getActivity()).playlist.StopPlaying();
                    }
                }
                //Playlist.playlistactivity.finish();
                Track track = mPlayListItems.get(position);
                ((DisplayTrackActivity) getActivity()).musicSrv.setSong(position);
                ((DisplayTrackActivity) getActivity()).Current_position = position;
                ((DisplayTrackActivity) getActivity()).mSelectedTrackTitle.setText(track.getTitle());
                ((DisplayTrackActivity) getActivity()).mArtistTitile.setText(track.getUser().getUsername());
                Picasso.with(getActivity()).load(track.getArtworkURL()).into(((DisplayTrackActivity) getActivity()).mSelectedTrackImage);
                ((DisplayTrackActivity) getActivity()).musicSrv.playSong();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(((DisplayTrackActivity)getActivity()).playlist!=null)
                {
                    if(((DisplayTrackActivity)getActivity()).playlist.Detectplayer())
                    {
                        ((DisplayTrackActivity)getActivity()).playlist.StopPlaying();
                    }
                }
                Track track= new Track();
                track = mPlayListItems.get(position);
                String path = "https://api.soundcloud.com/tracks/" + track.getID() + "/stream?client_id=" + Config.CLIENT_ID;
                //Log.d("SongList", path);
               Toast.makeText(getActivity(),"Adding to PlayList",Toast.LENGTH_SHORT).show();
                new DownloadFileFromURL(getActivity(),Integer.toString(track.getID()), track.getTitle(),((DisplayTrackActivity)getActivity()).folder).execute(path);

                return true;
            }
        });


        return view;
    }
    private void loadTracks(final List<Track> tracks) {
        mPlayListItems.clear();
        mPlayListItems.addAll(tracks);
        Song_Set=new LinkedHashSet<Track>(mPlayListItems);
        mPlayListItems.clear();
        mPlayListItems.addAll(Song_Set);
        Collections.shuffle(mPlayListItems);
        mAdapter.notifyDataSetChanged();

    }
    @Override
    public void onAttach(Activity a){
        super.onAttach(a);
        try
        {
            data=(DataPass) a;
        }
        catch(ClassCastException e){
            e.printStackTrace();
        }



    }
    public interface DataPass{
        void onDataPass(List<Track> tracks);
    }
    public List<Track> theSongList(){
        return AllSongs;
    }
}
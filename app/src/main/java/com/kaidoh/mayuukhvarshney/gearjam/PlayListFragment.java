package com.kaidoh.mayuukhvarshney.gearjam;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Collections;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by mayuukhvarshney on 10/04/16.
 */
public class PlayListFragment extends Fragment {
    private List<Track> mPlayListItems,TempArray;
    private int mark=0;
    private ListView listView;
    private SCTrackAdapter mAdapter;
    protected  ArrayList<LinkedHashMap<String,String>>SongList;
    protected  ArrayList<LinkedHashMap<String,String>> PlayListSongs;
    protected LinkedHashMap<Integer,String> IDS= new LinkedHashMap<>();
    private int Current_position=0;
   PlayListPass data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_list, container, false);

        mPlayListItems = new ArrayList<Track>();
        TempArray= new ArrayList<Track>();
        SongList = new ArrayList<LinkedHashMap<String, String>>();
        listView = (ListView) view.findViewById(R.id.song_list_view);
        mAdapter = new SCTrackAdapter(getActivity(), mPlayListItems);
        listView.setAdapter(mAdapter);
        File home = new File(Environment.getExternalStorageDirectory() + File.separator + "GearJam");
        SCTrackService trackService = SoundCloud.getTrackService();
        if (home.listFiles() == null) {
            Toast.makeText(getActivity(), "No Songs in PlayList :(", Toast.LENGTH_SHORT).show();
        } else {
            if (home.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : home.listFiles(new FileExtensionFilter())) {
                    LinkedHashMap<String, String> song = new LinkedHashMap<>();
                    //Log.d("File","the file is "+file.getName());
                    song.put("SongTitle", file.getName().substring(0, findbracket(file.getName())));
                    song.put("SongPath", file.getPath());
                    String txt = file.getName().substring(0, findbracket(file.getName()));
                    SongList.add(song);
                    final int temp1 = convert(filter(file.getName()));
                    final String temp = file.getPath();
                    trackService.getTrack(temp1, new Callback<Track>() {
                        @Override
                        public void success(Track track, Response response) {
                           mPlayListItems.add(track);
                           IDS.put(track.getID(),temp);
                           // ((Playlist)getActivity()).musicSrv.TestMethod();
                            mAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void failure(RetrofitError error) {

                            Toast.makeText(getActivity(), "Network Error ArtWork couldn't be Loaded :(", Toast.LENGTH_SHORT).show();



                        }

                    });

                }


                Collections.sort(mPlayListItems, new Comparator<Track>() {
                    @Override
                    public int compare(Track lhs, Track rhs) {
                        return lhs.getTitle().compareTo(rhs.getTitle());
                    }

                });
            }



            data.onPlayListPass(SongList, mPlayListItems, IDS);




            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (((Playlist)getActivity()).display != null) {
                        if (((Playlist)getActivity()).display.DetectPlayer()) {
                            ((Playlist)getActivity()).display.StopPlaying();
                        }
                    }
                    ((Playlist)getActivity()).musicSrv.setSong(position);
                    Track track = mPlayListItems.get(position);
                 //   ((Playlist)getActivity()).musicSrv.setSong(track.getTitle());

                    Current_position = position;
                    //mSelectedTrackTitle.setText(SongList.get(position).get("SongTitle"));
                    //mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
                    ((Playlist)getActivity()).mSelectedTrackTitle.setText(track.getTitle());
                    ((Playlist)getActivity()).mArtistTitile.setText(track.getUser().getUsername());
                    Picasso.with(getActivity()).load(track.getArtworkURL()).into(((Playlist)getActivity()).mSelectedTrackImage);
                    Log.d("PlayList", "the current Song " + track.getTitle());
                    ((Playlist)getActivity()).musicSrv.playSong();
                }
            });


        }

        return view;
    }
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
    public String filter(String txt){

        for(int i=0;i<txt.length();i++)
        {
            if(txt.charAt(i)=='<' ){
                mark = i;
                break;
            }
        }
        int start=mark+1;
        StringBuilder ID=new StringBuilder();
        while(txt.charAt(start)!='>')
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
            if(txt.charAt(i)=='<'){
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
    @Override
    public void onAttach(Activity a){
        super.onAttach(a);
        try
        {
            data=(PlayListPass) a;
        }
        catch(ClassCastException e){
            e.printStackTrace();
        }



    }
    public interface PlayListPass{
         void onPlayListPass(ArrayList<LinkedHashMap<String,String>> tracks,List<Track> songs,LinkedHashMap<Integer,String> ID);
    }

}

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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    protected LinkedHashMap<Integer,String> IDS;
    protected int tempID;
    protected String tempPath;
    protected int Current_position=0;
   PlayListPass data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_list, container, false);

        mPlayListItems = new ArrayList<Track>();
        TempArray= new ArrayList<Track>();
        SongList = new ArrayList<LinkedHashMap<String, String>>();
        IDS= new LinkedHashMap<>();
        listView = (ListView) view.findViewById(R.id.song_list_view);
        mAdapter = new SCTrackAdapter(getActivity(), mPlayListItems);
        listView.setAdapter(mAdapter);
       // File home = new File(Environment.getExternalStorageDirectory() + File.separator + "GearJam");
        File home=getActivity().getFilesDir();
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
                     tempID= convert(filter(file.getName()));

                     tempPath = file.getPath();

                    IDS.put(tempID,tempPath);

                    trackService.getTrack(tempID, new Callback<Track>() {

                        @Override
                        public void success(Track track, Response response) {
                           mPlayListItems.add(track);
                            Log.d("PlayListFragment","Track is"+track.getTitle());
                           //IDS.put(track.getID(),tempPath);
                           //((Playlist)getActivity()).musicSrv.TestMethod();
                            mAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("PLayListFragment","retrofit error ",error);
                            Toast.makeText(getActivity(), "Network Error ArtWork couldn't be Loaded :(", Toast.LENGTH_SHORT).show();



                        }

                    });

                }




            }


            data.onPlayListPass(SongList, mPlayListItems, IDS);




            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (((Playlist)getActivity()).display != null) {
                        if (((Playlist)getActivity()).display.DetectPlayer()) {
                            Log.d("PlayListFragment"," coming inside the if method ");
                            ((Playlist)getActivity()).display.StopPlaying();
                        }
                    }
                    ((Playlist)getActivity()).musicSrv.setSong(position);
                    Track track = mPlayListItems.get(position);
                    if(position==0)
                    {
                        Log.d("PlayListFragment","the song path "+IDS.get(track.getID()));
                    }
                 //   ((Playlist)getActivity()).musicSrv.setSong(track.getTitle());

                    Current_position = position;
                    //mSelectedTrackTitle.setText(SongList.get(position).get("SongTitle"));
                    //mSelectedTrackImage.setImageResource(R.drawable.waiting_image);
                    ((Playlist)getActivity()).mSelectedTrackTitle.setText(track.getTitle());
                    ((Playlist)getActivity()).mArtistTitile.setText(track.getUser().getUsername());
                    Picasso.with(getActivity()).load(track.getArtworkURL()).into(((Playlist) getActivity()).mSelectedTrackImage);
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


        for(int i=txt.length()-1;i>0;i--)
        {
            if(txt.charAt(i)=='>' ){
                mark = i;
                break;
            }
        }
        int start=mark-1;
        StringBuilder temp=new StringBuilder();
        while(txt.charAt(start)!='<')
        {
            temp.append(txt.charAt(start));
            start--;
        }

        String ID = new StringBuffer(temp).
                reverse().toString();
        return ID;
    }
    public int findbracket(String txt){
        int flag=0;
        for(int i=txt.length()-1;i>0;i--)
        {
            if(txt.charAt(i)=='>'){
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

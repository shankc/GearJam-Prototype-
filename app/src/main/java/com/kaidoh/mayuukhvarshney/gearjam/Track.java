package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 07/03/16.
 */
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayuukhvarshney on 30/01/16.
 */
public class Track {
    @SerializedName("title")
    private String mTitle;


    @SerializedName("stream_url")
    private String mStreamURL;

    @SerializedName("artwork_url")

    private String mArtworkURL;

    @SerializedName("genre")
    private String mgenre;

    @SerializedName("user")
    private Users mUser;

    @SerializedName("playback_count")
    private Integer mPlaybackCount;
    public String getTitle() {
        return mTitle;
    }


    public Integer getPlaybackCount(){ return mPlaybackCount;}

    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }
    public String getTrackType(){
        return mgenre;
    }

    public Users getUser(){return mUser;}

}

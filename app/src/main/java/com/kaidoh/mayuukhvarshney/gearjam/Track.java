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



    @SerializedName("user")
    private Users mUser;

    @SerializedName("id")
    private int mID;
 @SerializedName("waveform_url")
 private String mWaveformURL;

    public String getTitle() {
        return mTitle;
    }

public String getmWaveformURL(){
    return mWaveformURL;
}


    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }
    public int getID() {
        return mID;
    }


    public Users getUser(){return mUser;}

}

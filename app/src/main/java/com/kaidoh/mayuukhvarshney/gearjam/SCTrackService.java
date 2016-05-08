package com.kaidoh.mayuukhvarshney.gearjam;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by mayuukhvarshney on 14/03/16.
 */

public interface SCTrackService {
    @GET("/tracks/{id}?client_id=" + Config.CLIENT_ID)
    void getTrack(@Path("id") int ID, Callback<Track> cb);
}


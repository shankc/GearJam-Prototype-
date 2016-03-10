package com.kaidoh.mayuukhvarshney.gearjam;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;
/**
 * Created by mayuukhvarshney on 07/03/16.
 */
public interface SCService {
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    void getRecentTracks(@QueryMap Map<String, String> x,@QueryMap Map<String,Integer> y, Callback<List<Track>> cb);
}

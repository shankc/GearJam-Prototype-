package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 07/03/16.
 */

import retrofit.RestAdapter;

/**
 * Created by mayuukhvarshney on 30/01/16.
 */
public class SoundCloud {
    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder().setEndpoint(Config.API_URL).build();
    private static final SCService SERVICE = REST_ADAPTER.create(SCService.class);
    private static final SCTrackService TRACK_SERVICE=REST_ADAPTER.create(SCTrackService.class);

    public static SCService getService() {
        return SERVICE;
    }
    public static SCTrackService getTrackService(){return TRACK_SERVICE;}
}
package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Intent;
import android.view.View;
import android.app.Application;
/**
 * Created by mayuukhvarshney on 01/04/16.
 */

// this class exisits for communication between two activities.!! mainly the displayactivity class and playlist class!!

public class AppControl extends Application {
    private static AppControl mInstance;
    Playlist play;
    DisplayTrackActivity Display;
    public static synchronized AppControl getInstance() {
        return mInstance;
    }


    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), PlayListService.class));
    }
}
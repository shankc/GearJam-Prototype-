package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat.Builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by mayuukhvarshney on 10/03/16.
 */
public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
    String ID,Title;
    Context mContext;
    File Path;
    int id=1;
    protected NotificationManager mNotifyManager;
    protected Builder mBuilder;
    public DownloadFileFromURL(Context context,String trackID,String TL,File folder) // need to pass userID also along with this later..
    {
        ID=trackID;
        Title=TL;
        mContext=context;
        Path=folder;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.start_download);
        mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());

    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        // Update progress
        mBuilder.setProgress(100, values[0], false);
        mNotifyManager.notify(id, mBuilder.build());
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... f_url) {
        URL u = null;
        InputStream is = null;

        try {

            u = new URL(f_url[0]);
            is = u.openStream();
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();//to know the size of video
            int size = huc.getContentLength();

            if (huc != null) {
                String fileName = Title+"<"+ID+">"+".mp3";
                Log.d("whaterver",fileName);
                String storagePath = Environment.getExternalStorageDirectory()+File.separator+"GearJam";

                File f = new File(storagePath, fileName);

                FileOutputStream fos =  mContext.openFileOutput(fileName,mContext.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                long total = 0;
                int len1 = 0;
                if (is != null) {
                    while ((len1 = is.read(buffer)) > 0) {
                        total += len1;
                        publishProgress((int) ((total * 100) / size));
                        fos.write(buffer, 0, len1);
                    }
                }
                if (fos != null) {
                    fos.close();
                }
                for (int i = 0; i <= 100; i += 5) {
                    // Sets the progress indicator completion percentage
                    publishProgress(Math.min(i, 100));
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        Log.d("TAG", "sleep failure");
                    }
                }

            }


        }

        catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if(is != null){
                    is.close();
                }
            }catch (Exception ioe) {
              ioe.printStackTrace();
            }
        }
        return "";
    }


    @Override
    protected void onPostExecute(String file_url) {

        mBuilder.setContentText("Download complete,added to PlayList");
        mBuilder.setSmallIcon(R.drawable.download_done);
        mBuilder.setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
        Toast.makeText(mContext,"Added To Playlist",Toast.LENGTH_SHORT).show();
        Log.d("Download","Done");


    }


}


package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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

                FileOutputStream fos = new FileOutputStream(f);
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
            }catch (IOException ioe) {
                // just going to ignore this one
            }
        }
        return "";
    }


    @Override
    protected void onPostExecute(String file_url) {

        Toast.makeText(mContext,"Added To Playlist",Toast.LENGTH_SHORT).show();
        Log.d("Download","Done");

    }


}


package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity{
    protected  GridView photoGrid;
    protected int mPhotoSize, mPhotoSpacing;
    protected ImageAdapter imageAdapter;
protected NotificationManager manager;
   public static final int[] ICONS={R.drawable.jimmy_page,R.drawable.stirling,

            R.drawable.hardwell,R.drawable.piano,R.drawable.ravi_shankar,R.drawable.charlie_parker,R.drawable.symphony,R.drawable.flute_player};

    public static final String[] CONTENT={"Guitar","Violin","Electronic","Piano","Sitar","Saxophone","Symphony","Flute"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected)
        {
            Toast.makeText(this,"Unable to Connect :(",Toast.LENGTH_SHORT).show();
        }


// get the photo size and spacing
        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);

// initialize image adapter

        imageAdapter = new ImageAdapter(this,ICONS,CONTENT);
        //imageAdapter.SetResources(ICONS,CONTENT);



        photoGrid = (GridView) findViewById(R.id.grid_view1);

// set image adapter to the GridView
        photoGrid.setAdapter(imageAdapter);
        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this,"item clikced " +position,Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, GuitarGenre.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);


                } else if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, ViolinGenre.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);

                } else if (position == 2) {
                    Intent intent = new Intent(MainActivity.this,ElectronicGenre.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                else if(position==3){
                    Intent intent = new Intent(MainActivity.this,PianoGenre.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                else if(position==4){
                    Intent intent = new Intent(MainActivity.this,SitarGenre.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                else if(position==6){

                }
                finish();
            }
        });

//get the view tree observer of the grid and set the height and numcols dynamically
        photoGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imageAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(photoGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (photoGrid.getWidth() / numColumns);
                        imageAdapter.setNumColumns(numColumns);
                        imageAdapter.setItemHeight(columnWidth);

                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;

        }
        else if(id==R.id.playlist_item){
            Intent intent= new Intent(this,Playlist.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("ElectronicGenere", "has come to onResume State");
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }


}
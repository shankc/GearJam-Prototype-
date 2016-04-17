package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 15/03/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
public class SitarGenre extends MainActivity {

    private int[] ICONS={R.drawable.anushka_world,R.drawable.electronic_sitar,R.drawable.harrison_rock_2,R.drawable.nishat_instrumental,R.drawable.fusion_sitar,R.drawable.ravishankar_sitar,R.drawable.sitar_experi,R.drawable.ambient_sitar};
    private String[] CONTENT={"World","Electronic","Rock","Instrumental","Fusion","Pyschadelic","Experimental","Ambient"};
    private ImageAdapter SitarImageAdapter;
    private GridView SitarGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        SitarImageAdapter= new ImageAdapter(this,ICONS,CONTENT);
        SitarGrid= (GridView) findViewById(R.id.grid_view1);
        SitarGrid.setAdapter(SitarImageAdapter);

        SitarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(SitarGenre.this,DisplayTrackActivity.class);
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "World");

                    startActivity(intent);
                } else if (position == 1) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "Electronic");

                    startActivity(intent);
                } else if (position == 2) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "Rock");

                    startActivity(intent);
                } else if (position == 3) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "Instrumental");

                    startActivity(intent);
                } else if (position == 4) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "Fusion");

                    startActivity(intent);
                } else if (position == 5) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "World music");

                    startActivity(intent);
                } else if (position == 6) {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "Experimental");
                    startActivity(intent);
                } else {
                    intent.putExtra("Instrument", "Sitar");
                    intent.putExtra("Genre", "Ambient");
                    startActivity(intent);
                }
            }
        });
        SitarGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (SitarImageAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(SitarGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (SitarGrid.getWidth() / numColumns);
                        SitarImageAdapter.setNumColumns(numColumns);
                        SitarImageAdapter.setItemHeight(columnWidth);

                    }
                }
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("ElectronicGenere", "has come to onResume State");
    }
    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}

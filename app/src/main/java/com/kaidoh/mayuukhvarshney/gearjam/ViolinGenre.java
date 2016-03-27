package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by mayuukhvarshney on 13/03/16.
 */
public class ViolinGenre extends MainActivity {
    private static int[] ICONS={R.drawable.violin_stirling_dubstep,R.drawable.violin_classical,R.drawable.violin_orchestral,R.drawable.folk_violin,R.drawable.contemp_classical,R.drawable.violin_jazz,R.drawable.violin_instrumental,R.drawable.violin_soundtrack};
    private static String[] CONTENT={"Dubstep","Classical","Orchestral","Folk","Contemporary","Jazz","Instrumental","Soundtrack"};
    private ImageAdapter ViolinImageAdapter;
    private  GridView ViolinGrid;
    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ViolinImageAdapter=new ImageAdapter(this,ICONS,CONTENT);
        ViolinGrid= (GridView) findViewById(R.id.grid_view1);
        ViolinGrid.setAdapter(ViolinImageAdapter);
   ViolinGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           Intent intent = new Intent(ViolinGenre.this, DisplayTrackActivity.class);
           if (position == 0) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "dubstep");
               startActivity(intent);

           } else if (position == 1) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "classical");
               startActivity(intent);
           } else if (position == 2) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "orchestral");
               startActivity(intent);

           } else if (position == 3) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "folk");
               startActivity(intent);
           } else if (position == 4) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "contemporary classical");
               startActivity(intent);
           } else if (position == 5) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "jazz");
               startActivity(intent);
           } else if (position == 6) {
               intent.putExtra("Instrument", "violin");
               intent.putExtra("Genre", "instrumental");
               startActivity(intent);

           } else {
               intent.putExtra("Instrument","violin");
               intent.putExtra("Genre","soundtrack");
               startActivity(intent);
           }
       }
   });
        ViolinGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (ViolinImageAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(ViolinGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (ViolinGrid.getWidth() / numColumns);
                        ViolinImageAdapter.setNumColumns(numColumns);
                        ViolinImageAdapter.setItemHeight(columnWidth);

                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}

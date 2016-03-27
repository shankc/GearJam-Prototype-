package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by mayuukhvarshney on 05/03/16.
 */
public class GuitarGenre  extends MainActivity{
private static int[] ICONS={R.drawable.slash_rock,R.drawable.acoustic,R.drawable.geroge_benson_jazz,R.drawable.madonna_pop,R.drawable.dimebag_metal,R.drawable.clapton_instrumental,R.drawable.bb_king,R.drawable.folk_guitar};
    private static String[] CONTENT={"Rock","Acoustic","Jazz","Pop","Metal","Instrumental","Blues","Folk"};
    private ImageAdapter GuitarimageAdapter;
    private GridView GuitarGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);



// get the photo size and spacing
        //mPhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        //mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);

// initialize image adapter

        GuitarimageAdapter = new ImageAdapter(this,ICONS,CONTENT);
        GuitarGrid= (GridView) findViewById(R.id.grid_view1);

// set image adapter to the GridView
        GuitarGrid.setAdapter(GuitarimageAdapter);
        GuitarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GuitarGenre.this, DisplayTrackActivity.class);
                if (position == 0) {

                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "rock");
                    startActivity(intent);
                } else if (position == 1) {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "acoustic");
                    startActivity(intent);
                } else if (position == 2) {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "jazz");
                    startActivity(intent);
                } else if (position == 3) {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "pop");
                    startActivity(intent);
                } else if (position == 4) {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "metal");
                    startActivity(intent);
                } else if (position == 5) {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "instrumental");
                    startActivity(intent);
                } else if (position == 6) {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre", "blues");
                    startActivity(intent);
                } else {
                    intent.putExtra("Instrument", "guitar");
                    intent.putExtra("Genre","folk");
                    startActivity(intent);
                }
            }
        });
//get the view tree observer of the grid and set the height and numcols dynamically
        GuitarGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (GuitarimageAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(GuitarGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (GuitarGrid.getWidth() / numColumns);
                        GuitarimageAdapter.setNumColumns(numColumns);
                        GuitarimageAdapter.setItemHeight(columnWidth);

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

package com.kaidoh.mayuukhvarshney.gearjam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
public class MainActivity extends Activity {
    protected  GridView photoGrid;
    protected int mPhotoSize, mPhotoSpacing;
    protected ImageAdapter imageAdapter;

   public static final int[] ICONS={R.drawable.jimmy_page,R.drawable.stirling,

            R.drawable.hardwell,R.drawable.symphony,R.drawable.ravi_shankar,R.drawable.charlie_parker,R.drawable.piano,R.drawable.flute_player};

    public static final String[] CONTENT={"Guitar","Violin","Electronic","Symphony","Sitar","Saxophone","Piano","Flute"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
                    Intent intent = new Intent(MainActivity.this,GuitarGenre.class);
                    startActivity(intent);

                }
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


}
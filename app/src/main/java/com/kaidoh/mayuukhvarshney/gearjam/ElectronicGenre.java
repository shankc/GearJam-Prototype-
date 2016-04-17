package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 13/03/16.
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
public class ElectronicGenre extends MainActivity {
    private int[] ICONS={R.drawable.armin_trance,R.drawable.edm,R.drawable.electronic_skrillex,R.drawable.guetta_dance,R.drawable.house,R.drawable.progressive_house,R.drawable.progressive_trance,R.drawable.psytrance};
    private String[] CONTENT={"Trance","EDM","Electronic","Dance","House","Progressive House","Progressive Trance","PsyTrance"};
    private ImageAdapter ElectronicImageAdapter;
    private GridView ElectronicGrid;
@Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    ElectronicImageAdapter=new ImageAdapter(this,ICONS,CONTENT);
    ElectronicGrid = (GridView) findViewById(R.id.grid_view1);
    ElectronicGrid.setAdapter(ElectronicImageAdapter);
    ElectronicGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        Intent intent = new Intent(ElectronicGenre.this, DisplayTrackActivity.class);

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position == 0) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Trance");

                startActivity(intent);
            } else if (position == 1) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Edm");
                //intent.addFlags()

                startActivity(intent);
            } else if (position == 2) {    // since URL format may replace the space with underscore. applies to all genres
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Electronic");

                startActivity(intent);
            } else if (position == 3) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Dance");

                startActivity(intent);
            } else if (position == 4) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "House");

                startActivity(intent);
            } else if (position == 5) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Progressive House");

                startActivity(intent);

            } else if (position == 6) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Progressive trance");

                startActivity(intent);
            } else if (position == 7) {
                intent.putExtra("Instrument", "Trance");
                intent.putExtra("Genre", "Psytrance");

                startActivity(intent);
            }
        }
    });
    ElectronicGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (ElectronicImageAdapter.getNumColumns() == 0) {
                final int numColumns = (int) Math.floor(ElectronicGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                if (numColumns > 0) {
                    final int columnWidth = (ElectronicGrid.getWidth() / numColumns);
                    ElectronicImageAdapter.setNumColumns(numColumns);
                    ElectronicImageAdapter.setItemHeight(columnWidth);

                }
            }
        }
    });

}
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("ElectronicGenere","has come to onResume State");
    }
    @Override
    public void onBackPressed(){
       // super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        Log.d("ElecornicGenre","Going to MainActivity");
        startActivity(intent);
    }
}

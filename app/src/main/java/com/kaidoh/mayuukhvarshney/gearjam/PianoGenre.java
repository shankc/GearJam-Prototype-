package com.kaidoh.mayuukhvarshney.gearjam;

/**
 * Created by mayuukhvarshney on 04/04/16.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
public class PianoGenre extends MainActivity {
    private int[] ICONS={R.drawable.chopin_classical,R.drawable.herbie_jazz,R.drawable.amy_instrumental,R.drawable.rick_pop,R.drawable.soundtrack_other,R.drawable.hiphop,R.drawable.rudness_electronic,R.drawable.rach_piano};
    private String[] CONTENT={"Classical","Jazz","Instrumental","Pop","SoundTrack","HipHop","Electronic","Contemporary"};
    private ImageAdapter PianoAdapter;
    private GridView  PianoGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        PianoAdapter=new ImageAdapter(this,ICONS,CONTENT);
        PianoGrid= (GridView) findViewById(R.id.grid_view1);
        PianoGrid.setAdapter(PianoAdapter);
        PianoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(PianoGenre.this, DisplayTrackActivity.class);

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Classical");

                    startActivity(intent);
                } else if (position == 1) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Jazz");

                    startActivity(intent);
                } else if (position == 2) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Instrumental");

                    startActivity(intent);
                } else if (position == 3) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Pop");

                    startActivity(intent);
                } else if (position == 4) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Soundtrack");

                    startActivity(intent);
                } else if (position == 5) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Hip hop");

                    startActivity(intent);
                } else if (position == 6) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Electronic");

                    startActivity(intent);
                } else if (position == 7) {
                    intent.putExtra("Instrument", "Piano");
                    intent.putExtra("Genre", "Piano");

                    startActivity(intent);
                }
                //finish();

            }
        });
        PianoGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (PianoAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(PianoGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (PianoGrid.getWidth() / numColumns);
                        PianoAdapter.setNumColumns(numColumns);
                        PianoAdapter.setItemHeight(columnWidth);

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

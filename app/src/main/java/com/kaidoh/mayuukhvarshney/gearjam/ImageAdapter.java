package com.kaidoh.mayuukhvarshney.gearjam;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int mItemHeight = 0;
    private int mNumColumns = 0;
    private RelativeLayout.LayoutParams mImageViewLayoutParams;
    private int Len;
    private int []ICONS=new int[8];
    private String[] CONTENT=new String[8];
    public ImageAdapter(Context context,int [] img,String[] txt) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,mItemHeight);
        for(int i=0;i<img.length;i++)
        {
            ICONS[i]=img[i];
            CONTENT[i]=txt[i];
        }

    }


    public int getCount() {
        return CONTENT.length;
    }

    // set numcols
    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    // set photo item height
    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null)
            view = mInflater.inflate(R.layout.menu_items, null);

        ImageView cover = (ImageView) view.findViewById(R.id.cover);
        TextView title = (TextView) view.findViewById(R.id.title);

        cover.setLayoutParams(mImageViewLayoutParams);

// Check the height matches our calculated column width
        if (cover.getLayoutParams().height != mItemHeight) {
            cover.setLayoutParams(mImageViewLayoutParams);
        }
        cover.setImageResource(ICONS[position % ICONS.length]);
        title.setText(CONTENT[position % CONTENT.length]);

        return view;
    }
}
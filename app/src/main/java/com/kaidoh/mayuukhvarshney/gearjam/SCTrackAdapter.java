package com.kaidoh.mayuukhvarshney.gearjam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by mayuukhvarshney on 07/03/16.
 */

    public class SCTrackAdapter extends BaseAdapter {

        private Context mContext;
        private List<Track> mTracks;

        public SCTrackAdapter(Context context, List<Track> tracks) {
            mContext = context;
            mTracks = tracks;
        }

        @Override
        public int getCount() {
            return mTracks.size();
        }

        @Override
        public Track getItem(int position) {
            return mTracks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

             String str;
            Track track = getItem(position);

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.track_list_row, parent, false);
                holder = new ViewHolder();
                holder.trackImageView = (ImageView) convertView.findViewById(R.id.track_image);
                holder.titleTextView = (TextView) convertView.findViewById(R.id.track_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            str=track.getTitle().toLowerCase();
             if(track.getTitle().length()>=10 )//|| str!="folk" || str!="instrumental" || str!="violin" || str!="saxophone"|| str!="sitar"|| str!="flute")
            {
            holder.titleTextView.setText(track.getTitle());
               // holder.artistTextView.setText(track.getUser().getUsername());
            //  holder.genreTextView.setText(track.getTrackType());
            }
            //holder.titleTextView.setText(track.getmTrackType());
            //holder.genreTextView.setText(tra.getmTrackType());

            // Trigger the download of the URL asynchronously into the image view.
            if(track.getArtworkURL()!=null){
            Picasso.with(mContext).load(track.getArtworkURL()).into(holder.trackImageView);
            }
            else {
                //Picasso.with(mContext).load(track.getmWaveformURL()).into(holder.trackImageView);
                holder.trackImageView.setImageResource(R.drawable.default_pic);
            }



            return convertView;
        }

        static class ViewHolder {
            ImageView trackImageView;
            TextView titleTextView;
            TextView genreTextView;
            TextView artistTextView;
        }

    }

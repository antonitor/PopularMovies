package com.tonietorres.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tonietorres.popularmovies.model.Video;

import java.util.ArrayList;

/**
 * Created by Toni on 21/02/2018.
 */

public class VideoListAdapter extends ArrayAdapter<Video> {
    public VideoListAdapter(Context context, ArrayList<Video> videos) {
        super(context, 0, videos);
    }
  
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Video video = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.video_name)).setText(video.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri;
                if (getItem(position).getSite().equals(getContext().getString(R.string.youtube_site))) {
                    uri = Uri.parse(getContext().getString(R.string.youtube_video) + getItem(position).getKey());
                } else {
                    uri = Uri.parse(getItem(position).getKey());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}

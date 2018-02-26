package com.tonietorres.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tonietorres.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * Created by Toni on 21/02/2018.
 */

public class ReviewListAdapter  extends ArrayAdapter<Review> {
    public ReviewListAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review review = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.review_author)).setText(review.getAuthor());
        ((TextView) convertView.findViewById(R.id.review_content)).setText(review.getContent());
        ((TextView) convertView.findViewById(R.id.review_url)).setText(review.getUrl());

        return convertView;
    }
}

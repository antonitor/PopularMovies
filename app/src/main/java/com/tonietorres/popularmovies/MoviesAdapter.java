package com.tonietorres.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tonietorres.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by Toni on 16/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{

    private List<Movie> mMoviesDataSet;
    private final Context mContext;
    final private MovieClickListener mMovieClickListener;

    public MoviesAdapter(Context mContext, MovieClickListener clickListener ) {
        this.mContext = mContext;
        this.mMovieClickListener = clickListener;
    }

    public interface MovieClickListener{
        void onMovieClick(String id);
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movie_poster, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mMoviesDataSet.get(position).getPoster_path())
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (mMoviesDataSet!=null) {
            return mMoviesDataSet.size();
        } else {
            return 0;
        }
    }

    void swapDataSet(List<Movie> newDataSet) {
        mMoviesDataSet = newDataSet;
        notifyDataSetChanged();
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView poster;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            this.poster = (ImageView) itemView.findViewById(R.id.poster_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String id = mMoviesDataSet.get(getAdapterPosition()).getId();
            mMovieClickListener.onMovieClick(id);
        }
    }
}

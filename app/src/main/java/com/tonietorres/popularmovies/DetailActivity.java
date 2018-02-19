package com.tonietorres.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonietorres.popularmovies.model.Movie;
import com.tonietorres.popularmovies.utils.JsonUtils;
import com.tonietorres.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    private final int MOVIE_DETAIL_LOADER_ID = 2;
    private ImageView mPosterView;
    private TextView mOriginalTitleTV;
    private TextView mReleaseDateTV;
    private TextView mRatingTV;
    private TextView mOverviewTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mPosterView = findViewById(R.id.poster_detail);
        mReleaseDateTV = findViewById(R.id.release_date);
        mRatingTV = findViewById(R.id.rating);
        mOverviewTV = findViewById(R.id.overview);
        mOriginalTitleTV = findViewById(R.id.original_title);

        Bundle bundle = new Bundle();
        bundle.putString(Intent.EXTRA_INDEX, getIntent().getStringExtra(Intent.EXTRA_INDEX));
        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, bundle, this);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Movie loadInBackground() {
                String json = NetworkUtils.fetchMovieById(args.getString(Intent.EXTRA_INDEX));
                return JsonUtils.getMovie(json);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        this.setTitle(data.getTitle());
        Picasso.with(this)
                .load(data.getPoster_path())
                .into(mPosterView);
        mOriginalTitleTV.setText(data.getOriginal_title());
        mReleaseDateTV.setText(data.getRelease_date());
        mRatingTV.setText(data.getVote_average());
        mOverviewTV.setText(data.getOverview());
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }
}

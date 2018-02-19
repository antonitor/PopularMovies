package com.tonietorres.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonietorres.popularmovies.model.Movie;
import com.tonietorres.popularmovies.utils.JsonUtils;
import com.tonietorres.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    private static final int MOVIE_DETAIL_LOADER_ID = 2;
    private ImageView mPosterView;
    private TextView mReleaseDateTV;
    private TextView mRatingTV;
    private TextView mOverviewTV;
    private TextView mTitleTV;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mErrorMessage = findViewById(R.id.detail_error_message);
        mPosterView = findViewById(R.id.poster_detail);
        mReleaseDateTV = findViewById(R.id.release_date);
        mRatingTV = findViewById(R.id.rating);
        mOverviewTV = findViewById(R.id.overview);
        mTitleTV = findViewById(R.id.title_tv);
        mProgressBar = findViewById(R.id.pb_detail);
        mFrameLayout = findViewById(R.id.frameLayout);

        if (NetworkUtils.isOnline(this)) {
            Bundle bundle = new Bundle();
            bundle.putString(Intent.EXTRA_INDEX, getIntent().getStringExtra(Intent.EXTRA_INDEX));
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, bundle, this);
        } else {
            showErrorMessage(getString(R.string.no_network));
        }
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {

            Movie mMovie;

            @Override
            protected void onStartLoading() {
                if (mMovie == null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                } else {
                    deliverResult(mMovie);
                }
            }

            @Override
            public Movie loadInBackground() {
                String json = NetworkUtils.fetchMovieById(args.getString(Intent.EXTRA_INDEX));
                return JsonUtils.getMovie(json);
            }

            @Override
            public void deliverResult(Movie data) {
                mMovie = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (data == null) {
            showErrorMessage(getString(R.string.error_message));
        } else {
            Picasso.with(this)
                    .load(data.getPoster_path())
                    .into(mPosterView);
            mReleaseDateTV.setText(data.getRelease_date());
            mRatingTV.setText(data.getVote_average());
            mRatingTV.append(getString(R.string.out_of_ten));
            mOverviewTV.setText(data.getOverview());
            mTitleTV.setText(data.getOriginal_title());
            showMovieDetails();
        }
    }

    private void showErrorMessage(String message) {
        mPosterView.setVisibility(View.INVISIBLE);
        mReleaseDateTV.setVisibility(View.INVISIBLE);
        mRatingTV.setVisibility(View.INVISIBLE);
        mOverviewTV.setVisibility(View.INVISIBLE);
        mTitleTV.setVisibility(View.INVISIBLE);
        mFrameLayout.setVisibility(View.INVISIBLE);
        mErrorMessage.setText(message);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showMovieDetails() {
        mPosterView.setVisibility(View.VISIBLE);
        mReleaseDateTV.setVisibility(View.VISIBLE);
        mRatingTV.setVisibility(View.VISIBLE);
        mOverviewTV.setVisibility(View.VISIBLE);
        mTitleTV.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }
}

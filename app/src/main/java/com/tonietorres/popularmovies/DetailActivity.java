package com.tonietorres.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonietorres.popularmovies.data.MoviesContract;
import com.tonietorres.popularmovies.data.MoviesContract.MovieEntry;
import com.tonietorres.popularmovies.model.Movie;
import com.tonietorres.popularmovies.model.Review;
import com.tonietorres.popularmovies.model.Video;
import com.tonietorres.popularmovies.utils.JsonUtils;
import com.tonietorres.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    private Movie mMovie;
    private boolean mFavoriteFlag = false;

    private static final int MOVIE_DETAIL_LOADER_ID = 2;
    private ImageView mPosterView;
    private TextView mReleaseDateTV;
    private TextView mRatingTV;
    private ImageButton mFavoriteButton;
    private TextView mOverviewTV;
    private TextView mTitleTV;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private ListView mVideoList;
    private ListView mReviewList;
    private TextView mVideoLabel;
    private TextView mReviewLabel;
    private View separator1;
    private View separator2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(getString(R.string.detail_activity_saved_instance_state_movie));
            mFavoriteFlag = savedInstanceState.getBoolean(getString(R.string.detail_activity_saved_instance_state_fav));
        }

        mErrorMessage = findViewById(R.id.detail_error_message);
        mPosterView = findViewById(R.id.poster_detail);
        mReleaseDateTV = findViewById(R.id.release_date);
        mRatingTV = findViewById(R.id.rating);
        mOverviewTV = findViewById(R.id.overview);
        mTitleTV = findViewById(R.id.title_tv);
        mProgressBar = findViewById(R.id.pb_detail);
        mVideoList = findViewById(R.id.video_list);
        mReviewList = findViewById(R.id.review_list);
        mVideoLabel = findViewById(R.id.videos_label);
        mReviewLabel = findViewById(R.id.reviews_label);
        separator1 = findViewById(R.id.separator_videos);
        separator2 = findViewById(R.id.separator_reviews);
        mFavoriteButton = findViewById(R.id.fav_button);

        if (NetworkUtils.isOnline(this)) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.extra_movie_id), getIntent().getStringExtra(getString(R.string.extra_movie_id)));
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, bundle, this);
        } else {
            showErrorMessage(getString(R.string.no_network));
        }
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {

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
                //Get movie
                String jsonMovie = NetworkUtils.fetchMovieById(args.getString(getString(R.string.extra_movie_id)));
                Movie movie = JsonUtils.getMovie(jsonMovie);
                //Get videos and add them to the movie object
                String jsonVideos = NetworkUtils.fetchVideosByMovieId(movie.getId());
                List<Video> videos = JsonUtils.getVideoList(jsonVideos);
                movie.setVideos(videos);
                //Get reviews and add them to the movie object
                String jsonReviews = NetworkUtils.fetchReviewsByMovieId(movie.getId());
                List<Review> reviews = JsonUtils.getReviewList(jsonReviews);
                movie.setReviews(reviews);

                return movie;
            }

            @Override
            public void deliverResult(Movie data) {
                mMovie = data;
                setFavoriteFlag();
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

            VideoListAdapter videoAdapter = new VideoListAdapter(this, (ArrayList) data.getVideos());
            mVideoList.setAdapter(videoAdapter);

            ReviewListAdapter reviewAdapter = new ReviewListAdapter(this, (ArrayList) data.getReviews());
            mReviewList.setAdapter(reviewAdapter);

            setFavoriteFlag();
        }
    }

    private void addFavorite() {
        ContentResolver resolver = getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        cv.put(MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        cv.put(MovieEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path());
        resolver.insert(MovieEntry.CONTENT_URI, cv);
    }

    private void removeFavorite() {
        ContentResolver resolver = getContentResolver();
        int rowsDeleted = resolver.delete(MoviesContract.buildMoviesUriWithId(mMovie.getId()), null, null);
    }

    public void onFavoriteButtonPressed(View view) {
        if (!mFavoriteFlag) {
            mFavoriteFlag = true;
            addFavorite();
            setFavoriteButtonImage();
        } else {
            mFavoriteFlag = false;
            removeFavorite();
            setFavoriteButtonImage();
        }
        setResult(RESULT_OK,getIntent());
    }

    private void setFavoriteFlag() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MoviesContract.buildMoviesUriWithId(mMovie.getId()), null, null, null, null);
        if (cursor!=null && cursor.moveToNext()){
            mFavoriteFlag = true;
            setFavoriteButtonImage();
        }
    }

    private void setFavoriteButtonImage(){
        if (mFavoriteFlag) {
            mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
        } else {
            mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_fav));
        }
    }

    private void showErrorMessage(String message) {
        mPosterView.setVisibility(View.INVISIBLE);
        mReleaseDateTV.setVisibility(View.INVISIBLE);
        mRatingTV.setVisibility(View.INVISIBLE);
        mOverviewTV.setVisibility(View.INVISIBLE);
        mTitleTV.setVisibility(View.INVISIBLE);
        mFavoriteButton.setVisibility(View.INVISIBLE);
        mErrorMessage.setText(message);
        mErrorMessage.setVisibility(View.VISIBLE);
        mVideoLabel.setVisibility(View.INVISIBLE);
        mReviewLabel.setVisibility(View.INVISIBLE);
        separator1.setVisibility(View.INVISIBLE);
        separator2.setVisibility(View.INVISIBLE);

    }

    private void showMovieDetails() {
        mPosterView.setVisibility(View.VISIBLE);
        mReleaseDateTV.setVisibility(View.VISIBLE);
        mRatingTV.setVisibility(View.VISIBLE);
        mOverviewTV.setVisibility(View.VISIBLE);
        mTitleTV.setVisibility(View.VISIBLE);
        mVideoLabel.setVisibility(View.VISIBLE);
        mReviewLabel.setVisibility(View.VISIBLE);
        mFavoriteButton.setVisibility(View.VISIBLE);
        separator1.setVisibility(View.VISIBLE);
        separator2.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(getString(R.string.detail_activity_saved_instance_state_fav), mFavoriteFlag);
        outState.putParcelable(getString(R.string.detail_activity_saved_instance_state_movie), mMovie);
        super.onSaveInstanceState(outState);
    }
}

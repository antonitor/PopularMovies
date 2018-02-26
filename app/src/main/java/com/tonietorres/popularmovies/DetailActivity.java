package com.tonietorres.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.squareup.picasso.Picasso;
import com.tonietorres.popularmovies.data.MoviesContract;
import com.tonietorres.popularmovies.data.MoviesContract.MovieEntry;
import com.tonietorres.popularmovies.databinding.ActivityDetailBinding;
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
    private ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(getString(R.string.detail_activity_saved_instance_state_movie));
            mFavoriteFlag = savedInstanceState.getBoolean(getString(R.string.detail_activity_saved_instance_state_fav));
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

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
                    mBinding.pbDetail.setVisibility(View.VISIBLE);
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
        mBinding.pbDetail.setVisibility(View.INVISIBLE);
        if (data == null) {
            showErrorMessage(getString(R.string.error_message));
        } else {
            Picasso.with(this)
                    .load(data.getPoster_path())
                    .into(mBinding.posterDetail);
            mBinding.releaseDate.setText(data.getRelease_date());
            mBinding.rating.setText(data.getVote_average());
            mBinding.rating.append(getString(R.string.out_of_ten));
            mBinding.overview.setText(data.getOverview());
            mBinding.titleTv.setText(data.getOriginal_title());
            showMovieDetails();

            VideoListAdapter videoAdapter = new VideoListAdapter(this, (ArrayList) data.getVideos());
            mBinding.videoList.setAdapter(videoAdapter);

            ReviewListAdapter reviewAdapter = new ReviewListAdapter(this, (ArrayList) data.getReviews());
            mBinding.reviewList.setAdapter(reviewAdapter);

            setFavoriteFlag();
        }
    }

    //Adds this movie to the favorite content provider
    private void addFavorite() {
        ContentResolver resolver = getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        cv.put(MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        cv.put(MovieEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path());
        resolver.insert(MovieEntry.CONTENT_URI, cv);
    }

    //Removes this movie from the favorite content provider
    private void removeFavorite() {
        ContentResolver resolver = getContentResolver();
        int rowsDeleted = resolver.delete(MoviesContract.buildMoviesUriWithId(mMovie.getId()), null, null);
    }

    /*
        When favorite button is pressed, check if this movie is already set as favorite, then store
        or delete, and set the ImageButtn according to this.
    */
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

    /*
        This method is called whenever this Acrivity is created.
        Checks if this movie is stored as favorite, and sets the flag and the ImageButton according
        to this.
     */
    private void setFavoriteFlag() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MoviesContract.buildMoviesUriWithId(mMovie.getId()), null, null, null, null);
        if (cursor!=null && cursor.moveToNext()){
            mFavoriteFlag = true;
            setFavoriteButtonImage();
            cursor.close();
        }
    }

    /*
        Sets the drawable on the ImageButton according to the favorite flag state
     */
    private void setFavoriteButtonImage(){
        if (mFavoriteFlag) {
            mBinding.favButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
        } else {
            mBinding.favButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_fav));
        }
    }

    private void showErrorMessage(String message) {
        mBinding.posterDetail.setVisibility(View.INVISIBLE);
        mBinding.releaseDate.setVisibility(View.INVISIBLE);
        mBinding.rating.setVisibility(View.INVISIBLE);
        mBinding.overview.setVisibility(View.INVISIBLE);
        mBinding.titleTv.setVisibility(View.INVISIBLE);
        mBinding.favButton.setVisibility(View.INVISIBLE);
        mBinding.videosLabel.setVisibility(View.INVISIBLE);
        mBinding.reviewsLabel.setVisibility(View.INVISIBLE);
        mBinding.separatorReviews.setVisibility(View.INVISIBLE);
        mBinding.separatorVideos.setVisibility(View.INVISIBLE);

        mBinding.detailErrorMessage.setText(message);
        mBinding.detailErrorMessage.setVisibility(View.VISIBLE);

    }

    private void showMovieDetails() {
        mBinding.detailErrorMessage.setVisibility(View.INVISIBLE);

        mBinding.posterDetail.setVisibility(View.VISIBLE);
        mBinding.releaseDate.setVisibility(View.VISIBLE);
        mBinding.rating.setVisibility(View.VISIBLE);
        mBinding.overview.setVisibility(View.VISIBLE);
        mBinding.titleTv.setVisibility(View.VISIBLE);
        mBinding.favButton.setVisibility(View.VISIBLE);
        mBinding.videosLabel.setVisibility(View.VISIBLE);
        mBinding.reviewsLabel.setVisibility(View.VISIBLE);
        mBinding.separatorReviews.setVisibility(View.VISIBLE);
        mBinding.separatorVideos.setVisibility(View.VISIBLE);
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

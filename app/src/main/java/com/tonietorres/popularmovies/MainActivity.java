package com.tonietorres.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.tonietorres.popularmovies.model.Movie;
import com.tonietorres.popularmovies.utils.JsonUtils;
import com.tonietorres.popularmovies.utils.NetworkUtils;

import java.util.List;

public class
MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerview;
    private static final int MOVIES_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerview = (RecyclerView) findViewById(R.id.movies_recyclerview);
        mMoviesAdapter = new MoviesAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(mMoviesAdapter);

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int loaderId, Bundle bundle) {

        final List<Movie> mMovieList;

        if (loaderId == MOVIES_LOADER_ID) {
            return new AsyncTaskLoader<List<Movie>>(this) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public List<Movie> loadInBackground() {
                    String jsonMovieList = NetworkUtils.fetchMovies(NetworkUtils.MOST_POPULAR);
                    return JsonUtils.getMovieList(jsonMovieList);
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieList) {
        if (movieList != null) {
            mMoviesAdapter.swapDataSet(movieList);
        } else {
            Log.d("EPPPP", "posters null");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}

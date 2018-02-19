package com.tonietorres.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonietorres.popularmovies.model.Movie;
import com.tonietorres.popularmovies.utils.JsonUtils;
import com.tonietorres.popularmovies.utils.NetworkUtils;

import java.util.List;

public class
MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MoviesAdapter.MovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerview;
    private static final int MOVIES_LOADER_ID = 1;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessage = findViewById(R.id.tv_error_message);
        mRecyclerview = (RecyclerView) findViewById(R.id.movies_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        if (NetworkUtils.isOnline(this)) {
            mMoviesAdapter = new MoviesAdapter(this, this);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            mRecyclerview.setLayoutManager(layoutManager);
            mRecyclerview.setAdapter(mMoviesAdapter);
            mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
            getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        } else {
            showErrorMessage(getString(R.string.no_network));
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int loaderId, Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovieList = null;

            @Override
            protected void onStartLoading() {
                if (mMovieList == null) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                } else {
                    deliverResult(mMovieList);
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                String jsonMovieList = NetworkUtils.fetchMovies(getPreferredOrderCriteria());
                return JsonUtils.getMovieList(jsonMovieList);
            }

            @Override
            public void deliverResult(List<Movie> data) {
                mMovieList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieList != null) {
            mMoviesAdapter.swapDataSet(movieList);
            showMovies();
        } else {
            showErrorMessage(getString(R.string.error_while_loading));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    public void onMovieClick(String id) {
        Intent intent = new Intent(this, DetailActivity.class);
        Log.d("MOVIE ID: ", id);
        intent.putExtra(Intent.EXTRA_INDEX, id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    private String getPreferredOrderCriteria() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderCriteria = prefs.getString(getString(R.string.pref_criteria_key), getString(R.string.pref_popular_key));
        if (orderCriteria.equals(getString(R.string.pref_popular_key))) {
            return getString(R.string.order_criteria_popular);
        } else {
            return getString(R.string.order_criteria_top_rated);
        }
    }

    private void showMovies() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerview.setVisibility(View.VISIBLE);
    }


    private void showErrorMessage(String message) {
        mErrorMessage.setText(message);
        mRecyclerview.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }
}

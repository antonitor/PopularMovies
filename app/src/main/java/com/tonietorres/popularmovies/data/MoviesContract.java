package com.tonietorres.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Toni on 25/02/2018.
 */

public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.tonietorres.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAV_MOVIES = "fav_movies";


    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAV_MOVIES)
                .build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster";
    }

    public static Uri buildMoviesUriWithId(String id) {
        return MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(id)
                .build();
    }
}

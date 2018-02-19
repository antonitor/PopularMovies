package com.tonietorres.popularmovies.utils;

import com.tonietorres.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toni on 16/02/2018.
 */

public class JsonUtils {

    private final static String RESULTS = "results";
    private final static String ID = "id";
    private final static String TITLE = "title";
    private final static String ORIGINAL_TITLE = "original_title";
    private final static String POSTER_PATH = "poster_path";
    private final static String OVERVIEW = "overview";
    private final static String RATING = "vote_average";
    private final static String RELEASE_DATE = "release_date";

    public static List<Movie> getMovieList(String json) {
        if (json == null)
            return null;

        List<Movie> moviesList = new ArrayList<>();
        try {
            JSONObject jsonMovies = new JSONObject(json);

            if (jsonMovies.has(RESULTS)) {
                JSONArray results = jsonMovies.getJSONArray(RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonMovie = new JSONObject(results.optString(i));
                    String id = jsonMovie.optString(ID);
                    String title = jsonMovie.optString(TITLE);
                    String original_title = jsonMovie.optString(ORIGINAL_TITLE);
                    String poster_path = NetworkUtils.getPosterFullPath(jsonMovie.optString(POSTER_PATH));
                    String overview = jsonMovie.optString(OVERVIEW);
                    String vote_average = jsonMovie.optString(RATING);
                    String release_date = jsonMovie.optString(RELEASE_DATE);
                    moviesList.add(new Movie(id, title, original_title, poster_path, overview, vote_average, release_date));
                }

            } else {
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return moviesList;
    }

    public static Movie getMovie(String json) {
        try {
            JSONObject jsonMovie = new JSONObject(json);
            if (jsonMovie.has(ID)) {
                String id = jsonMovie.optString(ID);
                String title = jsonMovie.optString(TITLE);
                String original_title = jsonMovie.optString(ORIGINAL_TITLE);
                String poster_path = NetworkUtils.getPosterFullPath(jsonMovie.optString(POSTER_PATH));
                String overview = jsonMovie.optString(OVERVIEW);
                String vote_average = jsonMovie.optString(RATING);
                String release_date = jsonMovie.optString(RELEASE_DATE);
                return new Movie(id, title, original_title, poster_path, overview, vote_average, release_date);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}

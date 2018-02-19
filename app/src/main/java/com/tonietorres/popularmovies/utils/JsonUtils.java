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

    public static List<Movie> getMovieList(String json) {
        List<Movie> moviesList = new ArrayList<>();
        try {
            JSONObject jsonMovies = new JSONObject(json);

            if (jsonMovies.has("results")) {
                JSONArray results = jsonMovies.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonMovie = new JSONObject(results.optString(i));
                    String id = jsonMovie.optString("id");
                    String title = jsonMovie.optString("title");
                    String original_title = jsonMovie.optString("original_title");
                    String poster_path = NetworkUtils.getPosterFullPath(jsonMovie.optString("poster_path"));
                    String overview = jsonMovie.optString("overview");
                    String vote_average = jsonMovie.optString("vote_average");
                    String release_date = jsonMovie.optString("release_date");
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
            if (jsonMovie.has("id")) {
                String id = jsonMovie.optString("id");
                String title = jsonMovie.optString("title");
                String original_title = jsonMovie.optString("original_title");
                String poster_path = NetworkUtils.getPosterFullPath(jsonMovie.optString("poster_path"));
                String overview = jsonMovie.optString("overview");
                String vote_average = jsonMovie.optString("vote_average");
                String release_date = jsonMovie.optString("release_date");
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

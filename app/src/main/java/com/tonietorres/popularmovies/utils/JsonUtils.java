package com.tonietorres.popularmovies.utils;

import com.tonietorres.popularmovies.model.Movie;
import com.tonietorres.popularmovies.model.Review;
import com.tonietorres.popularmovies.model.Video;

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
    private final static String VIDEO_NAME = "name";
    private final static String VIDEO_KEY = "key";
    private final static String VIDEO_SITE = "site";
    private final static String REVIEW_AUTHOR = "author";
    private final static String REVIEW_CONTENT = "content";
    private final static String REVIEW_URL = "url";




    private JsonUtils(){}

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

    public static List<Video> getVideoList(String json) {
        if (json == null)
            return null;

        List<Video> videoList = new ArrayList<>();
        try {
            JSONObject jsonVideos = new JSONObject(json);

            if (jsonVideos.has(RESULTS)) {
                JSONArray results = jsonVideos.getJSONArray(RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonVideo = new JSONObject(results.optString(i));
                    String id = jsonVideo.optString(ID);
                    String key = jsonVideo.optString(VIDEO_KEY);
                    String name = jsonVideo.optString(VIDEO_NAME);
                    String site = jsonVideo.optString(VIDEO_SITE);
                    videoList.add(new Video(id, key, name, site));
                }

            } else {
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return videoList;
    }

    public static List<Review> getReviewList(String json) {
        if (json == null)
            return null;

        List<Review> reviewList = new ArrayList<>();
        try {
            JSONObject jsonReviews = new JSONObject(json);

            if (jsonReviews.has(RESULTS)) {
                JSONArray results = jsonReviews.getJSONArray(RESULTS);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonReview = new JSONObject(results.optString(i));
                    String id = jsonReview.optString(ID);
                    String author = jsonReview.optString(REVIEW_AUTHOR);
                    String content = jsonReview.optString(REVIEW_CONTENT);
                    String url = jsonReview.optString(REVIEW_URL);
                    reviewList.add(new Review(id, author, content, url));
                }

            } else {
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return reviewList;
    }


}

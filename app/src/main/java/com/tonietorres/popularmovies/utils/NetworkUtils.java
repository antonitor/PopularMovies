package com.tonietorres.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.tonietorres.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Toni on 16/02/2018.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POSTER_BASE_URL =  "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";
    private static final String MOVIES_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String API_KEY_QUERY = "api_key";

    public static String fetchMovies(String criteria){
        String json = null;
        Uri buildUri = Uri.parse(MOVIES_URL).buildUpon()
                .appendPath(criteria)
                .appendQueryParameter(API_KEY_QUERY,API_KEY)
                .build();
        Log.d(TAG," - url: " + buildUri.toString());
        try {
            URL url = new URL(buildUri.toString());
            json = getResponseFromHttpUrl(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String fetchMovieById(String id){
        String json = null;
        Uri buildUri = Uri.parse(MOVIES_URL).buildUpon()
                .appendPath(id)
                .appendQueryParameter(API_KEY_QUERY,API_KEY)
                .build();
        Log.d(TAG," - url: " + buildUri.toString());
        try {
            URL url = new URL(buildUri.toString());
            json = getResponseFromHttpUrl(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getPosterFullPath(String relativePath) {
        return POSTER_BASE_URL + POSTER_SIZE + relativePath;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}

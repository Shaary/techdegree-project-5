package com.shaary.movienight.ui;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.shaary.movienight.R;
import com.shaary.movienight.databinding.ActivityMainBinding;
import com.shaary.movienight.model.MovieResults;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieResults mMovieResults;
    //data binding

    public static final String API_KEY = "f2388368dc53b8b5a5a298ec53148eed";
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String MOVIE_URL = "discover/mMovieResults/?api_key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);


        //TODO: bind these variables with the interface
        int primaryReleaseYear = 1990;
        int voteCount = 100;
        int voteAverage = 8;


        //so far sorts by popularity in descending rder
        String requestUrl = BASE_URL + MOVIE_URL + API_KEY +
                "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&primary_release_year="
                + primaryReleaseYear + "&vote_count.gte="
                + voteCount + "&vote_average.gte="
                + voteAverage;

        if (isNetworkAvailable()) {

            //https://developers.themoviedb.org/3/discover/movie-discover
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, "lol" + jsonData);
                        if (response.isSuccessful()) {

                            mMovieResults = getSearchResults(jsonData);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: lol", e);
                    } catch (JSONException je) {
                        Log.e(TAG, "JSON Exception caught: lol", je);
                    }
                }
            });
        }
    }

    private MovieResults getSearchResults(String jsonData) throws JSONException{
        JSONObject moviesList = new JSONObject(jsonData);

        String searchResults = moviesList.getString("results");
        Log.i(TAG, "From JSON: lol" + searchResults);
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        //TODO: make it a fragment
        else {
            Toast.makeText(this, R.string.network_unavailable_message,
                    Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }
}

package com.example.moviedatabasev2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Mulcahy on 31/03/2017.
 */

public class DetailedMovieActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private TextView tagline;
    private TextView description;
    private ImageView poster;
    private TextView genres;
    private TextView releaseDate;
    private TextView budget;
    private TextView revenue;
    private TextView numVotes;
    private RatingBar ratingBar;
    private LinearLayout progressBar;
    private int [] genreId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedmovie_layout);

        //assigning member variables
        toolbar = (Toolbar) findViewById(R.id.detailedmovie_toolbar);
        title = (TextView) findViewById(R.id.textViewTitle);
        tagline = (TextView) findViewById(R.id.textViewTagline);
        poster = (ImageView) findViewById(R.id.imageViewPoster);
        description = (TextView) findViewById(R.id.textViewDescription);
        genres = (TextView) findViewById(R.id.textViewGenres);
        releaseDate = (TextView) findViewById(R.id.textViewReleaseDate);
        budget = (TextView) findViewById(R.id.textViewBudget);
        revenue = (TextView) findViewById(R.id.textViewRevenue);
        numVotes = (TextView) findViewById(R.id.textViewNumVotes);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarDetailed);
        progressBar = (LinearLayout) findViewById(R.id.progressbar_det);

        //giving the toolbar a back button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        getSupportActionBar().setTitle(name);

        genreId = intent.getIntArrayExtra("genres");
        int id = intent.getIntExtra("id", 550);
        if (id != 0)
        {
            if (getInternetConnection())
                new JSONTask().execute("https://api.themoviedb.org/3/movie/" + id +"?api_key=9e295dfde4d031c0baf4813fbb3814a6&language=en-US");
            else
                getSupportActionBar().setTitle("No Internet Connection");
        }
    }

    private boolean getInternetConnection() {
        Context context = getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null)
            if (connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        else
            return false;
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class JSONTask extends AsyncTask<String, Void, DetailedMovie>
    {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }


        @Override
        protected DetailedMovie doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                if (url != null) {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();

                    InputStream stream = urlConnection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJSON = buffer.toString();

                    JSONObject jsonObject = new JSONObject(finalJSON);
                    if (jsonObject != null) {
                        String name = jsonObject.getString("title");
                        String description = jsonObject.getString("overview");
                        String releaseDate = jsonObject.getString("release_date");
                        String posterPath = jsonObject.getString("poster_path");
                        String language = jsonObject.getString("original_language");
                        double rating = jsonObject.getDouble("vote_average");
                        int id = jsonObject.getInt("id");
                        int numVotes = jsonObject.getInt("vote_count");
                        JSONArray genreJSONArray = jsonObject.getJSONArray("genres");
                        JSONObject [] jsonArrayObject = new JSONObject[genreJSONArray.length()];
                        String [] genres = new String[genreJSONArray.length()];
                        for (int i = 0; i < genreJSONArray.length(); i++)
                        {
                            jsonArrayObject[i] = genreJSONArray.getJSONObject(i);
                            genres[i] = jsonArrayObject[i].getString("name");
                        }
                        String tagline = jsonObject.getString("tagline");
                        String runtime = "" + jsonObject.getInt("runtime");
                        String budget = jsonObject.getString("budget");
                        String revenue = jsonObject.getString("revenue");
                        DetailedMovie movie = new DetailedMovie(name, description, releaseDate, posterPath, language, rating, numVotes, id, genreId, tagline, runtime, budget, revenue, genres);
                        return  movie;
                    }
                    return  null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(DetailedMovie movie) {
            super.onPostExecute(movie);
            progressBar.setVisibility(View.GONE);
            if (movie != null) {
                title.setText(movie.getName());
                tagline.setText("'" + movie.getTagline() + "'");
                description.setText("Description: " + movie.getDescription());
                String genreString = "Genres: ";
                String [] genreArrayString = movie.getGenreArray();
                for (int i = 0; i < genreArrayString.length; i++)
                {
                    genreString += genreArrayString[i] + ", ";
                }
                genreString = genreString.substring(0, genreString.length()-2);
                genres.setText(genreString);
                releaseDate.setText("Release Date: " + movie.getRealeaseDate());
                budget.setText("Budget: " + movie.getBuget());
                revenue.setText("Revenue: " + movie.getRevenue());
                numVotes.setText(movie.getNumVotes() + " people have voted");
                ratingBar.setNumStars(5);
                ratingBar.setRating((float) movie.getVoteAverage()/2);
                new ImageTask(poster).execute("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
            }
        }
    }
}

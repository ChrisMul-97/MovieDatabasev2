package com.example.moviedatabasev2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_GO;

/**
 * Created by Chris Mulcahy on 20/03/2017.
 */

public class SecondActivity extends AppCompatActivity {
    private ListView listView;
    private EditText editText;
    private LinearLayout progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.searchToolbar);
        progressBar = (LinearLayout) findViewById(R.id.progressbar_view);
        progressBar.setVisibility(View.INVISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_DONE) {
                    search();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void search()
    {
        String [] searchArray;
        searchArray = editText.getText().toString().split("\\s+");
        String searchFinal = "";
        for (int i =0;  i < searchArray.length; i++)
        {
            searchFinal += searchArray[i] + "+";
        }
        searchFinal = searchFinal.substring(0, searchFinal.length()-1);
        if (searchFinal.length() != 0)
        {
            searchFinal = searchFinal.substring(0, searchFinal.length()-1);
        }
        if (getInternetConnection())
            new JSONTask().execute("https://api.themoviedb.org/3/search/movie?&api_key=9e295dfde4d031c0baf4813fbb3814a6&page=1&query=" + searchFinal);
        else
            NoInternetConnectionList();

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

    private void NoInternetConnectionList()
    {

        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        List<Movie> failList = new ArrayList<Movie>();
        Movie errorMovie = new Movie();
        errorMovie.setName("No Internet Connection :(");
        errorMovie.setRealeaseDate("Coming soon?");
        errorMovie.setVoteAverage(0);
        failList.add(errorMovie);
        MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.movie_layout, failList);
        if (listView != null) {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
        }
        getSupportActionBar().setTitle("Offline");
    }

    private class JSONTask extends AsyncTask<String, String, List<Movie>> {

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        @Override
        protected List<Movie> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();

                JSONObject jsonObject = new JSONObject(finalJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                List<Movie> movieList = new ArrayList<Movie>();

                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectForList = jsonArray.getJSONObject(i);
                        String name = objectForList.getString("title");
                        String description = objectForList.getString("overview");
                        String releaseDate = objectForList.getString("release_date");
                        String posterPath = objectForList.getString("poster_path");
                        String language = objectForList.getString("original_language");
                        double rating = objectForList.getDouble("vote_average");
                        int id = objectForList.getInt("id");
                        int numVotes = objectForList.getInt("vote_count");
                        JSONArray genreArray = objectForList.getJSONArray("genre_ids");
                        int[] genreIds = new int[genreArray.length()];
                        for (int j = 0; j < genreArray.length(); j++) {
                            genreIds[j] = genreArray.getInt(j);
                        }
                        Movie movieObject = new Movie(name, description, releaseDate, posterPath, language, rating, numVotes, id, genreIds);
                        movieList.add(movieObject);
                    }
                }
                else
                {
                    Movie errorMovie = new Movie();
                    errorMovie.setName("No results :(");
                    errorMovie.setRealeaseDate("Coming soon?");
                    errorMovie.setVoteAverage(0);
                    movieList.add(errorMovie);
                }
                return movieList;

            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally {
                if (connection != null)
                {
                    connection.disconnect();
                }
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.movie_layout, s);
            listView.setAdapter(adapter);
        }
    }

    public class MovieAdapter extends ArrayAdapter {

        private List<Movie> list;
        private LayoutInflater inflater;

        public MovieAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            this.list = objects;
            this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.movie_layout,parent,false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView textView3 = (TextView) convertView.findViewById(R.id.textView3);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            Button button = (Button) convertView.findViewById(R.id.buttonMore);
            new ImageTask(imageView).execute("https://image.tmdb.org/t/p/w500" + list.get(position).getPosterPath());
            textView.setText(list.get(position).getName());
            textView2.setText(list.get(position).getRealeaseDate());
            textView3.setText(list.get(position).getNumVotes() + " people have voted");
            ratingBar.setNumStars(5);
            ratingBar.setRating((float) list.get(position).getVoteAverage()/2);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getInternetConnection())
                    {
                        Intent intent = new Intent(SecondActivity.this, DetailedMovieActivity.class);
                        intent.putExtra("name", list.get(position).getName());
                        intent.putExtra("id", list.get(position).getId());
                        intent.putExtra("genres", list.get(position).getGenreId());
                        SecondActivity.this.startActivity(intent);
                    }
                    else
                    {
                        NoInternetConnectionList();
                    }
                }
            });

            return convertView;
        }
    }
}

package com.example.moviedatabasev2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.util.LruCache;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button buttonLastPage;
    private Button buttonNextPage;
    private TextView pageNumber;
    private ListView movieList;
    private ImageButton imageButton;
    private List<Integer> movieIds;
    private LinearLayout progressBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private String filter;
    private int pageNumberInt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        filter = "popular";
        getSupportActionBar().setTitle("Popular");

        buttonLastPage = (Button) findViewById(R.id.buttonLastPage);
        buttonNextPage = (Button) findViewById(R.id.buttonNextPage);
        pageNumber = (TextView) findViewById(R.id.textViewPageNum);
        movieList = (ListView) findViewById(R.id.listView);
        movieIds = new ArrayList<Integer>();
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        progressBar = (LinearLayout) findViewById(R.id.progressbar_view);
        pageNumber.setText("" + pageNumberInt);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new JSONTaskList().execute("https://api.themoviedb.org/3/movie/" + filter + "?api_key=9e295dfde4d031c0baf4813fbb3814a6&page=" + pageNumberInt);

        buttonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumberInt++;
                new JSONTaskList().execute("https://api.themoviedb.org/3/movie/" + filter + "?api_key=9e295dfde4d031c0baf4813fbb3814a6&page=" + pageNumberInt);
                pageNumber.setText("" + pageNumberInt);
            }
        });

        buttonLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNumberInt > 1) {
                    pageNumberInt--;
                    new JSONTaskList().execute("https://api.themoviedb.org/3/movie/" + filter + "?api_key=9e295dfde4d031c0baf4813fbb3814a6&page=" + pageNumberInt);
                    pageNumber.setText("" + pageNumberInt);
                }
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            drawer.closeDrawers();
            MainActivity.this.startActivity(intent);
        }
        if (id == R.id.popular) {
            if (!(filter.equals("popular"))) {
                pageNumberInt = 1;
                pageNumber.setText("" + pageNumberInt);
                filter = "popular";
                getSupportActionBar().setTitle("Popular");
                new JSONTaskList().execute("https://api.themoviedb.org/3/movie/" + filter + "?api_key=9e295dfde4d031c0baf4813fbb3814a6&page=" + pageNumberInt);
            }
            drawer.closeDrawers();
        }
        if (id == R.id.top_rated) {
            if (!(filter.equals("top_rated"))) {
                pageNumberInt = 1;
                pageNumber.setText("" + pageNumberInt);
                filter = "top_rated";
                getSupportActionBar().setTitle("Top Rated");
                new JSONTaskList().execute("https://api.themoviedb.org/3/movie/" + filter + "?api_key=9e295dfde4d031c0baf4813fbb3814a6&page=" + pageNumberInt);
            }
            drawer.closeDrawers();
        }
        if (id == R.id.upcoming) {
            if (!(filter.equals("upcoming"))) {
                pageNumberInt = 1;
                pageNumber.setText("" + pageNumberInt);
                filter = "upcoming";
                getSupportActionBar().setTitle("Upcoming");
                new JSONTaskList().execute("https://api.themoviedb.org/3/movie/" + filter + "?api_key=9e295dfde4d031c0baf4813fbb3814a6&page=" + pageNumberInt);
            }
            drawer.closeDrawers();
        }
        return false;
    }

    private class JSONTaskList extends AsyncTask<String, Void, List<Movie>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            movieList.setVisibility(View.GONE);
            super.onPreExecute();
        }


        @Override
        protected List<Movie> doInBackground(String... params) {
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
                        pageNumberInt = jsonObject.getInt("page");
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    List<Movie> movieList = new ArrayList<Movie>();

                    if (jsonArray.length() != 0) {
                        movieList.clear();
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
                            movieIds.add(i, movieObject.getId());
                        }
                    } else {
                        Movie errorMovie = new Movie();
                        errorMovie.setName("No results :(");
                        errorMovie.setRealeaseDate("Coming soon?");
                        errorMovie.setVoteAverage(0);
                        movieList.add(errorMovie);
                    }
                    return movieList;
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
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            progressBar.setVisibility(View.GONE);
            movieList.setVisibility(View.VISIBLE);
            MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.movie_layout, movies);
            if (movieList != null) {

                movieList.setAdapter(adapter);
            }
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
                    Intent intent = new Intent(MainActivity.this, DetailedMovieActivity.class);
                    intent.putExtra("name", list.get(position).getName());
                    intent.putExtra("id", list.get(position).getId());
                    intent.putExtra("genres", list.get(position).getGenreId());
                    MainActivity.this.startActivity(intent);
                }
            });

            return convertView;
        }
    }
}

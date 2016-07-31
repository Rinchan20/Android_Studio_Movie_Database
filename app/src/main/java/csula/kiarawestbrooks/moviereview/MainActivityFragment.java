package csula.kiarawestbrooks.moviereview;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMovieReviewTask movieReviewTask = new FetchMovieReviewTask();
        //Log.d("MainActivityFragment", " -> in OnStart() method");
        movieReviewTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh)
        {
            Log.d("MainActivityFragment", " -> in OnOptionsItemSelected() method");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
//            FetchMovieReviewTask movieReviewTask = new FetchMovieReviewTask();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d("MainActivityFragment", " -> in OnCreateView() method");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> movieData = new ArrayList<>();
        listView = (ListView) rootView.findViewById(R.id.movie_reviews);
        adapter = new ArrayAdapter(getActivity(), R.layout.row, R.id.movie_reviews, movieData);
        listView.setAdapter(adapter);

        //Add a listener for listview items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsFragment.class);
                intent.putExtra("details", adapter.getItemId(position));
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class FetchMovieReviewTask extends AsyncTask<Void, Void, String[]>
    {
        private final String LOG_TAG = FetchMovieReviewTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Log.d("MainActivityFragment", " -> in DoInBackground() method");
            //The raw JSON response as a string
            String movieReviewJsonString = null;
            String[] results = null;

            try
            {
                //Contruct the URL to get the reviews using the api key
                //Change PRIVATE to API key
                URL url = new URL("https://api.themoviedb.org/3/movie/now_playing?api_key=PRIVATE&page=1");
                Log.d(LOG_TAG, " -> API URL:" + url.toString());
                //Send a request to the movie reviews api and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                {
                    //Then there is nothing to do
                    movieReviewJsonString = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0)
                {
                    movieReviewJsonString = null;
                }
                movieReviewJsonString = buffer.toString();
                //Log.d("MainActivityFragment", " before getting movie information from JSON");
                results = getMovieInformationFromJson(movieReviewJsonString);
                //Log.d("MainActivityFragment", " after getting movie information from JSON");
            }catch(IOException error)
            {
                Log.e("MovieFragment", "ERROR: ", error);
                movieReviewJsonString = null;
            }catch(JSONException error)
            {
                movieReviewJsonString = null;
            }finally
            {
                if(urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if(reader != null)
                {
                    try
                    {
                        reader.close();
                    }catch(final IOException error)
                    {
                        Log.e("MovieFragment", "Error closing buffered reader stream", error);
                    }
                }
            }

            return results;
        }

        private String[] getMovieInformationFromJson(String movieReviewJsonString) throws JSONException
        {
            //Log.d("MainActivityFragment", " -> in getmovieInformationFromJSON() method");
            final String HIGHEST_POSSIBLE_REVIEW_SCORE = "10";

            //Items to be extracted
            final String MOVIE_LIST = "results";
            final String MOVIE_NAME = "title";
            final String MOVIE_REVIEW_SCORE = "vote_average";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieReviewJsonString);
            Log.d(LOG_TAG, " the JSON object, movieJson -> " + movieJson.toString());
            JSONArray movieReviewArray = movieJson.getJSONArray(MOVIE_LIST);
//            for(int index = 0; index < movieReviewArray.length(); index++)
//            {
//                Log.d(LOG_TAG, " a JSON object,from the movieReviewArray -> " + movieReviewArray.get(index));
//            }

            String[] result = new String[movieReviewArray.length()];
            //Log.d(LOG_TAG, " size of resulting array -> " + result.length);

            for(int index = 0; index < result.length; index++)
            {
                //Log.d(LOG_TAG, " index -> " + index);
                String title;
                String overview;
                String releaseDate;
                String reviewScore;

                //Log.d(LOG_TAG, " before getting JSONObject " );
                JSONObject movieReview = movieReviewArray.getJSONObject(index);
                //Log.d(LOG_TAG, " assigned movieReview to -> " + movieReview);
                title = movieReview.getString(MOVIE_NAME);
                //Log.d(LOG_TAG, " the movie title -> " + title);
                overview = movieReview.getString(MOVIE_OVERVIEW);
                //Log.d(LOG_TAG, " the movie overview -> " + overview);
                releaseDate = movieReview.getString(MOVIE_RELEASE_DATE);
                //Log.d(LOG_TAG, " the movie release date -> " + releaseDate);
                reviewScore = movieReview.getString(MOVIE_REVIEW_SCORE);
                //Log.d(LOG_TAG, " the movie review score -> " + reviewScore);

                result[index] = title + " - " + overview + " - " + releaseDate + " - " + reviewScore + " - " + HIGHEST_POSSIBLE_REVIEW_SCORE;
                //Log.d(LOG_TAG, " the movie result -> " + result[index]);
            }
            //Log.d(LOG_TAG, " after result's for loop");
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(strings == null)
            {
                Log.d(LOG_TAG, " in onPostExecute -> strings is null");
            }else
            {
                int count = 1;
                for(String string : strings)
                {
                    Log.d(LOG_TAG, " in onPostExecute -> string #" + count + " is " + string);
                    count++;
                }

            }
//          Cannot get past here, gives null pointer exception even when debugger shows adapter is not null
//            adapter.clear();
//            adapter.addAll(new ArrayList<String>(Arrays.asList(strings)));
        }
    }
}

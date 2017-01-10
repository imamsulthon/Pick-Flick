package sadboy.pickflick.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.adapters.CastAdapter;
import sadboy.pickflick.adapters.FilmsAdapter;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.MyApplication;
import sadboy.pickflick.models.Film;
import sadboy.pickflick.models.Person;

public class MovieDetail extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    private Snackbar snackbar;
    private String movie_id;
    private String movie_title;
    private String movie_posterpath;
    private String movie_date;
    private SliderLayout sliderShow;
    private String popularMoviesJsonData;
    private String popularTVJsonData;
    private TextView title, director, rating, releaseDate, genre, adulttime;
    ExpandableTextView overview;
    private ImageView poster;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private String movieJsonData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        snackbar = Snackbar
                .make(findViewById(R.id.movie_detail_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        poster = (ImageView) findViewById(R.id.movie_poster);
        title = (TextView) findViewById(R.id.movie_title);
        title.setSelected(true);
        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        overview = (ExpandableTextView) findViewById(R.id.movie_overview);
        director = (TextView) findViewById(R.id.movie_director);
        director.setSelected(true);
        director.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        rating = (TextView) findViewById(R.id.movie_rating);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        genre = (TextView) findViewById(R.id.movie_genre);
        genre.setSelected(true);
        genre.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        adulttime = (TextView) findViewById(R.id.adult_time);

        Intent i = getIntent();
        movieJsonData = i.getStringExtra("movieJsonData");

        if (movieJsonData.equals("") == true) {
            showSnack(false);
        } else {
            setMovieDetail(movieJsonData);
        }
        setToolbar();
        checkConnection();


    }


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_movie_detail);
        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1: {
                Calendar calendar = Calendar.getInstance();

                new DatePickerDialog(this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        /*  Intent myIntent = new Intent(this, MyReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, 5000, pendingIntent);
                Log.d("Button", Float.toString(calendar.getTimeInMillis()));*/


                        movie_date = monthOfYear + "/" + dayOfMonth + "/" + year;

                        SharedPreferences read = getApplicationContext().getSharedPreferences("Reminders_Movie", MODE_PRIVATE);
                        SharedPreferences write = getApplicationContext().getSharedPreferences("Reminders_Movie", MODE_PRIVATE);

                        String s1 = read.getString("MovieID", null);
                        String s2 = read.getString("MovieTitle", null);
                        String s3 = read.getString("MovieImage", null);
                        String s4 = read.getString("MovieDate", null);

                        SharedPreferences.Editor editor = write.edit();

                        if (s1 != null) {
                            editor.putString("MovieID", s1 + "," + movie_id);
                            editor.putString("MovieTitle", s2 + "," + movie_title);
                            editor.putString("MovieImage", s3 + "," + movie_posterpath);
                            editor.putString("MovieDate", s4 + "," + movie_date);
                            Toast toast = Toast.makeText(MovieDetail.this, "Movie added to reminders", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            editor.putString("MovieID", "," + movie_id);
                            editor.putString("MovieTitle", "," + movie_title);
                            editor.putString("MovieImage", "," + movie_posterpath);
                            editor.putString("MovieDate", "," + movie_date);
                            Toast toast = Toast.makeText(MovieDetail.this, "Movie added to reminders", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        editor.apply();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();


            }
            break;
            case R.id.fab2: {
                SharedPreferences read = getApplicationContext().getSharedPreferences("Bookmarks_Movie", MODE_PRIVATE);
                SharedPreferences write = getApplicationContext().getSharedPreferences("Bookmarks_Movie", MODE_PRIVATE);

                String s1 = read.getString("MovieID", null);
                String s2 = read.getString("MovieTitle", null);
                String s3 = read.getString("MovieImage", null);

                SharedPreferences.Editor editor = write.edit();

                if (s1 != null) {
                    editor.putString("MovieID", s1 + "," + movie_id);
                    editor.putString("MovieTitle", s2 + "," + movie_title);
                    editor.putString("MovieImage", s3 + "," + movie_posterpath);
                    Toast toast = Toast.makeText(this, "Movie added to watchlist", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    editor.putString("MovieID", "," + movie_id);
                    editor.putString("MovieTitle", "," + movie_title);
                    editor.putString("MovieImage", "," + movie_posterpath);
                    Toast toast = Toast.makeText(this, "Movie added to watchlist", Toast.LENGTH_SHORT);
                    toast.show();
                }
                editor.apply();
            }
            break;
            default: {

            }
        }
    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }


    private void populateRecyclerView1(RecyclerView recyclerView, List<Person> celebs) {

        ProgressBar p = (ProgressBar) findViewById(R.id.movie_detail_progress_bar);
        CastAdapter adapter = new CastAdapter(this, celebs, p);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void populateRecyclerView2(RecyclerView recyclerView, List<Film> films) {

        ProgressBar p = (ProgressBar) findViewById(R.id.movie_detail_progress_bar);
        FilmsAdapter adapter = new FilmsAdapter(this, films, p);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    public void setMovieDetail(String jsonData) {
        List<Person> celebs;
        List<Film> films;

        sliderShow = (SliderLayout) findViewById(R.id.movie_images);
        sliderShow.setDuration(5000);
        sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.indicator_images));


        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONObject images = jsonResponse.getJSONObject("images");
            JSONArray genr = jsonResponse.getJSONArray("genres");
            JSONArray backdrops = images.getJSONArray("backdrops");
            JSONObject credits = jsonResponse.getJSONObject("credits");
            JSONArray crew = credits.getJSONArray("crew");
            JSONArray cas = credits.getJSONArray("cast");
            JSONObject similar = jsonResponse.getJSONObject("similar");

            int l = crew.length();
            String dir;

            movie_id = jsonResponse.getString("id");

            title.setText(jsonResponse.getString("title"));

            Picasso.with(this)
                    .load("https://image.tmdb.org/t/p/w342" + jsonResponse.getString("poster_path"))
                    .fit()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(poster);

            movie_title = jsonResponse.getString("title");
            movie_posterpath = jsonResponse.getString("poster_path");

            overview.setText(jsonResponse.getString("overview"));


            int dataSize = backdrops.length();

            if (dataSize > 7) {
                dataSize = 7;
            }

            for (int i = 0; i < dataSize; i++) {

                JSONObject backdropImage = backdrops.getJSONObject(i);

                DefaultSliderView sliderView = new DefaultSliderView(this);
                sliderView
                        .image("https://image.tmdb.org/t/p/w780" + backdropImage.getString("file_path"))
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                sliderShow.addSlider(sliderView);

            }

            int den = cas.length();
            celebs = new ArrayList<>(den);
            for (int i = 0; i < den; i++) {

                JSONObject jsonFilm = cas.getJSONObject(i);

                Person celeb = new Person();

                celeb.setName(jsonFilm.getString("name"));
                celeb.setProfilePath(jsonFilm.getString("profile_path"));
                celeb.setId(jsonFilm.getString("id"));
                celeb.setRole(jsonFilm.getString("character"));
                celebs.add(celeb);
            }

            RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.cast_recyclerview);
            populateRecyclerView1(recyclerView1, celebs);

            JSONArray results = similar.getJSONArray("results");
            int datasize = results.length();
            films = new ArrayList<>(datasize);

            for (int i = 0; i < datasize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Film film = new Film();

                film.setTitle(jsonFilm.getString("title"));
                film.setPosterPath(jsonFilm.getString("poster_path"));
                film.setId(jsonFilm.getString("id"));

                films.add(film);
            }

            RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.similar_recyclerview);
            populateRecyclerView2(recyclerView2, films);

            for (int i = 0; i < l; i++) {
                JSONObject str = crew.getJSONObject(i);
                dir = str.getString("job");
                if (dir.equals("Director")) {
                    dir = str.getString("name");
                    director.setText(dir);
                    break;
                }
            }

            rating.setText(jsonResponse.getString("vote_average"));
            releaseDate.setText(jsonResponse.getString("release_date"));

            String adult = jsonResponse.getString("adult");
            String running = jsonResponse.getString("runtime");

            int r = 0;
            if (!running.equals("null")) {
                r = Integer.parseInt(running);

            }

            int h = r / 60;
            int m = r % 60;
            if (adult.equals("false")) {
                adult = "N/A";
            } else {
                adult = "Adult";
            }

            if (running.equals("-")) {
                adulttime.setText("-");
            } else {
                String s = adult + " . " + h + " hour " + m + " min";
                adulttime.setText(s);
            }
            adulttime.setSelected(true);
            adulttime.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            String gen;
            int len = genr.length();
            JSONObject stri = genr.getJSONObject(0);
            gen = stri.getString("name");

            for (int i = 1; i < len; i++) {
                JSONObject str = genr.getJSONObject(i);
                gen = gen + ", " + str.getString("name");

            }
            genre.setText(gen);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        } else {

            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.parseColor("#00b8d4"));
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProgressBar p = (ProgressBar) findViewById(R.id.movie_detail_progress_bar);
        p.setVisibility(View.INVISIBLE);
        MyApplication.getInstance().setConnectivityListener(MovieDetail.this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent i = new Intent(this,Search.class);
            startActivity(i);
        } else if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Pick Flick - Available on Play Store");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie\n" + "https://www.themoviedb.org/movie/" + movie_id);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));

        } else if (id == R.id.action_home) {
            Intent i = new Intent(this, Films.class);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            popularMoviesJsonData = pref.getString("popularMovieJsonData", null);
            popularTVJsonData = pref.getString("popularTVJsonData", null);
            i.putExtra("popularMovieJsonData", popularMoviesJsonData);
            i.putExtra("popularTVJsonData", popularTVJsonData);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}

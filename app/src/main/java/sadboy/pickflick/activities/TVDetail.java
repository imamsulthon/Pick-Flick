package sadboy.pickflick.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import sadboy.pickflick.adapters.TVAdapter;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.MyApplication;
import sadboy.pickflick.models.Person;
import sadboy.pickflick.models.Television;

public class TVDetail extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,View.OnClickListener {

    private Snackbar snackbar;
    private String tv_id;
    private String tv_title;
    private String tv_posterpath;
    private String tv_date;
    private SliderLayout sliderShow;
    private String popularMoviesJsonData;
    private String popularTVJsonData;
    private TextView title, episodes, rating, airingDate, genre, channeltime;
    ExpandableTextView overview;
    private ImageView poster;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);

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
                .make(findViewById(R.id.tv_detail_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);


        poster = (ImageView) findViewById(R.id.tv_poster);
        title = (TextView) findViewById(R.id.tv_title);
        title.setSelected(true);
        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        overview = (ExpandableTextView) findViewById(R.id.tv_overview);
        episodes = (TextView) findViewById(R.id.tv_episodes);
        rating = (TextView) findViewById(R.id.tv_rating);
        airingDate = (TextView) findViewById(R.id.tv_release_date);
        genre = (TextView) findViewById(R.id.tv_genre);
        genre.setSelected(true);
        genre.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        channeltime = (TextView) findViewById(R.id.channel_time);

        Intent i = getIntent();
        String movieJsonData = i.getStringExtra("tvJsonData");

        if (movieJsonData.equals("") == true) {
            showSnack(false);
        } else {
            setTVDetail(movieJsonData);
        }
        setToolbar();
        checkConnection();

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tv_detail);
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


                        tv_date = monthOfYear + "/" + dayOfMonth + "/" + year;

                        SharedPreferences read = getApplicationContext().getSharedPreferences("Reminders_TV", MODE_PRIVATE);
                        SharedPreferences write = getApplicationContext().getSharedPreferences("Reminders_TV", MODE_PRIVATE);

                        String s1 = read.getString("TVID", null);
                        String s2 = read.getString("TVTitle", null);
                        String s3 = read.getString("TVImage", null);
                        String s4 = read.getString("TVDate", null);

                        SharedPreferences.Editor editor = write.edit();

                        if (s1 != null) {
                            editor.putString("TVID", s1 + "," + tv_id);
                            editor.putString("TVTitle", s2 + "," + tv_title);
                            editor.putString("TVImage", s3 + "," + tv_posterpath);
                            editor.putString("TVDate", s4 + "," + tv_date);
                            Toast toast = Toast.makeText(TVDetail.this, "TV show added to reminders", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            editor.putString("TVID", "," + tv_id);
                            editor.putString("TVTitle", "," + tv_title);
                            editor.putString("TVImage", "," + tv_posterpath);
                            editor.putString("TVDate", "," + tv_date);
                            Toast toast = Toast.makeText(TVDetail.this, "TV show added to reminders", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        editor.apply();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
                break;
            case R.id.fab2:
            {
                SharedPreferences read = getApplicationContext().getSharedPreferences("Bookmarks_TV", MODE_PRIVATE);
                SharedPreferences write = getApplicationContext().getSharedPreferences("Bookmarks_TV", MODE_PRIVATE);

                String s1 = read.getString("TVID", null);
                String s2 = read.getString("TVTitle", null);
                String s3 = read.getString("TVImage", null);

                SharedPreferences.Editor editor = write.edit();

                if (s1 != null) {
                    editor.putString("TVID", s1 + "," + tv_id);
                    editor.putString("TVTitle", s2 + "," + tv_title);
                    editor.putString("TVImage", s3 + "," + tv_posterpath);
                    Toast toast = Toast.makeText(this, "TV show added to watchlist", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    editor.putString("TVID", "," + tv_id);
                    editor.putString("TVTitle", "," + tv_title);
                    editor.putString("TVImage", "," + tv_posterpath);
                    Toast toast = Toast.makeText(this, "TV show added to watchlist", Toast.LENGTH_SHORT);
                    toast.show();
                }
                editor.apply();
            }
                break;
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

        ProgressBar p = (ProgressBar)findViewById(R.id.tv_detail_progress_bar);
        CastAdapter adapter = new CastAdapter(this, celebs,p);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void populateRecyclerView2(RecyclerView recyclerView, List<Television> tvs) {

        ProgressBar p = (ProgressBar)findViewById(R.id.tv_detail_progress_bar);
        TVAdapter adapter = new TVAdapter(this, tvs,p);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    public void setTVDetail(String jsonData) {
        List<Person> celebs;
        List<Television> tvs;

        sliderShow = (SliderLayout) findViewById(R.id.tv_images);
        sliderShow.setDuration(5000);
        sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.images_indicator));

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONObject images = jsonResponse.getJSONObject("images");
            JSONArray genr = jsonResponse.getJSONArray("genres");
            JSONArray backdrops = images.getJSONArray("backdrops");
            JSONObject credits = jsonResponse.getJSONObject("credits");
            JSONArray cas = credits.getJSONArray("cast");
            JSONObject similar = jsonResponse.getJSONObject("similar");
            JSONArray rtime = jsonResponse.getJSONArray("episode_run_time");
            JSONArray channel = jsonResponse.getJSONArray("networks");

            title.setText(jsonResponse.getString("name"));
            Picasso.with(this)
                    .load("https://image.tmdb.org/t/p/w342" + jsonResponse.getString("poster_path"))
                    .fit()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(poster);
            tv_posterpath = jsonResponse.getString("poster_path");

            tv_title = jsonResponse.getString("name");
            tv_id = jsonResponse.getString("id");
            airingDate.setText(jsonResponse.getString("first_air_date") + " to " + jsonResponse.getString("last_air_date"));
            overview.setText(jsonResponse.getString("overview"));
            String season = jsonResponse.getString("number_of_seasons") + " seasons & " +
                    jsonResponse.getString("number_of_episodes") + " episodes";
            episodes.setText(season);




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
            tvs = new ArrayList<>(datasize);

            for (int i = 0; i < datasize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Television tv = new Television();

                tv.setTitle(jsonFilm.getString("name"));
                tv.setPosterPath(jsonFilm.getString("poster_path"));
                tv.setId(jsonFilm.getString("id"));

                tvs.add(tv);
            }

            rating.setText(jsonResponse.getString("vote_average"));
            String gen;
            int len = genr.length();
            JSONObject stri = genr.getJSONObject(0);
            gen = stri.getString("name");

            for (int i = 1; i < len; i++) {
                JSONObject str = genr.getJSONObject(i);
                gen = gen + ", " + str.getString("name");

            }

            genre.setText(gen);

            String rt = rtime.getString(0);
            JSONObject cha = channel.getJSONObject(0);

            String chtim = cha.getString("name") + " . " + rt + " min";
            channeltime.setText(chtim);
            channeltime.setSelected(true);
            channeltime.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.similar_recyclerview);
            populateRecyclerView2(recyclerView2, tvs);

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
            sbView.setBackgroundColor(Color.parseColor("#2979ff"));
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(TVDetail.this);
        ProgressBar p = (ProgressBar)findViewById(R.id.tv_detail_progress_bar);
        p.setVisibility(View.INVISIBLE);
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
            sendIntent.putExtra(Intent.EXTRA_TEXT,"Check out this TV Show\n"+"https://www.themoviedb.org/tv/" + tv_id);
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

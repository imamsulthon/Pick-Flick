package sadboy.pickflick.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.adapters.FilmsAdapter;
import sadboy.pickflick.adapters.PersonImageAdapter;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.MyApplication;
import sadboy.pickflick.models.Film;

public class PersonDetail extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Snackbar snackbar;
    public String celeb_id;
    private TextView name, born, birthplace;
    private String popularMoviesJsonData;
    private String popularTVJsonData;
    private ExpandableTextView bio;
    private ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        snackbar = Snackbar
                .make(findViewById(R.id.person_detail_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        Intent i = getIntent();
        String movieJsonData = i.getStringExtra("personJsonData");

        profile = (ImageView) findViewById(R.id.person_image);
        name = (TextView) findViewById(R.id.person_name);
        born = (TextView) findViewById(R.id.person_born);
        birthplace = (TextView) findViewById(R.id.person_birthplace);
        bio = (ExpandableTextView) findViewById(R.id.person_bio);

        if (movieJsonData == null) {
            showSnack(false);
        } else {
            setPersonDetail(movieJsonData);
        }
        setToolbar();
        checkConnection();

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_person_detail);
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

    private void populateRecyclerView1(RecyclerView recyclerView, List<String> images) {

        PersonImageAdapter adapter = new PersonImageAdapter(this, images);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void populateRecyclerView2(RecyclerView recyclerView, List<Film> films) {

        ProgressBar p = (ProgressBar) findViewById(R.id.person_detail_progress_bar);
        FilmsAdapter adapter = new FilmsAdapter(this, films, p);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    public void setPersonDetail(String jsonData) {


        JSONObject jsonResponse;
        List<Film> films;

        try {
            jsonResponse = new JSONObject(jsonData);
            JSONObject images = jsonResponse.getJSONObject("images");
            JSONArray backdrops = images.getJSONArray("profiles");
            JSONObject cast = jsonResponse.getJSONObject("combined_credits");
            JSONArray results = cast.getJSONArray("cast");

            celeb_id = jsonResponse.getString("id");
            name.setText(jsonResponse.getString("name"));
            name.setSelected(true);
            name.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            born.setText(jsonResponse.getString("birthday"));
            if (born.getText().equals("null") || born.getText().equals("")) {
                born.setText("Information Unavailable");
            }

            birthplace.setText(jsonResponse.getString("place_of_birth"));
            if (birthplace.getText().equals("null") || birthplace.getText().equals("")) {
                birthplace.setText("Information Unavailable");
            }

            bio.setText(jsonResponse.getString("biography"));
            if (bio.getText().equals("null") || bio.getText().equals("")) {
                bio.setText("Information Unavailable");
            }

            Picasso.with(this)
                    .load("https://image.tmdb.org/t/p/w185" + jsonResponse.getString("profile_path"))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(profile);

            int dataSize = backdrops.length();

            List<String> pics = new ArrayList<>(dataSize);
            for (int i = 0; i < dataSize; i++) {

                JSONObject backdropImage = backdrops.getJSONObject(i);
                pics.add(backdropImage.getString("file_path"));
            }

            RecyclerView r1 = (RecyclerView) findViewById(R.id.person_images);
            populateRecyclerView1(r1, pics);


            int size = results.length();
            films = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Film film = new Film();

                film.setTitle("as " + jsonFilm.getString("character"));
                if (film.getTitle().equals("as ")) {
                    film.setTitle("Information Unavailable");
                }
                film.setPosterPath(jsonFilm.getString("poster_path"));
                film.setId(jsonFilm.getString("id"));

                if (jsonFilm.getString("media_type").equals("movie")) {
                    films.add(film);
                }
            }

            RecyclerView r2 = (RecyclerView) findViewById(R.id.cast_recyclerview);
            populateRecyclerView2(r2, films);

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
        ProgressBar p = (ProgressBar) findViewById(R.id.person_detail_progress_bar);
        p.setVisibility(View.INVISIBLE);
        MyApplication.getInstance().setConnectivityListener(PersonDetail.this);
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
            Intent i = new Intent(this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Pick Flick - Available on Play Store");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check this out\n" + "https://www.themoviedb.org/person/" + celeb_id);
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

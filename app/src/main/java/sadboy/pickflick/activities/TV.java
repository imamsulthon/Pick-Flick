package sadboy.pickflick.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.adapters.TVAdapter;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.AsyncDownloader.JsonDataSetter;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.http.MyApplication;
import sadboy.pickflick.models.Television;

public class TV extends AppCompatActivity implements JsonDataSetter, ConnectivityReceiver.ConnectivityReceiverListener {

    private Drawer result = null;
    private String popularMoviesJsonData;
    private String popularTVJsonData;
    private AsyncDownloader downloader1, downloader2, downloader3, downloader4;
    private Snackbar snackbar;
    private SliderLayout sliderShow;
    private Intent ini;
    private TextView text1, text2, text3;
    private TextView more1, more2, more3;
    static int cnt;
    static int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        setDrawer(savedInstanceState);

        cnt = 0;
        flag = 0;

        Intent i = getIntent();
        popularMoviesJsonData = i.getStringExtra("popularMovieJsonData");
        popularTVJsonData = i.getStringExtra("popularTVJsonData");

        ini = new Intent(this, TVDetail.class);

        snackbar = Snackbar
                .make(findViewById(R.id.tv_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);

        more1 = (TextView) findViewById(R.id.more1);
        more2 = (TextView) findViewById(R.id.more2);
        more3 = (TextView) findViewById(R.id.more3);

        checkConnection();
    }

    public void setUrls() {


        if (flag == 0) {
            setPopularTV(popularTVJsonData);
            flag++;
        }

        DatabaseUrl url = DatabaseUrl.getInstance();

        String topRatedTV = url.getTopRatedTV("1");
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(topRatedTV);

        String airingTodayTV = url.getAiringTodayTV("1");
        downloader2 = new AsyncDownloader(this);
        downloader2.execute(airingTodayTV);

        String onTheAirTV = url.getOnTheAirTV("1");
        downloader3 = new AsyncDownloader(this);
        downloader3.execute(onTheAirTV);


    }

    @Override
    public void setJsonData(String jsonData) {

        if (jsonData == null) {
            showSnack(false);
        } else {
            switch (cnt) {
                case 0: {
                    setTopRatedTV(jsonData);
                    text1.setVisibility(View.VISIBLE);
                    more1.setVisibility(View.VISIBLE);
                    downloader1.cancel(true);
                    cnt++;
                }
                break;
                case 1: {
                    setAiringTodayTV(jsonData);
                    text2.setVisibility(View.VISIBLE);
                    more2.setVisibility(View.VISIBLE);
                    downloader2.cancel(true);
                    cnt++;
                }
                break;
                case 2: {
                    setOnTheAirTV(jsonData);
                    text3.setVisibility(View.VISIBLE);
                    more3.setVisibility(View.VISIBLE);
                    downloader3.cancel(true);
                    cnt++;
                }
                break;
                case 3: {
                    ini.putExtra("tvJsonData", jsonData);
                    downloader4.cancel(true);
                    startActivity(ini);
                }

            }
        }

    }

    private void populateRecyclerView(RecyclerView recyclerView, List<Television> tvs) {

        ProgressBar p = (ProgressBar) findViewById(R.id.tv_progress_bar);

        TVAdapter adapter = new TVAdapter(this, tvs, p);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    public void openTVDetail(String id) {
        DatabaseUrl url = DatabaseUrl.getInstance();

        String tvDetail = url.getTVDetail(id);
        downloader4 = new AsyncDownloader(this);
        downloader4.execute(tvDetail);

    }

    private void setPopularTV(String jsonData) {
        sliderShow = (SliderLayout) findViewById(R.id.slider_popular_tv);
        sliderShow.setDuration(5000);

        List<Television> tvs;
        sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator_popular_tv));

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONArray results = jsonResponse.getJSONArray("results");
            int dataSize = results.length();
            tvs = new ArrayList<>(dataSize);

            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                final Television tv = new Television();

                tv.setTitle(jsonFilm.getString("name"));
                tv.setBackDropPath(jsonFilm.getString("backdrop_path"));
                tv.setId(jsonFilm.getString("id"));


                tvs.add(tv);

                if (!tv.getBackDropPath().equals("null")) {
                    TextSliderView textSliderView = new TextSliderView(this);
                    textSliderView
                            .description(tv.getTitle())
                            .image("https://image.tmdb.org/t/p/w780" + tv.getBackDropPath())
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                    textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            openTVDetail(tv.getId());
                            ProgressBar p = (ProgressBar) findViewById(R.id.tv_progress_bar);
                            p.setVisibility(View.VISIBLE);
                        }
                    });

                    sliderShow.addSlider(textSliderView);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTopRatedTV(String jsonData) {

        List<Television> tvs;

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONArray results = jsonResponse.getJSONArray("results");
            int dataSize = results.length();
            tvs = new ArrayList<>(dataSize);

            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Television tv = new Television();

                tv.setTitle(jsonFilm.getString("name"));
                tv.setBackDropPath(jsonFilm.getString("backdrop_path"));
                tv.setPosterPath(jsonFilm.getString("poster_path"));
                tv.setId(jsonFilm.getString("id"));

                if (!tv.getPosterPath().equals("null")) {
                    tvs.add(tv);
                }
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.top_rated_tv_recyclerview);
            populateRecyclerView(recyclerView, tvs);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setAiringTodayTV(String jsonData) {
        List<Television> tvs;

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONArray results = jsonResponse.getJSONArray("results");
            int dataSize = results.length();
            tvs = new ArrayList<>(dataSize);

            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Television tv = new Television();

                tv.setTitle(jsonFilm.getString("name"));
                tv.setBackDropPath(jsonFilm.getString("backdrop_path"));
                tv.setPosterPath(jsonFilm.getString("poster_path"));
                tv.setId(jsonFilm.getString("id"));

                if (!tv.getPosterPath().equals("null")) {
                    tvs.add(tv);
                }
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.airing_today_tv_recyclerview);
            populateRecyclerView(recyclerView, tvs);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOnTheAirTV(String jsonData) {
        List<Television> tvs;

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONArray results = jsonResponse.getJSONArray("results");
            int dataSize = results.length();
            tvs = new ArrayList<>(dataSize);

            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Television tv = new Television();

                tv.setTitle(jsonFilm.getString("name"));
                tv.setBackDropPath(jsonFilm.getString("backdrop_path"));
                tv.setPosterPath(jsonFilm.getString("poster_path"));
                tv.setId(jsonFilm.getString("id"));

                if (!tv.getPosterPath().equals("null")) {
                    tvs.add(tv);
                }
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.on_the_air_tv_recyclerview);
            populateRecyclerView(recyclerView, tvs);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openMore(View v) {
        Intent i = new Intent(this, MoreTV.class);
        i.putExtra("popularMovieJsonData", popularMoviesJsonData);
        i.putExtra("popularTVJsonData", popularTVJsonData);

        int id = v.getId();

        switch (id) {
            case R.id.more1:
                i.putExtra("Type", "1");
                break;
            case R.id.more2:
                i.putExtra("Type", "2");
                break;
            case R.id.more3:
                i.putExtra("Type", "3");
                break;
        }
        startActivity(i);
    }

    private void setDrawer(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tv);
        setSupportActionBar(toolbar);

        result = new DrawerBuilder(this)
                .withRootView(R.id.drawer_container_tv)
                .withStickyHeader(R.layout.drawer_header)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Movies").withIcon(R.drawable.ic_local_movies),
                        new PrimaryDrawerItem().withName("TV").withIcon(R.drawable.ic_tv_white),
                        new PrimaryDrawerItem().withName("Celebrities").withIcon(R.drawable.ic_people),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Watchlist").withIcon(R.drawable.ic_bookmark),
                        new PrimaryDrawerItem().withName("Reminders").withIcon(R.drawable.ic_alarm),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Share").withIcon(R.drawable.ic_share_app),
                        new PrimaryDrawerItem().withName("Contact").withIcon(R.drawable.ic_email),
                        new PrimaryDrawerItem().withName("Exit").withIcon(R.drawable.ic_exit_to_app)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {
                            case 0: {

                                Intent i = new Intent(TV.this, Films.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 1: {
                            }
                            break;
                            case 2: {
                                Intent i = new Intent(TV.this, Celebrities.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 4: {
                                Intent i = new Intent(TV.this, Bookmarks.class);
                                startActivity(i);
                            }
                            break;
                            case 5: {
                                Intent i = new Intent(TV.this, Reminders.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 7: {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        "Download Pick Flick - Available on Play Store \n" + " https://play.google.com/store/apps/details?id=sadboy.pickflick");
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, "Share via"));
                            }
                            break;
                            case 8: {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.setData(Uri.parse("mailto:"));
                                email.setType("text/plain");
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"heysadboy@gmail.com"});
                                email.putExtra(Intent.EXTRA_SUBJECT, "Pick Flick app support");
                                email.putExtra(Intent.EXTRA_TEXT, "Please specify your mobile configuration, android version and your query.");
                                startActivity(Intent.createChooser(email, "Send via"));

                            }
                            break;
                            case 9: {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                startActivity(intent);
                            }
                            break;
                            default: {
                                Toast.makeText(TV.this, ("Invalid Choice " + position), Toast.LENGTH_SHORT).show();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState).withSelectedItemByPosition(1)
                .build();

        TextView t = (TextView) result.getStickyHeader().findViewById(R.id.drawer_title);
        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto.ttf");
        t.setTypeface(roboto);

    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

            setUrls();

            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        } else {

            if (downloader1 != null && downloader2 != null
                    && downloader3 != null) {
                downloader1.cancel(true);
                downloader2.cancel(true);
                downloader3.cancel(true);

            }


            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.parseColor("#2979ff"));
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(TV.this);
        ProgressBar p = (ProgressBar) findViewById(R.id.tv_progress_bar);
        p.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rate) {

            Uri uri = Uri.parse("market://details?id=" + "sadboy.pickflick");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + "sadboy.pickflick")));
            }

        } else if (id == R.id.action_about) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        } else if (id == R.id.action_search) {
            Intent i = new Intent(this, Search.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}

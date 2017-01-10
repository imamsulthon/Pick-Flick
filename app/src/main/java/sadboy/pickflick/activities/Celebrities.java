package sadboy.pickflick.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import sadboy.pickflick.adapters.CelebrityAdapter;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.AsyncDownloader.JsonDataSetter;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.http.MyApplication;
import sadboy.pickflick.models.Person;

public class Celebrities extends AppCompatActivity implements JsonDataSetter, ConnectivityReceiver.ConnectivityReceiverListener {

    private Drawer result = null;
    private String popularMoviesJsonData;
    private String popularTVJsonData;
    private AsyncDownloader downloader1;
    private Snackbar snackbar;
    private String page;
    private ProgressBar p;
    private List<Person> celebs;
    int cnt = 1;
    RecyclerView recyclerView;
    CelebrityAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebrities);

        setDrawer(savedInstanceState);

        Intent i = getIntent();
        popularMoviesJsonData = i.getStringExtra("popularMovieJsonData");
        popularTVJsonData = i.getStringExtra("popularTVJsonData");

        p = (ProgressBar) findViewById(R.id.celebrity_progress_bar);
        celebs = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.celebrities_recyclerview);
        adapter = new CelebrityAdapter(this, celebs, p);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        snackbar = Snackbar
                .make(findViewById(R.id.celebrity_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);
        checkConnection();
    }

    public void setUrls(int page) {

        DatabaseUrl url = DatabaseUrl.getInstance();

        String p  = Integer.toString(page);
        String popularPeople = url.getPopularPersons(p);
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(popularPeople);

    }

    @Override
    public void setJsonData(String jsonData) {

        if (jsonData == null) {
            showSnack(false);
        } else {
            setPopularPeople(jsonData);
            downloader1.cancel(true);
        }

    }

    public void setPopularPeople(String jsonData) {

        p.setVisibility(View.INVISIBLE);
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONArray results = jsonResponse.getJSONArray("results");
            int dataSize = results.length();


            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonFilm = results.getJSONObject(i);

                Person celeb = new Person();

                celeb.setName(jsonFilm.getString("name"));
                celeb.setProfilePath(jsonFilm.getString("profile_path"));
                celeb.setId(jsonFilm.getString("id"));

                JSONArray tempArray = jsonFilm.getJSONArray("known_for");
                JSONObject knownFor = tempArray.getJSONObject(0);

                if (knownFor.getString("media_type").equals("movie")) {
                    celeb.setKnownForFilm(knownFor.getString("original_title") + ", " + knownFor.getString("release_date").substring(0, 4));

                }

                if (!celeb.getProfilePath().equals("null"))
                {    celebs.add(celeb);
                     adapter.notifyDataSetChanged();
                }

            }
            
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager ) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    setUrls(cnt);
                    p.setVisibility(View.VISIBLE);
                    cnt++;
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDrawer(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_celebrities);
        setSupportActionBar(toolbar);

        result = new DrawerBuilder(this)
                .withRootView(R.id.drawer_container_celebrity)
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

                                Intent i = new Intent(Celebrities.this, Films.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 1: {
                                Intent i = new Intent(Celebrities.this, TV.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 2: {

                            }
                            break;
                            case 4: {
                                Intent i = new Intent(Celebrities.this, Bookmarks.class);
                                startActivity(i);
                            }
                            break;
                            case 5: {
                                Intent i = new Intent(Celebrities.this, Reminders.class);
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
                            case 8:{
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.setData(Uri.parse("mailto:"));
                                email.setType("text/plain");
                                email.putExtra(Intent.EXTRA_EMAIL,  new String[]{"heysadboy@gmail.com"});
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
                                Toast.makeText(Celebrities.this, ("Invalid Choice " + position), Toast.LENGTH_SHORT).show();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState).withSelectedItemByPosition(2)
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

            setUrls(cnt);
            cnt++;

            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        } else {

            if (downloader1 != null) {
                downloader1.cancel(true);
            }


            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.parseColor("#00b8d4"));
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(Celebrities.this);
        ProgressBar p = (ProgressBar) findViewById(R.id.celebrity_progress_bar);
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

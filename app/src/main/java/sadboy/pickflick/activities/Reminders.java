package sadboy.pickflick.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import sadboy.pickflick.R;
import sadboy.pickflick.adapters.FragmentAdapter;
import sadboy.pickflick.fragments.MovieReminders;
import sadboy.pickflick.fragments.TVReminders;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.MyApplication;

public class Reminders extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Drawer result = null;
    private String popularMoviesJsonData;
    private String popularTVJsonData;
    private Snackbar snackbar;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentAdapter appAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        MovieReminders movieReminders = new MovieReminders();
        appAdapter.addFragment(movieReminders, "Movies");
        TVReminders tvReminders = new TVReminders();
        appAdapter.addFragment(tvReminders, "TV Shows");


        viewPager.setAdapter(appAdapter);
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#00b8d4"));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(5);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00b8d4"));


        setDrawer(savedInstanceState);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        popularMoviesJsonData = pref.getString("popularMovieJsonData", null);
        popularTVJsonData = pref.getString("popularTVJsonData", null);

        snackbar = Snackbar
                .make(findViewById(R.id.reminders_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        checkConnection();
    }

    private void setDrawer(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reminders);
        setSupportActionBar(toolbar);

        result = new DrawerBuilder(this)
                .withRootView(R.id.drawer_container_reminders)
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

                                Intent i = new Intent(Reminders.this, Films.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 1: {
                                Intent i = new Intent(Reminders.this, TV.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);
                            }
                            break;
                            case 2: {
                                Intent i = new Intent(Reminders.this, Celebrities.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);

                            }
                            case 4: {

                                Intent i = new Intent(Reminders.this, Bookmarks.class);
                                i.putExtra("popularMovieJsonData", popularMoviesJsonData);
                                i.putExtra("popularTVJsonData", popularTVJsonData);
                                startActivity(i);

                            }
                            break;
                            case 5: {

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
                                Toast.makeText(Reminders.this, ("Invalid Choice " + position), Toast.LENGTH_SHORT).show();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState).withSelectedItemByPosition(5)
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
        MyApplication.getInstance().setConnectivityListener(Reminders.this);
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

package sadboy.pickflick.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import sadboy.pickflick.R;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.MyApplication;

public class About extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        snackbar = Snackbar
                .make(findViewById(R.id.about_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        setToolbar();
        checkConnection();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about);
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
        ProgressBar p = (ProgressBar) findViewById(R.id.about_progress_bar);
        p.setVisibility(View.INVISIBLE);
        MyApplication.getInstance().setConnectivityListener(About.this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

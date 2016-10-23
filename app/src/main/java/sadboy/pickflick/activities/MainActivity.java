package sadboy.pickflick.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import sadboy.pickflick.R;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.AsyncDownloader.JsonDataSetter;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.http.MyApplication;

public class MainActivity extends AppCompatActivity implements JsonDataSetter, ConnectivityReceiver.ConnectivityReceiverListener {

    private AsyncDownloader downloader1, downloader2;
    private Snackbar snackbar;
    public ImageView glow;
    private boolean start = true;
    Intent i;
    static int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        cnt = 0;

        i = new Intent(this, Films.class);
        glow = (ImageView) findViewById(R.id.glow);

        snackbar = Snackbar
                .make(findViewById(R.id.main_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#2979ff"));

        checkConnection();

    }


    public void setUrls() {

        DatabaseUrl url = DatabaseUrl.getInstance();

        String popularMovies = url.getPopularMovies();
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(popularMovies);

        String popularTV = url.getPopularTV();
        downloader2 = new AsyncDownloader(this);
        downloader2.execute(popularTV);
    }

    @Override
    public void setJsonData(String jsonData) {

        if (jsonData == null) {
            showSnack(false);
        } else {
            switch (cnt) {
                case 0: {
                    i.putExtra("popularMovieJsonData", jsonData);
                    downloader1.cancel(true);
                    cnt++;
                }
                break;
                case 1: {
                    i.putExtra("popularTVJsonData", jsonData);
                    downloader2.cancel(true);
                    cnt++;
                    startActivity(i);
                    finish();
                }
                break;
            }
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

            if (downloader1 != null && downloader2 != null) {
                downloader1.cancel(true);
                downloader2.cancel(true);
            }
            snackbar.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(MainActivity.this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}

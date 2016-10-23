package sadboy.pickflick.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.adapters.FilmsAdapter;
import sadboy.pickflick.adapters.SearchAdapter;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.ConnectivityReceiver;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.http.MyApplication;
import sadboy.pickflick.models.SearchResult;

public class Search extends AppCompatActivity implements AsyncDownloader.JsonDataSetter, ConnectivityReceiver.ConnectivityReceiverListener {

    private EditText searchBar;
    private AsyncDownloader downloader1;
    private Snackbar snackbar;
    private Snackbar noResults;
    private String str;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private ProgressBar p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = (EditText) findViewById(R.id.search_bar);
        recyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        p = (ProgressBar) findViewById(R.id.search_progress_bar);

        snackbar = Snackbar
                .make(findViewById(R.id.search_activity), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);

        noResults = Snackbar
                .make(findViewById(R.id.search_activity), "No results, try using different keywords", Snackbar.LENGTH_INDEFINITE);
        View sbView = noResults.getView();
        sbView.setBackgroundColor(Color.parseColor("#2979ff"));


        setToolbar();
        queryWatcher();
        checkConnection();
    }

    public void setUrls(String s, String page) {

        DatabaseUrl url = DatabaseUrl.getInstance();

        String results = url.getSearchResults(s, page);
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(results);


    }

    public void queryWatcher() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                p.setVisibility(View.VISIBLE);
                if (downloader1 != null) {
                    downloader1.cancel(true);
                }

                if (s != null) {
                    str = s.toString();
                    setUrls(str, "1");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    downloader1.cancel(true);
                    p.setVisibility(View.INVISIBLE);
                    setUrls("gggggggg", "1");

                    if (noResults.isShown()) {
                        noResults.dismiss();
                    }

                }
            }
        });
    }

    public void populateRecyclerView(String jsonData) {
        List<SearchResult> results = null;

        if (jsonData == null) {

        } else {
            JSONObject jsonResponse;
            try {
                jsonResponse = new JSONObject(jsonData);

                JSONArray r = jsonResponse.getJSONArray("results");
                int dataSize = r.length();
                results = new ArrayList<>(dataSize);

                for (int i = 0; i < dataSize; i++) {

                    JSONObject res = r.getJSONObject(i);

                    final SearchResult result = new SearchResult();

                    result.setId(res.getString("id"));
                    String t = res.getString("media_type");

                    if (t.equals("movie")) {
                        result.setMediaType("Movie");
                    } else if (t.equals("tv")) {
                        result.setMediaType("TV");
                    } else if (t.equals("person")) {
                        result.setMediaType("Person");
                    }

                    if (result.getMediaType().equals("Movie")) {
                        result.setMediaName(res.getString("title"));
                    } else {
                        result.setMediaName(res.getString("name"));
                    }

                    if (result.getMediaType().equals("Movie") || result.getMediaType().equals("TV")) {
                        result.setMediaImage(res.getString("poster_path"));
                    } else {
                        result.setMediaImage(res.getString("profile_path"));
                    }

                    results.add(result);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new SearchAdapter(this, results);
            recyclerView.setAdapter(adapter);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            if (adapter.getItemCount() == 0) {
                noResults.show();
            } else {
                noResults.dismiss();
            }
            p.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setJsonData(String jsonData) {

        populateRecyclerView(jsonData);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            if (adapter.getItemCount() == 0) {
                noResults.show();
            } else {
                noResults.dismiss();
            }
            setUrls(str,"1");
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

            if (snackbar.isShown() == true) {
                snackbar.dismiss();
            }
        } else {

            if (downloader1 != null) {
                downloader1.cancel(true);
            }

            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.parseColor("#2979ff"));
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.getInstance().setConnectivityListener(Search.this);
        p.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


}

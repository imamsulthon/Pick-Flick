package sadboy.pickflick.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.adapters.BookmarksAdapter;
import sadboy.pickflick.adapters.SearchAdapter;
import sadboy.pickflick.models.SearchResult;

/**
 * Created by Varun Kumar on 8/22/2016.
 */
public class TVBookmarks extends android.support.v4.app.Fragment {

    LayoutInflater inflater;
    View tvBookmarks;
    ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        tvBookmarks = inflater.from(container.getContext()).inflate(R.layout.tv_bookmarks, container, false);

        recyclerView = (RecyclerView) tvBookmarks.findViewById(R.id.tv_bookmarks_recyclerview);

        setTVBookmarks();
        return tvBookmarks;
    }

    public void setTVBookmarks() {
        List<SearchResult> results = null;
        SharedPreferences read = getActivity().getApplicationContext().getSharedPreferences("Bookmarks_TV", 0);
        String s1 = read.getString("TVID", null);
        String s2 = read.getString("TVTitle", null);
        String s3 = read.getString("TVImage", null);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.bookmarks_progress_bar);

        String[] ids;
        String[] titles;
        String[] images;

        if (s1 != null) {
            String[] id = s1.split(",");
            String[] title = s2.split(",");
            String[] image = s3.split(",");

            ids = Arrays.copyOfRange(id, 1, id.length);
            titles = Arrays.copyOfRange(title, 1, title.length);
            images = Arrays.copyOfRange(image, 1, image.length);

            results = new ArrayList<>(ids.length);

            for (int i = 0; i < ids.length; i++) {
                final SearchResult result = new SearchResult();

                result.setId(ids[i]);
                result.setMediaName(titles[i]);
                result.setMediaImage(images[i]);
                result.setMediaType("TV");
                results.add(result);
            }
        }


        BookmarksAdapter adapter = new BookmarksAdapter(getActivity(), results,progressBar );
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
    }
}

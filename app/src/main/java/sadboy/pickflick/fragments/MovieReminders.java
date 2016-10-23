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
import sadboy.pickflick.adapters.ReminderAdapter;
import sadboy.pickflick.models.SearchResult;

/**
 * Created by Varun Kumar on 8/22/2016.
 */
public class MovieReminders  extends android.support.v4.app.Fragment {
    LayoutInflater inflater;
    View movieReminders;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        movieReminders = inflater.from(container.getContext()).inflate(R.layout.movie_reminders, container, false);

        recyclerView = (RecyclerView) movieReminders.findViewById(R.id.movie_reminders_recyclerview);

        setMovieBookmarks();
        return  movieReminders;
    }

    public void setMovieBookmarks()
    {
        List<SearchResult> results = null;
        SharedPreferences read = getActivity().getApplicationContext().getSharedPreferences("Reminders_Movie", 0);
        String s1 = read.getString("MovieID", null);
        String s2 = read.getString("MovieTitle", null);
        String s3 = read.getString("MovieImage", null);
        String s4 = read.getString("MovieDate",null);

        String[] ids;
        String[] titles;
        String[] images;
        String[] dates;

        if (s1 != null) {
            String[] id = s1.split(",");
            String[] title = s2.split(",");
            String[] image = s3.split(",");
            String[] date = s4.split(",");

            ids = Arrays.copyOfRange(id, 1, id.length);
            titles = Arrays.copyOfRange(title, 1, title.length);
            images = Arrays.copyOfRange(image, 1, image.length);
            dates = Arrays.copyOfRange(date,1,date.length);

            results = new ArrayList<>(ids.length);

            for (int i = 0; i < ids.length; i++) {
                final SearchResult result = new SearchResult();

                result.setId(ids[i]);
                result.setMediaName(titles[i]);
                result.setMediaImage(images[i]);
                result.setMediaType("Movie");
                result.setMediaDate(dates[i]);
                results.add(result);
            }
        }

        progressBar = (ProgressBar) getActivity().findViewById(R.id.reminders_progress_bar);

        ReminderAdapter adapter = new ReminderAdapter(getActivity(), results,progressBar);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

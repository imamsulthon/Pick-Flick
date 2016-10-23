package sadboy.pickflick.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MovieDetail;
import sadboy.pickflick.activities.TVDetail;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.models.SearchResult;

/**
 * Created by Varun Kumar on 8/22/2016.
 */
public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> implements AsyncDownloader.JsonDataSetter {
    private List<SearchResult> results;
    private Context context;
    private String media_type;
    private String media_id;
    private ProgressBar progressBar;
    private Intent i;
    AsyncDownloader downloader1;

    public BookmarksAdapter(Context ctx, List<SearchResult> results, ProgressBar progressBar) {
        this.context = ctx;
        this.results = results;
        this.progressBar = progressBar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.media_name);
            image = (ImageView) itemView.findViewById(R.id.media_image);
            card = (CardView) itemView.findViewById(R.id.search_card);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.bookmark_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SearchResult result = results.get(position);
        final int pos = position;

        holder.name.setText(result.getMediaName());
        media_type = result.getMediaType();
        media_id = result.getId();

        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w185" + result.getMediaImage())
                .fit()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media_type.equals("Movie")) {
                    i = new Intent(context, MovieDetail.class);
                } else if (media_type.equals("TV")) {
                    i = new Intent(context, TVDetail.class);
                }
                setUrls(result.getId());
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                        .setTitle("Delete this from your bookmarks?")
                        .setMessage("You may need to bookmark it again")
                        .setPositiveButton("DELETE",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String type= results.get(0).getMediaType();
                                        results.remove(pos);

                                        if (type.equals("TV")) {
                                            SharedPreferences write = context.getApplicationContext().getSharedPreferences("Bookmarks_TV", 0);
                                            SharedPreferences.Editor editor = write.edit();
                                            editor.clear();
                                            editor.apply();

                                            String s1 = "", s2 = "", s3 = "";

                                            for (int i = 0; i < results.size(); i++) {
                                                s1 = s1 + "," + results.get(i).getId();
                                                s2 = s2 + "," + results.get(i).getMediaName();
                                                s3 = s3 + "," + results.get(i).getMediaImage();
                                            }

                                            editor.putString("TVID", s1);
                                            editor.putString("TVTitle", s2);
                                            editor.putString("TVImage", s3);

                                            editor.apply();
                                        } else if (type.equals("Movie")) {
                                            SharedPreferences write = context.getApplicationContext().getSharedPreferences("Bookmarks_Movie", 0);
                                            SharedPreferences.Editor editor = write.edit();
                                            editor.clear();
                                            editor.apply();

                                            String s1 = "", s2 = "", s3 = "";

                                            for (int i = 0; i < results.size(); i++) {

                                                s1 = s1 + "," + results.get(i).getId();
                                                s2 = s2 + "," + results.get(i).getMediaName();
                                                s3 = s3 + "," + results.get(i).getMediaImage();
                                            }

                                            editor.putString("MovieID", s1);
                                            editor.putString("MovieTitle", s2);
                                            editor.putString("MovieImage", s3);

                                            editor.apply();
                                        }


                                        notifyItemRemoved(pos);
                                        notifyItemRangeChanged(pos, results.size());


                                    }

                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create().show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (results == null) {
            return 0;
        }
        return results.size();
    }

    public void setUrls(String id) {

        DatabaseUrl url = DatabaseUrl.getInstance();
        String detail = null;
        if (media_type.equals("Movie")) {
            detail = url.getMovieDetail(id);
        } else if (media_type.equals("TV")) {
            detail = url.getTVDetail(id);
        }

        if (detail != null) {
            downloader1 = new AsyncDownloader(this);
            downloader1.execute(detail);
        }

    }

    @Override
    public void setJsonData(String jsonData) {
        if (media_type.equals("Movie")) {
            i.putExtra("movieJsonData", jsonData);
        } else if (media_type.equals("TV")) {
            i.putExtra("tvJsonData", jsonData);
        }
        downloader1.cancel(true);
        context.startActivity(i);
    }
}

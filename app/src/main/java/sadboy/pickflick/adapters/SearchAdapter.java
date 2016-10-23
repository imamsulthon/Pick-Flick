package sadboy.pickflick.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MovieDetail;
import sadboy.pickflick.activities.PersonDetail;
import sadboy.pickflick.activities.TVDetail;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.models.Film;
import sadboy.pickflick.models.SearchResult;

/**
 * Created by Varun Kumar on 8/19/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements AsyncDownloader.JsonDataSetter {

    private List<SearchResult> results;
    private Context context;
    private Intent i;
    private String str;
    AsyncDownloader downloader1;

    public SearchAdapter(Context ctx, List<SearchResult> results) {
        this.context = ctx;
        this.results = results;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView type;
        public ImageView image;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.media_name);
            type = (TextView) itemView.findViewById(R.id.media_type);
            image = (ImageView) itemView.findViewById(R.id.media_image);
            card = (CardView) itemView.findViewById(R.id.search_card);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SearchResult result = results.get(position);

        holder.name.setText(result.getMediaName());
        if (result.getMediaType() != "1") {
            holder.type.setText(result.getMediaType());
        }
        else
        {
            result.setMediaType("Movie");
        }
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w185" + result.getMediaImage())
                .fit()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                YoYo.with(Techniques.Pulse)
                        .duration(500)
                        .playOn(holder.card);

                if (result.getMediaType().equals("Movie")) {
                    i = new Intent(context, MovieDetail.class);
                } else if (result.getMediaType().equals("TV")) {
                    i = new Intent(context, TVDetail.class);
                } else if (result.getMediaType().equals("Person")) {
                    i = new Intent(context, PersonDetail.class);
                }
                str = result.getMediaType();
                setUrls(result.getId());
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

        if (str.equals("Movie")) {
            detail = url.getMovieDetail(id);
        } else if (str.equals("TV")) {
            detail = url.getTVDetail(id);
        } else if (str.equals("Person")) {
            detail = url.getPersonDetail(id);
        }

        if (detail != null) {
            downloader1 = new AsyncDownloader(this);
            downloader1.execute(detail);
        }

    }

    @Override
    public void setJsonData(String jsonData) {

        if (str.equals("Movie")) {
            i.putExtra("movieJsonData", jsonData);
        } else if (str.equals("TV")) {
            i.putExtra("tvJsonData", jsonData);
        } else if (str.equals("Person")) {
            i.putExtra("personJsonData", jsonData);
        }

        downloader1.cancel(true);
        context.startActivity(i);
    }

}

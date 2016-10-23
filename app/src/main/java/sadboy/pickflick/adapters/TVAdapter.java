package sadboy.pickflick.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MovieDetail;
import sadboy.pickflick.activities.TVDetail;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.models.Film;
import sadboy.pickflick.models.Television;

/**
 * Created by Varun Kumar on 7/29/2016.
 */
public class TVAdapter extends RecyclerView.Adapter<TVAdapter.ViewHolder> implements AsyncDownloader.JsonDataSetter {

    private List<Television> tvs;
    private Context context;
    private Intent i;
    private ProgressBar progressBar;
    AsyncDownloader downloader1;

    public TVAdapter(Context ctx, List<Television> tvsArray, ProgressBar progressBar) {
        this.context = ctx;
        this.tvs = tvsArray;
        this.progressBar = progressBar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public ImageView posterImage;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            posterImage = (ImageView) itemView.findViewById(R.id.poster);
            cardView = (CardView) itemView.findViewById(R.id.movie_card);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Television tv = tvs.get(position);

        holder.tvTitle.setText(tv.getTitle());
        holder.tvTitle.setSelected(true);
        holder.tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w342" + tv.getPosterPath())
                .fit()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.posterImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                YoYo.with(Techniques.Pulse)
                        .duration(500)
                        .playOn(holder.cardView);
                i = new Intent(context, TVDetail.class);
                setUrls(tv.getId());
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (tvs == null) {
            return 0;
        }
        return tvs.size();
    }

    public void setUrls(String id) {

        DatabaseUrl url = DatabaseUrl.getInstance();

        String tvDetail = url.getTVDetail(id);
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(tvDetail);

    }

    @Override
    public void setJsonData(String jsonData) {
        i.putExtra("tvJsonData", jsonData);
        downloader1.cancel(true);
        context.startActivity(i);
    }
}

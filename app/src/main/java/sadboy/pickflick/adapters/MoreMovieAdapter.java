package sadboy.pickflick.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MovieDetail;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.models.Film;

/**
 * Created by Varun Kumar on 10/21/2016.
 */

public class MoreMovieAdapter extends RecyclerView.Adapter<MoreMovieAdapter.ViewHolder> implements AsyncDownloader.JsonDataSetter {

    private List<Film> films;
    private Context context;
    private Intent i;
    private ProgressBar progressBar;
    private AsyncDownloader downloader1;

    public MoreMovieAdapter(Context ctx, List<Film> celebsArray, ProgressBar progressBar) {
        this.context = ctx;
        this.films = celebsArray;
        this.progressBar = progressBar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView fil;
        public ImageView image;
        public LinearLayout face;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.poster);
            fil = (TextView) itemView.findViewById(R.id.overview);
            face = (LinearLayout) itemView.findViewById(R.id.poster_layout);
            cardView = (CardView) itemView.findViewById(R.id.search_card);
        }
    }

    @Override
    public MoreMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.more_card, parent, false);

        return new MoreMovieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoreMovieAdapter.ViewHolder holder, int position) {
        final Film film = films.get(position);

        holder.name.setText(film.getTitle());
        holder.fil.setText(film.getOverview());
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w342" + film.getPosterPath())
                .error(R.drawable.placeholder)
                .into(holder.image);

        holder.face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                YoYo.with(Techniques.Pulse)
                        .duration(500)
                        .playOn(holder.cardView);
                i = new Intent(context, MovieDetail.class);
                setUrls(film.getId());
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (films == null) {
            return 0;
        }
        return films.size();
    }

    public void setUrls(String id) {

        DatabaseUrl url = DatabaseUrl.getInstance();

        String movieDetail = url.getMovieDetail(id);
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(movieDetail);

    }

    @Override
    public void setJsonData(String jsonData) {
        i.putExtra("movieJsonData", jsonData);
        downloader1.cancel(true);
        context.startActivity(i);
    }
}

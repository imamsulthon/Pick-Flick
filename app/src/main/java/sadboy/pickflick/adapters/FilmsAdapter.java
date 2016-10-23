package sadboy.pickflick.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MovieDetail;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.AsyncDownloader.JsonDataSetter;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.models.Film;

/**
 * Created by Varun Kumar on 7/20/2016.
 */
public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.ViewHolder> implements JsonDataSetter {

    private List<Film> films;
    private Context context;
    private ProgressBar progressBar;
    private Intent i;
    AsyncDownloader downloader1;

    public FilmsAdapter(Context ctx, List<Film> filmsArray, ProgressBar progressBar) {
        this.context = ctx;
        this.films = filmsArray;
        this.progressBar = progressBar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView filmTitle;
        public ImageView posterImage;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            filmTitle = (TextView) itemView.findViewById(R.id.title);
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
        final Film film = films.get(position);

        holder.filmTitle.setText(film.getTitle());
        holder.filmTitle.setSelected(true);
        holder.filmTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w342" + film.getPosterPath())
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

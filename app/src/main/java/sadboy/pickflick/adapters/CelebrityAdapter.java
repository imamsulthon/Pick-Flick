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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.util.List;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MovieDetail;
import sadboy.pickflick.activities.PersonDetail;
import sadboy.pickflick.http.AsyncDownloader;
import sadboy.pickflick.http.DatabaseUrl;
import sadboy.pickflick.models.Person;

/**
 * Created by Varun Kumar on 7/30/2016.
 */
public class CelebrityAdapter extends RecyclerView.Adapter<CelebrityAdapter.ViewHolder> implements AsyncDownloader.JsonDataSetter {

    private List<Person> celebs;
    private Context context;
    private Intent i;
    private ProgressBar progressBar;
    private AsyncDownloader downloader1;

    public CelebrityAdapter(Context ctx, List<Person> celebsArray, ProgressBar progressBar) {
        this.context = ctx;
        this.celebs = celebsArray;
        this.progressBar = progressBar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView film;
        public ImageView image;
        public LinearLayout face;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.celeb_name);
            image = (ImageView) itemView.findViewById(R.id.celeb_image);
            film = (TextView) itemView.findViewById(R.id.celeb_credits);
            face = (LinearLayout)itemView.findViewById(R.id.celeb_face);
            cardView = (CardView)itemView.findViewById(R.id.search_card);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.celebrity_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Person celeb = celebs.get(position);

        holder.name.setText(celeb.getName());
        holder.film.setText(celeb.getKnownForFilm());
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w185" + celeb.getProfilePath())
                .error(R.drawable.person)
                .into(holder.image);

        holder.face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               YoYo.with(Techniques.Pulse)
                        .duration(500)
                        .playOn(holder.cardView);
               i = new Intent(context, PersonDetail.class);
               setUrls(celeb.getId());
               progressBar.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (celebs == null) {
            return 0;
        }
        return celebs.size();
    }

    public void setUrls(String id) {

        DatabaseUrl url = DatabaseUrl.getInstance();

        String personDetail = url.getPersonDetail(id);
        downloader1 = new AsyncDownloader(this);
        downloader1.execute(personDetail);

    }

    @Override
    public void setJsonData(String jsonData) {
        i.putExtra("personJsonData", jsonData);
        downloader1.cancel(true);
        context.startActivity(i);
    }
}

package sadboy.pickflick.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import sadboy.pickflick.R;

/**
 * Created by Varun Kumar on 8/6/2016.
 */
public class PersonImageAdapter extends RecyclerView.Adapter<PersonImageAdapter.ViewHolder>  {

    private List<String> images;
    private Context context;

    public PersonImageAdapter(Context ctx, List<String> images) {
        this.context = ctx;
        this.images = images;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public LinearLayout face;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.celeb_card_image);
            face = (LinearLayout)itemView.findViewById(R.id.celeb_image_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.celeb_image_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String path = images.get(position);

        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w342" + path)
                .fit()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        if (images == null) {
            return 0;
        }
        return images.size();
    }
}

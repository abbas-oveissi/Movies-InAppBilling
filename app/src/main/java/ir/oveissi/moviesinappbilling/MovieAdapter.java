package ir.oveissi.moviesinappbilling;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.oveissi.moviesinappbilling.data.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv, directorTv, durationTv, ratingTv;
        RatingBar ratingRb;
        ImageView posterImg;
        private Movie movie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.movie_title_tv);
            directorTv = itemView.findViewById(R.id.movie_director_tv);
            durationTv = itemView.findViewById(R.id.movie_duration_tv);
            ratingTv = itemView.findViewById(R.id.movie_rate_tv);
            ratingRb = itemView.findViewById(R.id.movie_rate_rb);
            posterImg = itemView.findViewById(R.id.movie_poster_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Movie selectedMovie = MovieViewHolder.this.movie;
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.KEY_TITLE, selectedMovie.getTitle());
                    intent.putExtra(DetailActivity.KEY_DESCRIPTION, selectedMovie.getDescription());
                    intent.putExtra(DetailActivity.KEY_POSTER, selectedMovie.getPoster());
                    intent.putExtra(DetailActivity.KEY_RATE, selectedMovie.getRating());
                    intent.putExtra(DetailActivity.KEY_SCREENSHOT, selectedMovie.getScreenshot());
                    view.getContext().startActivity(intent);
                }
            });
        }

        public void bind(Movie movie) {
            this.movie = movie;
            titleTv.setText(movie.getTitle());
            directorTv.setText(directorTv
                    .getContext()
                    .getResources()
                    .getString(R.string.main_director_title, movie.getDirector()));
            durationTv.setText(durationTv
                    .getContext()
                    .getResources()
                    .getString(R.string.main_duration_title, movie.getDuration()));
            ratingTv.setText(String.valueOf(movie.getRating()));
            ratingRb.setRating(movie.getRating());
            Picasso.get().load(movie.getPoster()).into(posterImg);
        }

    }
}

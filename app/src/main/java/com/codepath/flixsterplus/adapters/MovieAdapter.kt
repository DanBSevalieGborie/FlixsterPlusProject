package com.codepath.flixsterplus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codepath.flixsterplus.R
import com.codepath.flixsterplus.databinding.ItemMovieBinding
import com.codepath.flixsterplus.models.Movie
import java.util.Locale

/**
 * Horizontal carousel of trending movies (the required RecyclerView).
 *
 * The click listener hands back the poster ImageView as well as the movie so
 * MainActivity can use it as the shared element for the transition.
 */
class MovieAdapter(
    private val movies: MutableList<Movie> = mutableListOf(),
    private val onClick: (Movie, ImageView) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    fun submit(items: List<Movie>) {
        movies.clear()
        movies.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) =
        holder.bind(movies[position])

    override fun getItemCount(): Int = movies.size

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieRating.text = String.format(Locale.US, "★ %.1f", movie.voteAverage)
            binding.movieYear.text = movie.releaseDate.take(4)

            // Stretch feature: rounded corners via a Glide transformation.
            val radius = binding.root.resources
                .getDimensionPixelSize(R.dimen.image_corner_radius)
            Glide.with(binding.moviePoster)
                .load(movie.listImageUrl)
                .transform(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(binding.moviePoster)

            // Unique name per row so the framework can match the two views.
            ViewCompat.setTransitionName(binding.moviePoster, "poster_movie_${movie.id}")

            binding.root.setOnClickListener { onClick(movie, binding.moviePoster) }
        }
    }
}

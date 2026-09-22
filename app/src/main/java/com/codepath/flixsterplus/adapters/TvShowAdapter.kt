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
import com.codepath.flixsterplus.databinding.ItemTvShowBinding
import com.codepath.flixsterplus.models.TvShow
import java.util.Locale

/**
 * Stretch feature: a second RecyclerView, backed by a second API call
 * (tv/popular), that lets the user browse a different kind of data.
 */
class TvShowAdapter(
    private val shows: MutableList<TvShow> = mutableListOf(),
    private val onClick: (TvShow, ImageView) -> Unit
) : RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {

    fun submit(items: List<TvShow>) {
        shows.clear()
        shows.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val binding = ItemTvShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TvShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) =
        holder.bind(shows[position])

    override fun getItemCount(): Int = shows.size

    inner class TvShowViewHolder(
        private val binding: ItemTvShowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(show: TvShow) {
            binding.tvName.text = show.name
            binding.tvRating.text = String.format(Locale.US, "★ %.1f", show.voteAverage)
            binding.tvFirstAired.text = show.firstAirDate.take(4)

            val radius = binding.root.resources
                .getDimensionPixelSize(R.dimen.image_corner_radius)
            Glide.with(binding.tvPoster)
                .load(show.listImageUrl)
                .transform(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(binding.tvPoster)

            ViewCompat.setTransitionName(binding.tvPoster, "poster_tv_${show.id}")

            binding.root.setOnClickListener { onClick(show, binding.tvPoster) }
        }
    }
}

package com.codepath.flixsterplus

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codepath.flixsterplus.databinding.ActivityDetailBinding
import com.codepath.flixsterplus.models.Movie
import com.codepath.flixsterplus.models.TmdbImage
import com.codepath.flixsterplus.models.TvShow
import java.util.Locale

/**
 * Details page (required feature).
 *
 * Reached with an Intent from MainActivity and shows several pieces of data
 * that are NOT on the main list: the overview, the backdrop image, the number
 * of votes, the popularity score and the original language, plus the release /
 * first-air date.
 *
 * One activity serves both models so movies and TV shows share the layout.
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
        const val EXTRA_TV_SHOW = "extra_tv_show"
        const val EXTRA_TRANSITION_NAME = "extra_transition_name"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Reconnect the shared element so the poster animates in from the list.
        intent.getStringExtra(EXTRA_TRANSITION_NAME)?.let {
            ViewCompat.setTransitionName(binding.detailPoster, it)
        }

        binding.backButton.setOnClickListener { ActivityCompat.finishAfterTransition(this) }

        when (val item = parcelable()) {
            is Movie -> bindMovie(item)
            is TvShow -> bindTvShow(item)
            else -> finish()
        }
    }

    private fun parcelable(): Parcelable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_MOVIE, Movie::class.java)
                ?: intent.getParcelableExtra(EXTRA_TV_SHOW, TvShow::class.java)
        } else {
            @Suppress("DEPRECATION")
            (intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
                ?: intent.getParcelableExtra<TvShow>(EXTRA_TV_SHOW))
        }
    }

    private fun bindMovie(movie: Movie) {
        binding.detailKind.text = getString(R.string.kind_movie)
        binding.detailTitle.text = movie.title
        binding.detailDateLabel.text = getString(R.string.label_released)
        binding.detailDateValue.text = movie.releaseDate.ifBlank { getString(R.string.value_unknown) }

        fillShared(
            overview = movie.overview,
            voteAverage = movie.voteAverage,
            voteCount = movie.voteCount,
            popularity = movie.popularity,
            language = movie.originalLanguage,
            posterUrl = movie.listImageUrl,
            backdropUrl = TmdbImage.backdrop(movie.backdropPath)
        )
    }

    private fun bindTvShow(show: TvShow) {
        binding.detailKind.text = getString(R.string.kind_tv)
        binding.detailTitle.text = show.name
        binding.detailDateLabel.text = getString(R.string.label_first_aired)
        binding.detailDateValue.text = show.firstAirDate.ifBlank { getString(R.string.value_unknown) }

        fillShared(
            overview = show.overview,
            voteAverage = show.voteAverage,
            voteCount = show.voteCount,
            popularity = show.popularity,
            language = show.originalLanguage,
            posterUrl = show.listImageUrl,
            backdropUrl = TmdbImage.backdrop(show.backdropPath)
        )
    }

    private fun fillShared(
        overview: String,
        voteAverage: Double,
        voteCount: Int,
        popularity: Double,
        language: String,
        posterUrl: String?,
        backdropUrl: String?
    ) {
        binding.detailOverview.text =
            overview.ifBlank { getString(R.string.value_no_overview) }
        binding.detailRatingValue.text = String.format(Locale.US, "%.1f / 10", voteAverage)
        binding.detailVotesValue.text = String.format(Locale.US, "%,d", voteCount)
        binding.detailPopularityValue.text = String.format(Locale.US, "%.0f", popularity)
        binding.detailLanguageValue.text = language.uppercase(Locale.US)
            .ifBlank { getString(R.string.value_unknown) }

        val radius = resources.getDimensionPixelSize(R.dimen.image_corner_radius)
        Glide.with(this)
            .load(posterUrl)
            .transform(CenterCrop(), RoundedCorners(radius))
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .into(binding.detailPoster)

        Glide.with(this)
            .load(backdropUrl ?: posterUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder_poster)
            .into(binding.detailBackdrop)
    }
}

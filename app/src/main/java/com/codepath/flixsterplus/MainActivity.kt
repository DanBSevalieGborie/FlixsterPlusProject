package com.codepath.flixsterplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.flixsterplus.adapters.MovieAdapter
import com.codepath.flixsterplus.adapters.TvShowAdapter
import com.codepath.flixsterplus.databinding.ActivityMainBinding
import com.codepath.flixsterplus.models.Movie
import com.codepath.flixsterplus.models.TvShow
import com.codepath.flixsterplus.network.TmdbClient
import kotlinx.coroutines.launch

/**
 * Main screen.
 *
 * Required feature : a RecyclerView of the `trending/movie/week` endpoint.
 * Stretch feature  : a second API call (`tv/popular`) in a second RecyclerView
 *                    so the user can browse a different kind of data.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val movieAdapter = MovieAdapter { movie, poster -> openMovie(movie, poster) }
    private val tvAdapter = TvShowAdapter { show, poster -> openTvShow(show, poster) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.movieList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = movieAdapter
            setHasFixedSize(true)
        }

        binding.tvList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tvAdapter
            isNestedScrollingEnabled = false
        }

        binding.swipeRefresh.setOnRefreshListener { loadData() }
        binding.retryButton.setOnClickListener { loadData() }

        loadData()
    }

    /** Runs both network calls and fills the two lists. */
    private fun loadData() {
        binding.errorGroup.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            try {
                val movies = TmdbClient.fetchTrendingMovies()
                val shows = TmdbClient.fetchPopularTvShows()

                movieAdapter.submit(movies)
                tvAdapter.submit(shows)

                binding.content.visibility = View.VISIBLE
                binding.errorGroup.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
                if (movieAdapter.itemCount == 0) {
                    binding.content.visibility = View.GONE
                    binding.errorGroup.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@MainActivity, R.string.error_network, Toast.LENGTH_SHORT).show()
                }
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    /** Required feature: pass the tapped entry to the details page with an Intent. */
    private fun openMovie(movie: Movie, poster: ImageView) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MOVIE, movie)
            putExtra(DetailActivity.EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(poster))
        }
        startActivity(intent, sharedElementOptions(poster))
    }

    private fun openTvShow(show: TvShow, poster: ImageView) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_TV_SHOW, show)
            putExtra(DetailActivity.EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(poster))
        }
        startActivity(intent, sharedElementOptions(poster))
    }

    /** Stretch feature: the poster animates from the list into the details page. */
    private fun sharedElementOptions(poster: ImageView): Bundle? {
        val name = ViewCompat.getTransitionName(poster) ?: return null
        return ActivityOptionsCompat
            .makeSceneTransitionAnimation(this, poster, name)
            .toBundle()
    }
}

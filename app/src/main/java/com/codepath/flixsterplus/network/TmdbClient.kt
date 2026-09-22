package com.codepath.flixsterplus.network

import com.codepath.flixsterplus.models.Movie
import com.codepath.flixsterplus.models.TvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Tiny networking layer around the two TMDB endpoints this app uses.
 *
 *  - trending/movie/week  -> the main required RecyclerView
 *  - tv/popular           -> the stretch-feature second RecyclerView
 *
 * Both calls run on Dispatchers.IO so the main thread is never blocked, and
 * the raw JSON is handed to the model classes to be parsed.
 */
object TmdbClient {

    // Provided by the CodePath assignment.
    private const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
    private const val BASE_URL = "https://api.themoviedb.org/3"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /** GET /trending/movie/week */
    suspend fun fetchTrendingMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val json = get("$BASE_URL/trending/movie/week?api_key=$API_KEY&language=en-US")
        Movie.fromJsonArray(json.getJSONArray("results"))
    }

    /** GET /tv/popular */
    suspend fun fetchPopularTvShows(): List<TvShow> = withContext(Dispatchers.IO) {
        val json = get("$BASE_URL/tv/popular?api_key=$API_KEY&language=en-US&page=1")
        TvShow.fromJsonArray(json.getJSONArray("results"))
    }

    private fun get(url: String): JSONObject {
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (!response.isSuccessful || body.isNullOrBlank()) {
                throw IOException("TMDB request failed (${response.code})")
            }
            return JSONObject(body)
        }
    }
}

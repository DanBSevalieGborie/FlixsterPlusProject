package com.codepath.flixsterplus.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

/**
 * One entry from the TMDB `trending/movie/week` endpoint.
 *
 * Parcelable so the whole object can ride along inside an Intent to the
 * details screen instead of passing a dozen separate extras.
 */
@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    override val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    override val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val originalLanguage: String
) : Parcelable, MediaItem {

    override val listTitle: String get() = title
    override val listImageUrl: String? get() = TmdbImage.poster(posterPath)

    companion object {
        fun fromJson(json: JSONObject): Movie = Movie(
            id = json.optInt("id"),
            title = json.optString("title").ifBlank { json.optString("name") },
            overview = json.optString("overview"),
            posterPath = json.optString("poster_path").takeIf { it.isNotBlank() && it != "null" },
            backdropPath = json.optString("backdrop_path").takeIf { it.isNotBlank() && it != "null" },
            releaseDate = json.optString("release_date"),
            voteAverage = json.optDouble("vote_average", 0.0),
            voteCount = json.optInt("vote_count"),
            popularity = json.optDouble("popularity", 0.0),
            originalLanguage = json.optString("original_language")
        )

        fun fromJsonArray(array: JSONArray): List<Movie> =
            (0 until array.length()).map { fromJson(array.getJSONObject(it)) }
    }
}

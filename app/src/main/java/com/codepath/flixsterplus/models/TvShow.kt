package com.codepath.flixsterplus.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

/**
 * One entry from the TMDB `tv/popular` endpoint.
 *
 * This is the second model / second API call that powers the stretch-feature
 * RecyclerView on the main screen.
 */
@Parcelize
data class TvShow(
    val id: Int,
    val name: String,
    override val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: String,
    override val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val originalLanguage: String,
    val originCountry: String
) : Parcelable, MediaItem {

    override val listTitle: String get() = name
    override val listImageUrl: String? get() = TmdbImage.poster(posterPath)

    companion object {
        fun fromJson(json: JSONObject): TvShow {
            val countries = json.optJSONArray("origin_country")
            val country = if (countries != null && countries.length() > 0) {
                (0 until countries.length()).joinToString(", ") { countries.optString(it) }
            } else {
                ""
            }
            return TvShow(
                id = json.optInt("id"),
                name = json.optString("name"),
                overview = json.optString("overview"),
                posterPath = json.optString("poster_path").takeIf { it.isNotBlank() && it != "null" },
                backdropPath = json.optString("backdrop_path").takeIf { it.isNotBlank() && it != "null" },
                firstAirDate = json.optString("first_air_date"),
                voteAverage = json.optDouble("vote_average", 0.0),
                voteCount = json.optInt("vote_count"),
                popularity = json.optDouble("popularity", 0.0),
                originalLanguage = json.optString("original_language"),
                originCountry = country
            )
        }

        fun fromJsonArray(array: JSONArray): List<TvShow> =
            (0 until array.length()).map { fromJson(array.getJSONObject(it)) }
    }
}

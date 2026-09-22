package com.codepath.flixsterplus.models

/**
 * Small shared contract so both RecyclerView adapters and the single
 * DetailActivity can treat a Movie and a TvShow the same way.
 */
interface MediaItem {
    val listTitle: String
    val listImageUrl: String?
    val overview: String
    val voteAverage: Double
}

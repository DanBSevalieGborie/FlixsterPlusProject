package com.codepath.flixsterplus.models

/** Builds the full CDN urls TMDB expects from the relative paths in the JSON. */
object TmdbImage {
    private const val BASE = "https://image.tmdb.org/t/p/"

    fun poster(path: String?): String? = path?.let { "${BASE}w500$it" }

    fun backdrop(path: String?): String? = path?.let { "${BASE}w780$it" }
}

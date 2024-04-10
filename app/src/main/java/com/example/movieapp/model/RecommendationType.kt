package com.example.movieapp.model

enum class RecommendationType(val type: String, val tabName: String) {
    NOW_PLAYING("now_playing", "NOW PLAYING"),
    POPULAR("popular", "POPULAR"),
    TOP_RATED("top_rated", "TOP RATED"),
    UPCOMING("upcoming", "UPCOMING")
}
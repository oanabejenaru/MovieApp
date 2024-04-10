package com.example.movieapp.model

enum class SortMode(val sortName: String) {
    RATING_ASCENDING("Rating ascending"),
    RATING_DESCENDING("Rating descending"),
    RELEASE_ASCENDING("Release date ascending"),
    RELEASE_DESCENDING("Release date descending")
}
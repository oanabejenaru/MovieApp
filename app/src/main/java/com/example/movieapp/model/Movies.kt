package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class MoviesApiResponse(
    val results: List<MovieData>
)

data class MovieData(
    val id: Int?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val yearOfRelease: String?,
    @SerializedName("vote_average")
    val averageRating: Float?
)
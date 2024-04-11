package com.example.movieapp.model

import com.example.movieapp.model.data.FavoriteMovie
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
) {
    companion object {
        fun fromFavoriteMovie(favoriteMovie: FavoriteMovie) =
            MovieData(
                id = favoriteMovie.apiId,
                posterPath = favoriteMovie.posterPath,
                yearOfRelease = favoriteMovie.yearOfRelease,
                averageRating = favoriteMovie.averageRating
            )
    }
}
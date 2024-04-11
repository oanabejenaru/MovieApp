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
    val dateOfRelease: String?,
    @SerializedName("vote_average")
    val averageRating: Float?
) {
    companion object {
        fun fromFavoriteMovie(favoriteMovie: FavoriteMovie) =
            MovieData(
                id = favoriteMovie.apiId,
                posterPath = favoriteMovie.posterPath,
                dateOfRelease = favoriteMovie.dateOfRelease,
                averageRating = favoriteMovie.averageRating
            )
    }
}

data class MovieDetailData(
    val id: Int?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    val title: String?,
    val tagline: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Float?,
    @SerializedName("vote_count")
    val voteCount:Int?,
    val genres: List<Genre>?,
    val overview: String?
)

data class Genre(
    val id: Int?,
    val name: String?
)
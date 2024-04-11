package com.example.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.MovieDetailData
import com.example.movieapp.util.Constants

@Entity(tableName = Constants.FAVORITE_MOVIES_TABLE)
data class FavoriteMovie(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val apiId: Int?,
    val posterPath: String?,
    val dateOfRelease: String?,
    val averageRating: Float?,
    val timeStamp: Long?
) {
    companion object {
        fun fromMovieData(movieData: MovieData) =
            FavoriteMovie(
                id = 0,
                apiId = movieData.id,
                posterPath = movieData.posterPath,
                dateOfRelease = movieData.dateOfRelease,
                averageRating = movieData.averageRating,
                timeStamp = System.currentTimeMillis()
            )

        fun fromMovieDetailData(movieDetailData: MovieDetailData) =
            FavoriteMovie(
                id = 0,
                apiId = movieDetailData.id,
                posterPath = movieDetailData.posterPath,
                dateOfRelease = movieDetailData.releaseDate,
                averageRating = movieDetailData.voteAverage,
                timeStamp = System.currentTimeMillis()
            )
    }
}

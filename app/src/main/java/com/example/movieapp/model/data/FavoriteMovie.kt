package com.example.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieapp.model.MovieData
import com.example.movieapp.util.Constants

@Entity(tableName = Constants.FAVORITE_MOVIES_TABLE)
data class FavoriteMovie(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val apiId: Int?,
    val posterPath: String?,
    val yearOfRelease: String?,
    val averageRating: Float?,
    val timeStamp: Long?
) {
    companion object {
        fun fromMovieData(movieData: MovieData) =
            FavoriteMovie(
                id = 0,
                apiId = movieData.id,
                posterPath = movieData.posterPath,
                yearOfRelease = movieData.yearOfRelease,
                averageRating = movieData.averageRating,
                timeStamp = System.currentTimeMillis()
            )
    }
}

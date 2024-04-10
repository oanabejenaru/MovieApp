package com.example.movieapp.model.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMoviesDao {
    @Query("SELECT * FROM ${Constants.FAVORITE_MOVIES_TABLE} ORDER BY timeStamp DESC")
    fun getAllFavoriteMovies() : Flow<List<FavoriteMovie>>

    @Query("SELECT apiId FROM ${Constants.FAVORITE_MOVIES_TABLE}")
    fun getAllFavoriteMoviesIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavoriteMovie(favoriteMovie : FavoriteMovie)

    @Query("DELETE FROM ${Constants.FAVORITE_MOVIES_TABLE} WHERE apiId = :movieId ")
    suspend fun deleteFavoriteMovie(movieId: Int)
}
package com.example.movieapp.repository

import com.example.movieapp.model.data.FavoriteMovie
import com.example.movieapp.model.data.FavoriteMoviesDao
import kotlinx.coroutines.flow.Flow

interface FavoriteMoviesDbRepo {
    suspend fun getAllFavoriteMovies() : Flow<List<FavoriteMovie>>
    suspend fun getAllFavoriteMoviesIds() : Flow<List<Int>>
    suspend fun addFavoriteMovie(favoriteMovie: FavoriteMovie)
    suspend fun removeFavoriteMovie(apiId: Int)
}

class FavoriteMoviesDbRepoImpl(
    private val favoriteMoviesDao: FavoriteMoviesDao
) : FavoriteMoviesDbRepo {
    override suspend fun getAllFavoriteMovies(): Flow<List<FavoriteMovie>> =
        favoriteMoviesDao.getAllFavoriteMovies()

    override suspend fun getAllFavoriteMoviesIds(): Flow<List<Int>> =
        favoriteMoviesDao.getAllFavoriteMoviesIds()

    override suspend fun addFavoriteMovie(favoriteMovie: FavoriteMovie) =
        favoriteMoviesDao.addFavoriteMovie(favoriteMovie)

    override suspend fun removeFavoriteMovie(apiId: Int) =
        favoriteMoviesDao.deleteFavoriteMovie(apiId)
}
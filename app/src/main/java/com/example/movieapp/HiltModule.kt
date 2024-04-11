package com.example.movieapp

import android.content.Context
import androidx.room.Room
import com.example.movieapp.model.api.ApiService
import com.example.movieapp.model.data.FavoriteMoviesDB
import com.example.movieapp.model.data.FavoriteMoviesDao
import com.example.movieapp.repository.FavoriteMoviesDbRepo
import com.example.movieapp.repository.FavoriteMoviesDbRepoImpl
import com.example.movieapp.repository.MoviesApiRepo
import com.example.movieapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun provideApiRepo() =
        MoviesApiRepo(ApiService.api)

    @Provides
    fun provideFavoriteMoviesDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            FavoriteMoviesDB::class.java,
            Constants.DB
        ).build()

    @Provides
    fun provideFavoriteMoviesDao(favoriteMoviesDb: FavoriteMoviesDB) =
        favoriteMoviesDb.favoriteMoviesDao()

    @Provides
    fun provideFavoriteMoviesDbRepo(
        favoriteMoviesDao: FavoriteMoviesDao
    ): FavoriteMoviesDbRepo =
        FavoriteMoviesDbRepoImpl(favoriteMoviesDao)
}
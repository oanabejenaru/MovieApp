package com.example.movieapp.model.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovie::class], version = 1, exportSchema = false)
abstract class FavoriteMoviesDB : RoomDatabase() {
    abstract fun favoriteMoviesDao(): FavoriteMoviesDao
}
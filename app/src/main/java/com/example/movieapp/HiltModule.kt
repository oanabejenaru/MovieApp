package com.example.movieapp

import com.example.movieapp.model.api.ApiService
import com.example.movieapp.repository.MoviesApiRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun provideApiRepo() = MoviesApiRepo(ApiService.api)
}
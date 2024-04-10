package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.movieapp.model.api.NetworkResult
import com.example.movieapp.model.data.Movie
import com.example.movieapp.repository.MoviesApiRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val apiRepo: MoviesApiRepo
) : ViewModel() {

    val resultNowPlayingMovies = apiRepo.nowPlayingMovies
    val resultPopularMovies = apiRepo.popularMovies
    val resultTopRatedMovies = apiRepo.topRatedMovies
    val resultUpcomingMovies = apiRepo.upcomingMovies

    override fun onCleared() {
        super.onCleared()
        apiRepo.cancelJob()
    }
}
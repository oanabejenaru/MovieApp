package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.data.FavoriteMovie
import com.example.movieapp.repository.FavoriteMoviesDbRepo
import com.example.movieapp.repository.MoviesApiRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val apiRepo: MoviesApiRepo,
    private val favoriteMoviesDbRepo: FavoriteMoviesDbRepo
) : ViewModel() {

    val resultNowPlayingMovies = apiRepo.nowPlayingMovies
    val resultPopularMovies = apiRepo.popularMovies
    val resultTopRatedMovies = apiRepo.topRatedMovies
    val resultUpcomingMovies = apiRepo.upcomingMovies

    val favoriteMoviesIds = MutableStateFlow<List<Int>>(listOf())

    init {
        getFavoriteMoviesIds()
    }

    private fun getFavoriteMoviesIds() {
        viewModelScope.launch {
            favoriteMoviesDbRepo.getAllFavoriteMoviesIds().collect {
                favoriteMoviesIds.value = it
            }
        }
    }

    fun addToFavorites(movie : MovieData) {
        viewModelScope.launch {
            favoriteMoviesDbRepo.addFavoriteMovie(FavoriteMovie.fromMovieData(movie))
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            favoriteMoviesDbRepo.removeFavoriteMovie(movieId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        apiRepo.cancelJob()
    }
}
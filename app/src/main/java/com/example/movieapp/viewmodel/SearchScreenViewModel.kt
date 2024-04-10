package com.example.movieapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.data.FavoriteMovie
import com.example.movieapp.repository.FavoriteMoviesDbRepo
import com.example.movieapp.repository.MoviesApiRepo
import com.example.movieapp.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val apiRepo: MoviesApiRepo,
    private val favoriteMoviesDbRepo: FavoriteMoviesDbRepo
) : ViewModel() {

    val result = apiRepo.searchedMovies
    var searchAppBarState by mutableStateOf(SearchAppBarState.CLOSED)
        private set

    var searchTextState by mutableStateOf("")
        private set

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

    fun searchMovies(searchQuery: String) {
        apiRepo.searchMovies(searchQuery)
        searchAppBarState = SearchAppBarState.TRIGGERED
    }

    fun updateSearchAppBarState(newState: SearchAppBarState) {
        searchAppBarState = newState
    }

    fun updateSearchTextState(newText : String) {
        searchTextState = newText
    }

    override fun onCleared() {
        super.onCleared()
        apiRepo.cancelJob()
    }
}
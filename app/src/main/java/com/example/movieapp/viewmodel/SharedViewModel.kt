package com.example.movieapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.MovieDetailData
import com.example.movieapp.model.SortMode
import com.example.movieapp.model.api.RequestState
import com.example.movieapp.model.data.FavoriteMovie
import com.example.movieapp.repository.FavoriteMoviesDbRepo
import com.example.movieapp.repository.MoviesApiRepo
import com.example.movieapp.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val apiRepo: MoviesApiRepo,
    private val favoriteMoviesDbRepo: FavoriteMoviesDbRepo
) : ViewModel() {

    val resultSearchedMovies = apiRepo.searchedMovies
    val resultNowPlayingMovies = apiRepo.nowPlayingMovies
    val resultPopularMovies = apiRepo.popularMovies
    val resultTopRatedMovies = apiRepo.topRatedMovies
    val resultUpcomingMovies = apiRepo.upcomingMovies
    val resultMovieDetails = apiRepo.movieDetails

    var searchAppBarState by mutableStateOf(SearchAppBarState.CLOSED)
        private set

    var searchTextState by mutableStateOf("")
        private set

    var currentSortMode by mutableStateOf<SortMode?>(null)
        private set

    val favoriteMoviesIds = MutableStateFlow<List<Int>>(listOf())
    val resultFavoriteMovies =
        MutableStateFlow<RequestState<List<MovieData>>>(RequestState.Initial())

    init {
        getFavoriteMoviesIds()
        getFavoriteMoviesList()
    }

    fun getMovieDetails(movieId: Int?) {
        movieId?.let { id ->
            apiRepo.getMovieDetails(id)
        }
    }

    private fun getFavoriteMoviesIds() {
        viewModelScope.launch {
            favoriteMoviesDbRepo.getAllFavoriteMoviesIds().collect {
                favoriteMoviesIds.value = it
            }
        }
    }

    private fun getFavoriteMoviesList() {
        resultFavoriteMovies.value = RequestState.Loading()
        try {
            viewModelScope.launch {
                favoriteMoviesDbRepo.getAllFavoriteMovies().collect { list ->
                    val movieDataList = list.map { MovieData.fromFavoriteMovie(it) }
                    resultFavoriteMovies.value = RequestState.Success(movieDataList)
                }
            }
        } catch (e: Exception) {
            resultFavoriteMovies.value =
                RequestState.Error(e.message ?: "Cannot retrieve favorite movies")
        }
    }

    fun addToFavorites(movie: MovieData) {
        viewModelScope.launch {
            favoriteMoviesDbRepo.addFavoriteMovie(FavoriteMovie.fromMovieData(movie))
        }
    }

    fun addToFavorites(movie: MovieDetailData) {
        viewModelScope.launch {
            favoriteMoviesDbRepo.addFavoriteMovie(FavoriteMovie.fromMovieDetailData(movie))
        }
    }

    fun removeFromFavorites(movieId: Int?) {
        movieId?.let { id ->
            viewModelScope.launch {
                favoriteMoviesDbRepo.removeFavoriteMovie(id)
            }
        }
    }

    fun searchMovies(searchQuery: String) {
        apiRepo.searchMovies(searchQuery)
        searchAppBarState = SearchAppBarState.TRIGGERED
    }

    fun updateSearchAppBarState(newState: SearchAppBarState) {
        searchAppBarState = newState
    }

    fun updateSearchTextState(newText: String) {
        searchTextState = newText
    }

    fun applySortMode(sortMode: SortMode) {
        currentSortMode = sortMode
        apiRepo.applySortMode(sortMode)
    }

    override fun onCleared() {
        super.onCleared()
        apiRepo.cancelJobs()
    }
}
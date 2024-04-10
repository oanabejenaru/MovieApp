package com.example.movieapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.movieapp.repository.MoviesApiRepo
import com.example.movieapp.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val apiRepo: MoviesApiRepo
) : ViewModel() {

    val result = apiRepo.searchedMovies
    var searchAppBarState by mutableStateOf(SearchAppBarState.CLOSED)
        private set

    var searchTextState by mutableStateOf("")
        private set

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
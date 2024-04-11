package com.example.movieapp.repository

import android.util.Log
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.RecommendationType
import com.example.movieapp.model.api.MoviesApi
import com.example.movieapp.model.api.RequestState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesApiRepo(
    private val api: MoviesApi
) {
    private val _nowPlayingMovies = MutableStateFlow<RequestState<List<MovieData>>>(RequestState.Initial())
    val nowPlayingMovies: StateFlow<RequestState<List<MovieData>>> = _nowPlayingMovies

    private val _popularMovies = MutableStateFlow<RequestState<List<MovieData>>>(RequestState.Initial())
    val popularMovies: StateFlow<RequestState<List<MovieData>>> = _popularMovies

    private val _topRatedMovies = MutableStateFlow<RequestState<List<MovieData>>>(RequestState.Initial())
    val topRatedMovies: StateFlow<RequestState<List<MovieData>>> = _topRatedMovies

    private val _upcomingMovies = MutableStateFlow<RequestState<List<MovieData>>>(RequestState.Initial())
    val upcomingMovies: StateFlow<RequestState<List<MovieData>>> = _upcomingMovies

    private val _searchedMovies = MutableStateFlow<RequestState<List<MovieData>>>(RequestState.Initial())
    val searchedMovies: StateFlow<RequestState<List<MovieData>>> = _searchedMovies

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception : ${throwable.localizedMessage}")
    }

    private var job: Job? = null
    private var searchMoviesJob: Job? = null

    init {
        getAllMovies()
    }

    private fun getAllMovies() {
        _nowPlayingMovies.value = RequestState.Loading()
        _popularMovies.value = RequestState.Loading()
        _topRatedMovies.value = RequestState.Loading()
        _upcomingMovies.value = RequestState.Loading()

        job = CoroutineScope(Dispatchers.IO).launch {
            val nowPlayingDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.NOW_PLAYING.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _nowPlayingMovies.value = RequestState.Success(it.results)
                        }
                    } else {
                        _nowPlayingMovies.value = RequestState.Error(response.message())
                    }
                }
            }
            nowPlayingDeferred.await()
            val popularDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.POPULAR.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _popularMovies.value = RequestState.Success(it.results)
                        }
                    } else {
                        _popularMovies.value = RequestState.Error(response.message())
                    }
                }
            }
            popularDeferred.await()
            val topRatedDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.TOP_RATED.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _topRatedMovies.value = RequestState.Success(it.results)
                        }
                    } else {
                        _topRatedMovies.value = RequestState.Error(response.message())
                    }
                }
            }
            topRatedDeferred.await()
            val upcomingDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.UPCOMING.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _upcomingMovies.value = RequestState.Success(it.results)
                        }
                    } else {
                        _upcomingMovies.value = RequestState.Error(response.message())
                    }
                }
            }
            upcomingDeferred.await()
        }
    }

    fun searchMovies(query : String) {
        _searchedMovies.value = RequestState.Loading()
        searchMoviesJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = api.searchMovies(query)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _searchedMovies.value = RequestState.Success(it.results)
                    }
                } else {
                    _searchedMovies.value = RequestState.Error(response.message())
                }
            }
        }
    }

    private fun onError(message: String) {
        Log.e("MoviesApiRepo", message)
    }

    fun cancelJobs() {
        job?.cancel()
        searchMoviesJob?.cancel()
    }
}
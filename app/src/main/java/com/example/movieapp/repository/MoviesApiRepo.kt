package com.example.movieapp.repository

import com.example.movieapp.model.MovieResult
import com.example.movieapp.model.RecommendationType
import com.example.movieapp.model.api.MoviesApi
import com.example.movieapp.model.api.NetworkResult
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
    private val _nowPlayingMovies = MutableStateFlow<NetworkResult<List<MovieResult>>>(NetworkResult.Initial())
    val nowPlayingMovies: StateFlow<NetworkResult<List<MovieResult>>> = _nowPlayingMovies

    private val _popularMovies = MutableStateFlow<NetworkResult<List<MovieResult>>>(NetworkResult.Initial())
    val popularMovies: StateFlow<NetworkResult<List<MovieResult>>> = _popularMovies

    private val _topRatedMovies = MutableStateFlow<NetworkResult<List<MovieResult>>>(NetworkResult.Initial())
    val topRatedMovies: StateFlow<NetworkResult<List<MovieResult>>> = _topRatedMovies

    private val _upcomingMovies = MutableStateFlow<NetworkResult<List<MovieResult>>>(NetworkResult.Initial())
    val upcomingMovies: StateFlow<NetworkResult<List<MovieResult>>> = _upcomingMovies

    private val _searchedMovies = MutableStateFlow<NetworkResult<List<MovieResult>>>(NetworkResult.Initial())
    val searchedMovies: StateFlow<NetworkResult<List<MovieResult>>> = _searchedMovies

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception : ${throwable.localizedMessage}")
    }

    private var job: Job? = null
    private var searchMoviesJob: Job? = null

    init {
        getAllMovies()
    }

    fun getAllMovies() {
        _nowPlayingMovies.value = NetworkResult.Loading()
        _popularMovies.value = NetworkResult.Loading()
        _topRatedMovies.value = NetworkResult.Loading()
        _upcomingMovies.value = NetworkResult.Loading()

        job = CoroutineScope(Dispatchers.IO).launch {
            val nowPlayingDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(RecommendationType.NOW_PLAYING.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _nowPlayingMovies.value = NetworkResult.Success(it.results)
                        }
                    } else {
                        _nowPlayingMovies.value = NetworkResult.Error(response.message())
                    }
                }
            }
            nowPlayingDeferred.await()
            val popularDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.POPULAR.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _popularMovies.value = NetworkResult.Success(it.results)
                        }
                    } else {
                        _popularMovies.value = NetworkResult.Error(response.message())
                    }
                }
            }
            popularDeferred.await()
            val topRatedDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.TOP_RATED.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _topRatedMovies.value = NetworkResult.Success(it.results)
                        }
                    } else {
                        _topRatedMovies.value = NetworkResult.Error(response.message())
                    }
                }
            }
            topRatedDeferred.await()
            val upcomingDeferred = async(Dispatchers.IO + exceptionHandler) {
                val response = api.getMovies(recommendationType = RecommendationType.UPCOMING.type)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _upcomingMovies.value = NetworkResult.Success(it.results)
                        }
                    } else {
                        _upcomingMovies.value = NetworkResult.Error(response.message())
                    }
                }
            }
            upcomingDeferred.await()
        }
    }

    fun searchMovies(query : String) {
        _searchedMovies.value = NetworkResult.Loading()
        searchMoviesJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = api.searchMovies(query)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _searchedMovies.value = NetworkResult.Success(it.results)
                    }
                } else {
                    _searchedMovies.value = NetworkResult.Error(response.message())
                }
            }
        }
    }

    private fun onError(message: String) {
        // to do
    }

    fun cancelJob() {
        job?.cancel()
        searchMoviesJob?.cancel()
    }
}
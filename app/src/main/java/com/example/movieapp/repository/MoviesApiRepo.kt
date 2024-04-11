package com.example.movieapp.repository

import android.util.Log
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.MovieDetailData
import com.example.movieapp.model.RecommendationType
import com.example.movieapp.model.SortMode
import com.example.movieapp.model.api.MoviesApi
import com.example.movieapp.model.api.RequestState
import com.example.movieapp.util.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
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

    private val _movieDetails = MutableStateFlow<RequestState<MovieDetailData>>(RequestState.Initial())
    val movieDetails: StateFlow<RequestState<MovieDetailData>> = _movieDetails

    private val allMoviesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onGetAllMoviesError("Exception : ${throwable.localizedMessage}")
    }

    private val searchMoviesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onSearchMoviesError("Exception : ${throwable.localizedMessage}")
    }

    private val getMovieDetailsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onGetMovieDetailsError("Exception : ${throwable.localizedMessage}")
    }

    private var job: Job? = null
    private var searchMoviesJob: Job? = null
    private var getMovieDetailsJob : Job? = null

    init {
        getAllMovies()
    }

    private fun getAllMovies() {
        _nowPlayingMovies.value = RequestState.Loading()
        _popularMovies.value = RequestState.Loading()
        _topRatedMovies.value = RequestState.Loading()
        _upcomingMovies.value = RequestState.Loading()

        job = CoroutineScope(Dispatchers.IO + allMoviesExceptionHandler).launch {
            val nowPlayingDeferred = async(Dispatchers.IO) {
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
            val popularDeferred = async(Dispatchers.IO) {
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
            val topRatedDeferred = async(Dispatchers.IO) {
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
            val upcomingDeferred = async(Dispatchers.IO) {
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
        searchMoviesJob = CoroutineScope(
            Dispatchers.IO + searchMoviesExceptionHandler
        ).launch {
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

    fun getMovieDetails(movieId: Int) {
        _movieDetails.value = RequestState.Loading()
        getMovieDetailsJob = CoroutineScope(
            Dispatchers.IO + getMovieDetailsExceptionHandler
        ).launch {
            val response = api.getMovieDetails(movieId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _movieDetails.value = RequestState.Success(it)
                    }
                } else {
                    _movieDetails.value = RequestState.Error(response.message())
                }
            }
        }
    }

    fun applySortMode(sortMode: SortMode) {
        val nowPlayingMovies = _nowPlayingMovies.value.data
        val popularMovies = _popularMovies.value.data
        val topRatedMovies = _topRatedMovies.value.data
        val upcomingMovies = _upcomingMovies.value.data
        if (nowPlayingMovies != null && popularMovies != null
            && topRatedMovies != null && upcomingMovies != null) {
            when (sortMode) {
                SortMode.RATING_ASCENDING -> {
                    _nowPlayingMovies.value =
                        RequestState.Success(nowPlayingMovies.sortedBy { it.averageRating })
                    _popularMovies.value =
                        RequestState.Success(popularMovies.sortedBy { it.averageRating })
                    _topRatedMovies.value =
                        RequestState.Success(topRatedMovies.sortedBy { it.averageRating })
                    _upcomingMovies.value =
                        RequestState.Success(upcomingMovies.sortedBy { it.averageRating })
                }

                SortMode.RATING_DESCENDING -> {
                    _nowPlayingMovies.value =
                        RequestState.Success(nowPlayingMovies.sortedByDescending { it.averageRating })
                    _popularMovies.value =
                        RequestState.Success(popularMovies.sortedByDescending { it.averageRating })
                    _topRatedMovies.value =
                        RequestState.Success(topRatedMovies.sortedByDescending { it.averageRating })
                    _upcomingMovies.value =
                        RequestState.Success(upcomingMovies.sortedByDescending { it.averageRating })
                }
                SortMode.RELEASE_ASCENDING -> {
                    _nowPlayingMovies.value =
                        RequestState.Success(nowPlayingMovies.sortedBy {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                    _popularMovies.value =
                        RequestState.Success(popularMovies.sortedBy {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                    _topRatedMovies.value =
                        RequestState.Success(topRatedMovies.sortedBy {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                    _upcomingMovies.value =
                        RequestState.Success(upcomingMovies.sortedBy {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                }
                SortMode.RELEASE_DESCENDING -> {
                    _nowPlayingMovies.value =
                        RequestState.Success(nowPlayingMovies.sortedByDescending {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                    _popularMovies.value =
                        RequestState.Success(popularMovies.sortedByDescending {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                    _topRatedMovies.value =
                        RequestState.Success(topRatedMovies.sortedByDescending {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                    _upcomingMovies.value =
                        RequestState.Success(upcomingMovies.sortedByDescending {
                            Utils.getYearFromDateString(it.dateOfRelease).toInt() }
                        )
                }
            }
        }
    }

    private fun onGetAllMoviesError(message: String) {
        Log.e("MoviesApiRepo - onGetAllMoviesError()", message)
        _nowPlayingMovies.value = RequestState.Error("Something went wrong")
        _popularMovies.value = RequestState.Error("Something went wrong")
        _topRatedMovies.value = RequestState.Error("Something went wrong")
        _upcomingMovies.value = RequestState.Error("Something went wrong")
    }

    private fun onSearchMoviesError(message: String) {
        Log.e("MoviesApiRepo - onSearchMoviesError()", message)
        _searchedMovies.value = RequestState.Error("Something went wrong")
    }

    private fun onGetMovieDetailsError(message: String) {
        Log.e("MoviesApiRepo - onGetMovieDetailsError()", message)
        _movieDetails.value = RequestState.Error("Something went wrong")
    }

    fun cancelJobs() {
        job?.cancel()
        searchMoviesJob?.cancel()
        getMovieDetailsJob?.cancel()
    }
}
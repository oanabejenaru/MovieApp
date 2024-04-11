package com.example.movieapp.model.api

import com.example.movieapp.Destination
import com.example.movieapp.model.MovieDetailData
import com.example.movieapp.model.MoviesApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {
    @GET("movie/{recommendation_type}")
    suspend fun getMovies(@Path("recommendation_type") recommendationType: String): Response<MoviesApiResponse>

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): Response<MoviesApiResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): Response<MovieDetailData>
}
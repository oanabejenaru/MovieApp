package com.example.movieapp.model.api

import com.example.movieapp.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

    private fun getRetrofit() : Retrofit {
        val apiKey = BuildConfig.MOVIE_API_KEY
        val clientInterceptor = Interceptor { chain ->
            var request: Request = chain.request()
            val url: HttpUrl = request.url.newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder().addInterceptor(clientInterceptor).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: MoviesApi = getRetrofit().create(MoviesApi::class.java)
}
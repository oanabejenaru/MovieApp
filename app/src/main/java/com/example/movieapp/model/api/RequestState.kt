package com.example.movieapp.model.api

sealed class RequestState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Initial<T>() : RequestState<T>()
    class Success<T>(data: T) : RequestState<T>(data)
    class Error<T>(message: String, data: T? = null) : RequestState<T>(data, message)
    class Loading<T> : RequestState<T>()
}
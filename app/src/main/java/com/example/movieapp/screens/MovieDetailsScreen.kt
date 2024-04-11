package com.example.movieapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import com.example.movieapp.viewmodel.SharedViewModel

@Composable
fun MovieDetailsScreen(
    viewModel: SharedViewModel,
    movieId: Int?
) {
    val movieDetails by viewModel.resultMovieDetails.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getMovieDetails(movieId = movieId)
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Movie details ${movieDetails.data?.title}")
    }
}
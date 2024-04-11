package com.example.movieapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.movieapp.Destination
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.api.RequestState
import com.example.movieapp.ui.common.MoviesListGrid
import com.example.movieapp.viewmodel.SharedViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: SharedViewModel
) {

    val favoriteMoviesIds by viewModel.favoriteMoviesIds.collectAsState()
    val favoriteMovies by viewModel.resultFavoriteMovies.collectAsState()

    Scaffold(
        topBar = { FavoritesAppBar() }
    ) { padding ->
        FavoritesContent(
            modifier = Modifier.padding(padding),
            onItemClick = { id ->
                val route = Destination.MovieDetails.createRoute(id.toString())
                navController.navigate(route)
            },
            onFavoriteClick = {
                viewModel.removeFromFavorites(it.id)
            },
            favoriteMoviesIds = favoriteMoviesIds,
            result = favoriteMovies
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesAppBar() {
    TopAppBar(
        title = {
            Text(text = "Favorites", color = Color.White)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}

@Composable
fun FavoritesContent(
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    onFavoriteClick: (MovieData) -> Unit,
    favoriteMoviesIds: List<Int>,
    result: RequestState<List<MovieData>>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (result) {
            is RequestState.Initial -> {
                // nothing to do for now
            }

            is RequestState.Error -> {
                Text(text = "Error: ${result.message}")
            }

            is RequestState.Loading -> {
                CircularProgressIndicator()
            }

            is RequestState.Success -> {
                if (!result.data.isNullOrEmpty()) {
                    MoviesListGrid(
                        modifier = modifier,
                        favoriteMoviesIds = favoriteMoviesIds,
                        moviesList = result.data,
                        onFavoriteClick = onFavoriteClick,
                        onItemClick = onItemClick
                    )
                } else {
                    Text(text = "No favorite movie saved")
                }
            }
        }
    }
}
package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapp.screens.FavoritesScreen
import com.example.movieapp.screens.HomeScreen
import com.example.movieapp.screens.MovieDetailsScreen
import com.example.movieapp.screens.MoviesBottomNav
import com.example.movieapp.screens.SearchScreen
import com.example.movieapp.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class Destination(val route: String) {
    data object Favorites : Destination("Favorites")
    data object Home : Destination("Home")
    data object Search : Destination("Search")
    data object MovieDetails: Destination("MovieDetails/{movieId}") {
        fun createRoute(movieId: String) = "MovieDetails/$movieId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MovieAppScaffold(
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MovieAppScaffold(
    navController: NavHostController
) {
    val sharedViewModel = hiltViewModel<SharedViewModel>()
    Scaffold(
        bottomBar = {
            MoviesBottomNav(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = Destination.Home.route
        ) {
            composable(Destination.Favorites.route) {
                FavoritesScreen(
                    navController = navController,
                    viewModel = sharedViewModel
                )
            }
            composable(Destination.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = sharedViewModel
                )
            }
            composable(Destination.Search.route) {
                SearchScreen(
                    navController = navController,
                    viewModel = sharedViewModel
                )
            }
            composable(Destination.MovieDetails.route) { navBackStackEntry ->
                val movieId = navBackStackEntry.arguments?.getString("movieId")
                MovieDetailsScreen(
                    navController = navController,
                    viewModel = sharedViewModel,
                    movieId = movieId?.toInt()
                )
            }
        }
    }
}


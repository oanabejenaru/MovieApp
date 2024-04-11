package com.example.movieapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.model.Genre
import com.example.movieapp.model.MovieDetailData
import com.example.movieapp.model.api.RequestState
import com.example.movieapp.model.data.FavoriteMovie
import com.example.movieapp.ui.common.CommonImage
import com.example.movieapp.ui.common.CommonProgressSpinner
import com.example.movieapp.ui.theme.RatingYellow
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Utils

import com.example.movieapp.viewmodel.SharedViewModel

@Composable
fun MovieDetailsScreen(
    navController: NavController,
    viewModel: SharedViewModel,
    movieId: Int?
) {
    val movieDetailsResult by viewModel.resultMovieDetails.collectAsState()
    val favoriteMoviesIds by viewModel.favoriteMoviesIds.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getMovieDetails(movieId = movieId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (movieDetailsResult) {
            is RequestState.Error -> {
                Text(text = "Couldn't get movie details")
            }

            is RequestState.Initial -> {
                // nothing to do
            }

            is RequestState.Loading -> {
                CommonProgressSpinner()
            }

            is RequestState.Success -> {
                val resultData = movieDetailsResult.data
                if (resultData != null) {
                    val isFavorite = favoriteMoviesIds.contains(resultData.id)
                    MovieContent(
                        navController = navController,
                        movie = resultData,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            if (!favoriteMoviesIds.contains(it.id)) {
                                viewModel.addToFavorites(it)
                            } else {
                                viewModel.removeFromFavorites(it.id)
                            }
                        }
                    )
                } else {
                    Text(text = "No details found about this movie")
                }
            }
        }
    }
}

@Composable
fun MovieContent(
    navController: NavController,
    movie: MovieDetailData,
    isFavorite: Boolean,
    onFavoriteClick: (MovieDetailData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Box {
            CommonImage(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
                url = Constants.BASE_IMAGE_URL + movie.backdropPath
            )
            IconButton(
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 10.dp
                ),
                onClick = {
                    navController.navigateUp()
                }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            CommonImage(
                modifier = Modifier
                    .height(240.dp)
                    .weight(0.4f),
                url = Constants.BASE_IMAGE_URL + movie.posterPath
            )
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 10.dp)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = movie.title ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2
                )

                Text(
                    text = movie.tagline ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 2
                )

                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = "Released in ${Utils.getYearFromDateString(movie.releaseDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = RatingYellow
                    )
                    Text(
                        text = "${Utils.formatNumberToOneDecimal(movie.voteAverage)} / 10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                    )
                    Text(
                        modifier = Modifier.padding(start = 3.dp),
                        text = "(${movie.voteCount})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                    )
                }
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.End),
                    onClick = {
                        onFavoriteClick.invoke(movie)
                    }
                ) {
                    if (isFavorite) {
                        Icon(
                            Icons.Filled.Favorite,
                            tint = Color.Red,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            tint = Color.Gray,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
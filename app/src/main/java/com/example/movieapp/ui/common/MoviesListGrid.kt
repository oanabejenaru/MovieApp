package com.example.movieapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.movieapp.model.MovieData
import com.example.movieapp.ui.theme.RatingYellow
import com.example.movieapp.util.Constants
import java.text.DecimalFormat

@Composable
fun MoviesListGrid(
    modifier: Modifier,
    favoriteMoviesIds: List<Int>,
    moviesList: List<MovieData>,
    onFavoriteClick: (MovieData) -> Unit,
    onItemClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxHeight(),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(moviesList) { movie ->
            val isFavorite = favoriteMoviesIds.contains(movie.id)
            MovieItem(
                item = movie,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun MovieItem(
    item: MovieData,
    isFavorite: Boolean,
    onFavoriteClick: (MovieData) -> Unit,
    onItemClick: (Int) -> Unit
) {
    val decimalFormat = DecimalFormat("#.#")
    val averageRating = decimalFormat.format(item.averageRating)
    var yearOfRelease = "-"
    if (!item.yearOfRelease.isNullOrEmpty() && item.yearOfRelease.count() >= 4) {
        yearOfRelease = item.yearOfRelease.slice(IntRange(0, 3))
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .padding(6.dp)
            .clickable {
                item.id?.let {
                    onItemClick.invoke(it)
                }
            }
    ) {
        Column {
            CommonImage(
                url = Constants.BASE_IMAGE_URL + item.posterPath,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(top = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = yearOfRelease)
                Row {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = RatingYellow
                    )
                    Text(text = averageRating)
                }
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = {
                        onFavoriteClick.invoke(item)
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
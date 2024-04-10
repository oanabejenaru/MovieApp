package com.example.movieapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.movieapp.R
import com.example.movieapp.model.MovieResult
import com.example.movieapp.model.api.ApiService
import com.example.movieapp.ui.theme.RatingYellow
import java.text.DecimalFormat

@Composable
fun MovieListGrid(
    modifier : Modifier,
    movieList: List<MovieResult>,
    onItemClick : (MovieResult) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(count = 2)
    ) {
        items(movieList) { movie ->
            MovieItem(
                item = movie,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun MovieItem(
    item: MovieResult,
    onItemClick: (MovieResult) -> Unit
) {
    val decimalFormat = DecimalFormat("#.#")
    val averageRating = decimalFormat.format(item.averageRating)
    val yearOfRelease = item.yearOfRelease?.slice(IntRange(0,3)) ?: ""

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .padding(6.dp)
            .clickable { onItemClick.invoke(item) }
    ) {
        Column {
            CommonImage(
                url = ApiService.POSTER_BASE_URL + item.posterPath,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
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
                        painter = painterResource(id = R.drawable.ic_rating_star),
                        contentDescription = null,
                        tint = RatingYellow
                    )
                    Text(text = averageRating)
                }
                Icon(
                    tint = Color.Gray,
                    painter = painterResource(id = R.drawable.ic_favorites_outlined),
                    contentDescription = null
                )
            }
        }

    }
}
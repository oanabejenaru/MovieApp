package com.example.movieapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.Destination
import com.example.movieapp.R
import com.example.movieapp.model.MovieData
import com.example.movieapp.model.RecommendationType
import com.example.movieapp.model.SortMode
import com.example.movieapp.model.api.RequestState
import com.example.movieapp.ui.common.MoviesListGrid
import com.example.movieapp.viewmodel.SharedViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: SharedViewModel
) {
    val nowPlayingMovies by viewModel.resultNowPlayingMovies.collectAsState()
    val popularMovies by viewModel.resultPopularMovies.collectAsState()
    val topRatedMovies by viewModel.resultTopRatedMovies.collectAsState()
    val upcomingMovies by viewModel.resultUpcomingMovies.collectAsState()
    val favoriteMoviesIds by viewModel.favoriteMoviesIds.collectAsState()

    val currentSortMode = viewModel.currentSortMode

    Scaffold(
        topBar = {
            HomeAppBar(
                currentSortMode = currentSortMode,
                onSortClicked = {
                    viewModel.applySortMode(it)
                }
            )
        }
    ) { padding ->
        HomeContent(
            modifier = Modifier.padding(padding),
            onItemClick = { id ->
                val route = Destination.MovieDetails.createRoute(id.toString())
                navController.navigate(route)
            },
            onFavoriteClick = {
                if (!favoriteMoviesIds.contains(it.id)) {
                    viewModel.addToFavorites(it)
                } else {
                    viewModel.removeFromFavorites(it.id)
                }
            },
            favoriteMoviesIds = favoriteMoviesIds,
            nowPlayingMovies = nowPlayingMovies,
            popularMovies = popularMovies,
            topRatedMovies = topRatedMovies,
            upcomingMovies = upcomingMovies
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    currentSortMode: SortMode?,
    onSortClicked: (SortMode) -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Home", color = Color.White)
        },
        actions = {
            SortAction(
                currentSortMode = currentSortMode,
                onSortClicked = onSortClicked
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}

@Composable
fun SortAction(
    currentSortMode: SortMode?,
    onSortClicked: (SortMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sort),
            contentDescription = null,
            tint = Color.White
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortMode.entries.toTypedArray()
                .forEach { sortMode ->
                    DropdownMenuItem(
                        trailingIcon = {
                            if (currentSortMode == sortMode) {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                        },
                        text = { Text(text = sortMode.sortName) },
                        onClick = {
                            expanded = false
                            onSortClicked(sortMode)
                        }
                    )
                }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeContent(
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    onFavoriteClick: (MovieData) -> Unit,
    favoriteMoviesIds: List<Int>,
    nowPlayingMovies: RequestState<List<MovieData>>,
    popularMovies: RequestState<List<MovieData>>,
    topRatedMovies: RequestState<List<MovieData>>,
    upcomingMovies: RequestState<List<MovieData>>
) {
    val pagerState = rememberPagerState(initialPage = 0)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TabLayout(pagerState = pagerState)
        TabContent(
            pagerState = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(1.dp)
                .fillMaxSize(),
            onItemClick = onItemClick,
            onFavoriteClick = onFavoriteClick,
            favoriteMoviesIds = favoriteMoviesIds,
            nowPlayingMovies = nowPlayingMovies,
            popularMovies = popularMovies,
            topRatedMovies = topRatedMovies,
            upcomingMovies = upcomingMovies
        )
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = Color.Black,
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(currentTabPosition = tabPositions[pagerState.currentPage]),
                height = 3.dp,
                color = Color.White
            )
        }
    ) {
        RecommendationType.entries.forEachIndexed { index, type ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(text = type.tabName)
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContent(
    pagerState: PagerState,
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
    onFavoriteClick: (MovieData) -> Unit,
    favoriteMoviesIds: List<Int>,
    nowPlayingMovies: RequestState<List<MovieData>>,
    popularMovies: RequestState<List<MovieData>>,
    topRatedMovies: RequestState<List<MovieData>>,
    upcomingMovies: RequestState<List<MovieData>>
) {
    HorizontalPager(
        state = pagerState,
        count = RecommendationType.entries.size
    ) { page ->
        when (page) {
            0 -> {
                HomeListContent(
                    modifier = modifier,
                    onItemClick = onItemClick,
                    onFavoriteClick = onFavoriteClick,
                    favoriteMoviesIds = favoriteMoviesIds,
                    result = nowPlayingMovies
                )
            }

            1 -> {
                HomeListContent(
                    modifier = modifier,
                    onItemClick = onItemClick,
                    onFavoriteClick = onFavoriteClick,
                    favoriteMoviesIds = favoriteMoviesIds,
                    result = popularMovies
                )
            }

            2 -> {
                HomeListContent(
                    modifier = modifier,
                    onItemClick = onItemClick,
                    onFavoriteClick = onFavoriteClick,
                    favoriteMoviesIds = favoriteMoviesIds,
                    result = topRatedMovies
                )
            }

            3 -> {
                HomeListContent(
                    modifier = modifier,
                    onFavoriteClick = onFavoriteClick,
                    favoriteMoviesIds = favoriteMoviesIds,
                    onItemClick = onItemClick,
                    result = upcomingMovies
                )
            }
        }
    }
}

@Composable
fun HomeListContent(
    modifier: Modifier,
    favoriteMoviesIds: List<Int>,
    result: RequestState<List<MovieData>>,
    onFavoriteClick: (MovieData) -> Unit,
    onItemClick: (Int) -> Unit
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
                    Text(text = "No movie found")
                }
            }

            is RequestState.Loading -> {
                CircularProgressIndicator()
            }

            is RequestState.Error -> {
                Text(text = "Error: ${result.message}")
            }
        }
    }
}




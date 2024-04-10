package com.example.movieapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.example.movieapp.R
import com.example.movieapp.model.MovieResult
import com.example.movieapp.model.RecommendationType
import com.example.movieapp.model.SortMode
import com.example.movieapp.model.api.NetworkResult
import com.example.movieapp.ui.common.MovieListGrid
import com.example.movieapp.viewmodel.HomeScreenViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel
) {
    val nowPlayingMovies by viewModel.resultNowPlayingMovies.collectAsState()
    val popularMovies by viewModel.resultPopularMovies.collectAsState()
    val topRatedMovies by viewModel.resultTopRatedMovies.collectAsState()
    val upcomingMovies by viewModel.resultUpcomingMovies.collectAsState()

    Scaffold(
        topBar = {
            HomeAppBar(
                onSortClicked = {
                    // to do
                }
            )
        }
    ) { padding ->
        HomeContent(
            modifier = Modifier.padding(padding),
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
    onSortClicked: (SortMode) -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Home", color = Color.White)
        },
        actions = {
            SortAction(onSortClicked = onSortClicked)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}

@Composable
fun SortAction(
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
    nowPlayingMovies: NetworkResult<List<MovieResult>>,
    popularMovies: NetworkResult<List<MovieResult>>,
    topRatedMovies: NetworkResult<List<MovieResult>>,
    upcomingMovies: NetworkResult<List<MovieResult>>
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
            onItemClick = {
                // to do
            },
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
    onItemClick: (MovieResult) -> Unit,
    nowPlayingMovies: NetworkResult<List<MovieResult>>,
    popularMovies: NetworkResult<List<MovieResult>>,
    topRatedMovies: NetworkResult<List<MovieResult>>,
    upcomingMovies: NetworkResult<List<MovieResult>>
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
                    result = nowPlayingMovies
                )
            }

            1 -> {
                HomeListContent(
                    modifier = modifier,
                    onItemClick = onItemClick,
                    result = popularMovies
                )
            }

            2 -> {
                HomeListContent(
                    modifier = modifier,
                    onItemClick = onItemClick,
                    result = topRatedMovies
                )
            }

            3 -> {
                HomeListContent(
                    modifier = modifier,
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
    result: NetworkResult<List<MovieResult>>,
    onItemClick: (MovieResult) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (result) {
            is NetworkResult.Initial -> {
                // nothing to do for now
            }
            is NetworkResult.Success -> {
                if (!result.data.isNullOrEmpty()) {
                    MovieListGrid(
                        modifier = modifier,
                        movieList = result.data,
                        onItemClick = onItemClick
                    )
                } else {
                    Text(text = "No movie found")
                }
            }

            is NetworkResult.Loading -> {
                CircularProgressIndicator()
            }

            is NetworkResult.Error -> {
                Text(text = "Error: ${result.message}")
            }
        }
    }
}




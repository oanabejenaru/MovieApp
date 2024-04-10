package com.example.movieapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.model.MovieResult
import com.example.movieapp.model.api.NetworkResult
import com.example.movieapp.ui.common.MovieListGrid
import com.example.movieapp.util.SearchAppBarState
import com.example.movieapp.viewmodel.SearchScreenViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchScreenViewModel,
) {
    val searchedMovies by viewModel.result.collectAsState()
    val searchAppBarState: SearchAppBarState = viewModel.searchAppBarState
    val searchTextState: String = viewModel.searchTextState

    Scaffold(
        topBar = {
            SearchScreenAppBar(
                viewModel = viewModel,
                searchAppBarState = searchAppBarState,
                searchTextState = searchTextState
            )
        }
    ) { paddingValues ->
        SearchMovieListContent(
            modifier = Modifier.padding(paddingValues),
            searchAppBarState = searchAppBarState,
            result = searchedMovies,
            onItemClick = {
                // to do
            }
        )
    }
}

@Composable
private fun SearchMovieListContent(
    modifier: Modifier,
    searchAppBarState: SearchAppBarState,
    result: NetworkResult<List<MovieResult>>,
    onItemClick: (MovieResult) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (searchAppBarState == SearchAppBarState.TRIGGERED) {
            when (result) {
                is NetworkResult.Initial -> {
                    // nothing to do
                }

                is NetworkResult.Error -> {
                    Text(text = "Error: ${result.message}")
                }

                is NetworkResult.Loading -> {
                    CircularProgressIndicator()
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
            }
        } else {
            Text(text = "Type something to search for a movie")
        }
    }
}

@Composable
private fun SearchScreenAppBar(
    viewModel: SearchScreenViewModel,
    searchAppBarState: SearchAppBarState,
    searchTextState: String
) {
    when (searchAppBarState) {
        SearchAppBarState.CLOSED -> {
            DefaultSearchScreenAppBar(
                onSearchClicked = {
                    viewModel.updateSearchAppBarState(newState = SearchAppBarState.OPENED)
                }
            )
        }

        else -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = { newText ->
                    viewModel.updateSearchTextState(newText = newText)
                },
                onCloseClicked = {
                    viewModel.updateSearchAppBarState(newState = SearchAppBarState.CLOSED)
                    viewModel.updateSearchTextState(newText = "")
                },
                onSearchClicked = viewModel::searchMovies
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultSearchScreenAppBar(
    onSearchClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Search movie",
                color = Color.White
            )
        },
        actions = {
            SearchAction(onSearchClicked = onSearchClicked)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}


@Composable
fun SearchAction(
    onSearchClicked: () -> Unit
) {
    IconButton(onClick = { onSearchClicked() }) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shadowElevation = 8.dp,
        color = Color.Black
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = "Search",
                    color = Color.Gray
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}
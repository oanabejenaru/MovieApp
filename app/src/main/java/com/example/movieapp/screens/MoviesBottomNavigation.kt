package com.example.movieapp.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.movieapp.Destination
import com.example.movieapp.R

@Composable
fun MoviesBottomNav(navController: NavController) {
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry.value?.destination

        val iconHome = painterResource(id = R.drawable.ic_movies)

        NavigationBarItem(
            selected = currentDestination?.route == Destination.Favorites.route,
            onClick = {
                navController.navigate(Destination.Favorites.route) {
                    popUpTo(Destination.Favorites.route)
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
            label = { Text(text = Destination.Favorites.route) }
        )

        NavigationBarItem(
            selected = currentDestination?.route == Destination.Home.route,
            onClick = {
                navController.navigate(Destination.Home.route) {
                    popUpTo(Destination.Home.route)
                    launchSingleTop = true
                }
            },
            icon = { Icon(painter = iconHome, contentDescription = null) },
            label = { Text(text = Destination.Home.route) }
        )

        NavigationBarItem(
            selected = currentDestination?.route == Destination.Search.route,
            onClick = {
                navController.navigate(Destination.Search.route) {
                    popUpTo(Destination.Search.route)
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            label = { Text(text = Destination.Search.route) }
        )
    }
}
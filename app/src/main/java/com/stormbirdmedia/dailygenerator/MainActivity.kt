package com.stormbirdmedia.dailygenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.stormbirdmedia.dailygenerator.screen.AddUserScreen
import com.stormbirdmedia.dailygenerator.screen.joke.JokeScreen
import com.stormbirdmedia.dailygenerator.screen.main.MainScreen
import com.stormbirdmedia.dailygenerator.screen.randomizer.RandomizerScreen
import com.stormbirdmedia.dailygenerator.ui.theme.DailyGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyGeneratorTheme {
                val navController = rememberNavController()

                AppNavHost(navController = navController)
            }
        }
    }
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
    NavHost(
        navController = navController,
        startDestination = MainDestination.UserList.route,
        modifier = modifier.padding(16.dp)
    ) {
        composable(route = MainDestination.UserList.route) {
            MainScreen(
               navController = navController
            )
        }
        composable(route = MainDestination.AddUser.route) {
            AddUserScreen(
                navController = navController
            )
        }
        composable(route = MainDestination.Randomizer.route) {
            RandomizerScreen(
                navController = navController
            )
        }
        composable(route = MainDestination.Joke.route) {
            JokeScreen(
                navController = navController
            )
        }
    }
}


sealed class MainDestination(val route: String) {
    object UserList : MainDestination("userList")
    object AddUser : MainDestination("second")
    object Randomizer : MainDestination("randomizer")
    object Joke : MainDestination("joke")
}

typealias OnClickHandler = () -> Unit
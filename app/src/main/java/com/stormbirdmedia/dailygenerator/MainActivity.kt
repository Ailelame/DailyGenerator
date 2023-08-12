package com.stormbirdmedia.dailygenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.DynamicColors
import com.stormbirdmedia.dailygenerator.screen.main.MainScreen
import com.stormbirdmedia.dailygenerator.ui.theme.DailyGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivitiesIfAvailable(application)

        setContent {
            DailyGeneratorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    AppNavHost(navController = navController)
                }
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

    }
}


sealed class MainDestination(val route: String) {
    object UserList : MainDestination("userList")
}

typealias OnClickHandler = () -> Unit
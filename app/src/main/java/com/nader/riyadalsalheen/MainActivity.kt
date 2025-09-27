package com.nader.riyadalsalheen

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nader.riyadalsalheen.ui.components.LoadingContent
import com.nader.riyadalsalheen.ui.screens.HadithDetailScreen
import com.nader.riyadalsalheen.ui.screens.SearchScreen
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()

            viewModel.isDarkMode = if(viewModel.systemTheme.value) isSystemInDarkTheme() else !isSystemInDarkTheme()
            RiyadalsalheenTheme (viewModel.isDarkMode, activity = this) {
                if (!viewModel.isInitialDataLoaded.value) {
                    LoadingContent()
                } else {
                    MainActivityComposable(viewModel)
                }
            }

        }
    }
}

@Composable
fun MainActivityComposable(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "hadithDetail/${viewModel.currentHadithId}"
    ) {
        composable("hadithDetail/{hadithId}") { backStackEntry ->
            val hadithId =
                backStackEntry.arguments?.getString("hadithId")?.toIntOrNull() ?: 0
            viewModel.navigateToHadith(hadithId)
            HadithDetailScreen(
                viewModel = viewModel,
                onSearch = { navController.navigate("search") },
                onNavigateToHadith = { navController.navigate("hadithDetail/$it") }
            )
        }
        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onHadithSelected = { navController.navigate("hadithDetail/$it") },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}
package com.nader.riyadalsalheen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Rtl
            ) {
                RiyadalsalheenTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        if (!viewModel.isInitialDataLoaded.value) {
                            LoadingContent()
                        } else {
                            MainActivityComposable(viewModel)
                        }
                    }
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
package com.nader.riyadalsalheen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.nader.riyadalsalheen.ui.screens.HadithDetailScreen
import com.nader.riyadalsalheen.ui.screens.SearchScreen
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Rtl
            ) {
                MainActivityComposable()
            }
        }
    }
}

@Composable
fun MainActivityComposable() {
    val viewModel: MainViewModel = viewModel()
    RiyadalsalheenTheme(darkTheme = viewModel.isDarkTheme.value) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "hadithDetail/{hadithId}"
            ) {
                composable("hadithDetail/{hadithId}") { backStackEntry ->
                    val hadithId = backStackEntry.arguments?.getInt("hadithId") ?: 0
                    val currentHadith = viewModel.currentHadith.value
                    if(hadithId == 0 && currentHadith != null) {
                        backStackEntry.arguments?.putString("hadithId", currentHadith.hadith.id.toString())
                    }

                    viewModel.navigateToHadith(hadithId)
                    HadithDetailScreen(
                        viewModel = viewModel,
                        onSearch = {
                            navController.navigate("search")
                        }
                    )
                }
                composable("search") {
                    SearchScreen(
                        viewModel = viewModel,
                        onHadithSelected = { hadithId ->
                            navController.navigate("hadithDetail/$hadithId")
                        },
                        onBackPressed = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}
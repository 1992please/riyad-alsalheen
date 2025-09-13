package com.nader.riyadalsalheen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nader.riyadalsalheen.ui.screens.BookListScreen
import com.nader.riyadalsalheen.ui.screens.DoorListScreen
import com.nader.riyadalsalheen.ui.screens.HadithDetailScreen
import com.nader.riyadalsalheen.ui.screens.HadithListScreen
import com.nader.riyadalsalheen.ui.screens.HomeScreen
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
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onBooksClicked = {
                            // Navigate to appendix screen
                            navController.navigate("books") {
                                popUpTo("home") {
                                    saveState = true
                                }
                            }
                        },
                        onNavigateToHadith = { hadithId ->
                            // Save to preferences
                            navController.navigate("hadithDetail/$hadithId") {
                                popUpTo("home") { saveState = true }
                            }
                        },
                        onBookmarksClicked = {

                        },
                        onSearchClicked = {

                        }
                    )
                }
                composable("books") {
                    BookListScreen(
                        viewModel = viewModel,
                        onBookSelected = { book ->
                            navController.navigate("doors/${book.id}")
                        }
                    )
                }
                composable("doors/{bookId}") { backStackEntry ->
                    val bookId =
                        backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                            ?: 0
                    LaunchedEffect(bookId) {
                        viewModel.loadDoors(bookId)
                    }
                    DoorListScreen(
                        viewModel = viewModel,
                        onDoorSelected = { door ->
                            navController.navigate("hadiths/${door.id}")
                        }
                    )
                }
                composable("hadiths/{doorId}") { backStackEntry ->
                    val doorId =
                        backStackEntry.arguments?.getString("doorId")?.toIntOrNull()
                            ?: 0
                    LaunchedEffect(doorId) {
                        viewModel.loadHadiths(doorId)
                    }
                    HadithListScreen(
                        viewModel = viewModel,
                        onHadithSelected = { hadith ->
                            navController.navigate("hadithDetail/${hadith.id}")
                        }
                    )
                }
                composable("hadithDetail/{hadithId}") { backStackEntry ->
                    val hadithId =
                        backStackEntry.arguments?.getString("hadithId")?.toIntOrNull()
                            ?: 0
                    LaunchedEffect(hadithId) {
                        viewModel.loadHadith(hadithId)
                    }
                    HadithDetailScreen(
                        viewModel = viewModel,
                        onNextHadith = {
                            if (hadithId < viewModel.hadithCount.intValue) {
                                navController.navigate("hadithDetail/${hadithId + 1}") {
                                    launchSingleTop = true
                                }
                            }
                        },
                        onPreviousHadith = {
                            if (hadithId > 1) {
                                navController.navigate("hadithDetail/${hadithId - 1}") {
                                    launchSingleTop = true
                                }
                            }
                        },
                        onBack = {
                            // Go back to previous screen based on navigation history
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {
                                navController.navigate("home") {
                                    popUpTo(0)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
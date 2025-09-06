package com.nader.riyadalsalheen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nader.riyadalsalheen.ui.screens.BookListScreen
import com.nader.riyadalsalheen.ui.screens.DoorListScreen
import com.nader.riyadalsalheen.ui.screens.HadithDetailScreen
import com.nader.riyadalsalheen.ui.screens.HadithListScreen
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "books"
                    ) {
                        composable("books") {
                            BookListScreen(
                                viewModel = viewModel,
                                onBookSelected = { book ->
                                    navController.navigate("doors/${book.id}")
                                }
                            )
                        }
                        composable("doors/{bookId}") { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 0
                            DoorListScreen(
                                viewModel = viewModel,
                                onDoorSelected = { door ->
                                    navController.navigate("hadiths/${door.id}")
                                }
                            )
                        }
                        composable("hadiths/{doorId}") { backStackEntry ->
                            val doorId = backStackEntry.arguments?.getString("doorId")?.toIntOrNull() ?: 0
                            HadithListScreen(
                                hadiths = viewModel.hadiths.value,
                                onHadithSelected = { hadith ->
                                    navController.navigate("hadithDetail/${hadith.id}")
                                }
                            )
                        }
                        composable("hadithDetail/{hadithId}") { backStackEntry ->
                            val hadithId = backStackEntry.arguments?.getString("hadithId")?.toIntOrNull() ?: 0
                            val hadith = viewModel.hadiths.value.find { it.id == hadithId }
                            hadith?.let { HadithDetailScreen(hadith = it) }
                        }
                    }
                }
            }
        }
    }

}
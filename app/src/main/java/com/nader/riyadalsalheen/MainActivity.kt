package com.nader.riyadalsalheen

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nader.riyadalsalheen.ui.components.FontSizeDialog
import com.nader.riyadalsalheen.ui.components.LoadingContent
import com.nader.riyadalsalheen.ui.components.NavigationDrawer
import com.nader.riyadalsalheen.ui.screens.AboutScreen
import com.nader.riyadalsalheen.ui.screens.BookmarksScreen
import com.nader.riyadalsalheen.ui.screens.HadithDetailScreen
import com.nader.riyadalsalheen.ui.screens.SearchScreen
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()

            val isDarkMode = if(viewModel.systemTheme.value) isSystemInDarkTheme() else !isSystemInDarkTheme()
            RiyadalsalheenTheme (isDarkMode, activity = this) {
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showFontSizeDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                bookmarks = viewModel.bookmarks.value,
                hadithCount = viewModel.hadithCount,
                versionName = viewModel.packageInfo.versionName ?: "1.0.0",
                onNavigateToAbout = { navController.navigate("about") },
                onNavigateToBookmarks = { navController.navigate("bookmarks") },
                onNavigateToHadith = {
                    navController.navigate("hadithDetail/$it")
                },
                onFontSizeChange = { showFontSizeDialog = true },
                onClose = { coroutineScope.launch { drawerState.close() } },
                onToggleDarkMode = { viewModel.toggleSystemTheme() }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "hadithDetail/${viewModel.currentHadithId}"
        ) {
            composable("hadithDetail/{hadithId}") { backStackEntry ->
                val hadithId =
                    backStackEntry.arguments?.getString("hadithId")?.toIntOrNull() ?: 0

                HadithDetailScreen(
                    initHadithID = hadithId,
                    viewModel = viewModel,
                    onLoadDoor = { doorId ->
                        coroutineScope.launch {
                            viewModel.getFirstHadithIdInDoor(doorId)?.let {
                                navController.navigate("hadithDetail/$it")
                            }
                        }
                    },
                    onSearch = { navController.navigate("search") },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
            composable("search") {
                SearchScreen(
                    viewModel = viewModel,
                    onHadithSelected = {
                        navController.navigate("hadithDetail/$it")
                    },
                    onBackPressed = {
                        navController.navigateUp()
                    }
                )
            }
            composable("bookmarks") {
                BookmarksScreen(
                    viewModel = viewModel,
                    onHadithSelected = {
                        navController.navigate("hadithDetail/$it")
                    },
                    onBackPressed = {
                        navController.navigateUp()
                    }
                )
            }
            composable("about") {
                AboutScreen(
                    onBackPressed = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }

    // Font Size Dialog
    if (showFontSizeDialog) {
        FontSizeDialog(
            fontSize = viewModel.fontSize.floatValue,
            onUpdateFontSize = {viewModel.updateFontSize(it)},
            onDismiss = { showFontSizeDialog = false }
        )
    }
}
// TODO make unit test to go over all hadith of the database and make sure that there's no hadith missing
// TODO add all missing hadiths with their sharh
// TODO add hadith matn norm text to the database to ease the search
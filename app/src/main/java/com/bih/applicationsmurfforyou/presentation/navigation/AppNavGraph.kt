package com.bih.applicationsmurfforyou.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.bih.applicationsmurfforyou.presentation.explore.ExploreScreen
import com.bih.applicationsmurfforyou.presentation.openscreen.OpenScreen
import com.bih.applicationsmurfforyou.presentation.smurf_detail.SmurfDetailScreen
import com.bih.applicationsmurfforyou.presentation.smurfify.SmurfScreen
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.OPEN_SCREEN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 2 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 2 },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {

        // Opening Screen (splash screen)
        composable(NavRoutes.OPEN_SCREEN) {
            OpenScreen {
                navController.navigate(NavRoutes.EXPLORE) {
                    popUpTo(NavRoutes.OPEN_SCREEN) { inclusive = true }
                }
            }
        }

        // Explore (list screen)
        composable(NavRoutes.EXPLORE) {
            ExploreScreen(
                onNavigateToSmurfify = { navController.navigate(NavRoutes.SMURFIFY) },
                onSmurfClick = { smurfName ->
                    navController.navigate(NavRoutes.smurfDetail(smurfName))
                }
            )
        }

        // Smurf Detail Screen
        composable(
            route = NavRoutes.SMURF_DETAIL,
            arguments = listOf(navArgument("smurfName") { type = NavType.StringType })
        ) {
            SmurfDetailScreen()
        }

        // Smurfify (ai screen)
        composable(NavRoutes.SMURFIFY) {
            SmurfScreen { navController.popBackStack() }
        }
    }
}

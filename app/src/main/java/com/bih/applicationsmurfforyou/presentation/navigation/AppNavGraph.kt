package com.bih.applicationsmurfforyou.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.bih.applicationsmurfforyou.presentation.explore.ExploreScreen
import com.bih.applicationsmurfforyou.presentation.smurfify.SmurfifyScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController) {

    AnimatedNavHost(
        navController = navController,
        startDestination = NavRoutes.SMURFIFY,
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

        // Explore (list screen)
        composable(NavRoutes.EXPLORE) {
            ExploreScreen(
                onNavigate = { navController.navigate(NavRoutes.SMURFIFY) }
            )
        }

        // Smurfify (ai screen)
        composable(NavRoutes.SMURFIFY) {
            SmurfifyScreen { navController.popBackStack() }
        }
    }
}




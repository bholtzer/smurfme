package com.bih.applicationsmurfforyou.presentation.navigation

sealed class Screen(val route: String) {
    object Explore : Screen("Explore")
    object Smurfies : Screen("Smurfies")
}
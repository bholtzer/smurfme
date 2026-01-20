package com.bih.applicationsmurfforyou.presentation.navigation

object NavRoutes {
    const val OPEN_SCREEN = "open_screen"
    const val EXPLORE = "explore"
    const val SMURFIFY = "smurfify"
    const val SMURF_DETAIL = "smurf_detail/{smurfName}"

    fun smurfDetail(smurfName: String): String {
        return "smurf_detail/$smurfName"
    }
}

package com.bih.applicationsmurfforyou.presentation.navigation

object NavRoutes {
    const val OPEN_SCREEN = "open_screen"
    const val EXPLORE = "explore"
    const val SMURFIFY = "smurfify"
    const val SMURF_DETAIL = "smurf_detail/{smurfName}"

    // New Routes for Settings Content
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS_CONDITIONS = "terms_conditions"
    const val PERMISSIONS = "permissions"
    const val LANGUAGE_SETTINGS = "language_settings"

    fun smurfDetail(smurfName: String): String {
        return "smurf_detail/$smurfName"
    }
}

package com.atomicanalyst.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Accounts : Screen("accounts")
}

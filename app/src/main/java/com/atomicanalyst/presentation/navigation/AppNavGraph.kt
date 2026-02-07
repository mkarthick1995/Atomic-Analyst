package com.atomicanalyst.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atomicanalyst.presentation.screen.AccountsScreen
import com.atomicanalyst.presentation.screen.HomeScreen

@Suppress("FunctionNaming")
@Composable
fun AppNavGraph(startDestination: String = Screen.Home.route) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigateAccounts = {
                navController.navigate(Screen.Accounts.route)
            })
        }
        composable(Screen.Accounts.route) {
            AccountsScreen()
        }
    }
}

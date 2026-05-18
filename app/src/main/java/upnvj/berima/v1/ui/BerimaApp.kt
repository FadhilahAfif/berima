package upnvj.berima.v1.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import upnvj.berima.v1.R
import upnvj.berima.v1.navigation.BerimaNavGraph
import upnvj.berima.v1.navigation.Screen
import upnvj.berima.v1.ui.theme.LocalBerimaColors

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconRes: Int
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Beranda", R.drawable.ic_home),
    BottomNavItem(Screen.Orders, "Pesanan", R.drawable.ic_orders),
    BottomNavItem(Screen.Profile, "Profil", R.drawable.ic_profile),
)

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Orders.route,
    Screen.Profile.route,
)

@Composable
fun BerimaApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val berimaColors = LocalBerimaColors.current

    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(item.iconRes),
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = berimaColors.containerGreen
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        BerimaNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

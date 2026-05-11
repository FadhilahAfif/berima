package upnvj.berima.v1.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import upnvj.berima.v1.navigation.BerimaNavGraph

/**
 * Root composable. Owns the single NavController for the app and hosts
 * the Scaffold that later phases can extend (bottom nav, snackbars, etc.).
 */
@Composable
fun BerimaApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        BerimaNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

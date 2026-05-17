package upnvj.berima.v1.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import upnvj.berima.v1.ui.auth.LoginScreen
import upnvj.berima.v1.ui.auth.RegisterScreen
import upnvj.berima.v1.ui.splash.SplashScreen

@Composable
fun BerimaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
            )
        }

        composable(Screen.Home.route) {
            Placeholder(name = "Home")
        }
        composable(Screen.Orders.route) {
            Placeholder(name = "Orders")
        }
        composable(Screen.Profile.route) {
            Placeholder(name = "Profile")
        }

        composable(
            route = Screen.ListingDetail.route,
            arguments = listOf(
                navArgument(Screen.ListingDetail.ARG_LISTING_ID) { type = NavType.StringType }
            )
        ) { entry ->
            val listingId = entry.arguments?.getString(Screen.ListingDetail.ARG_LISTING_ID).orEmpty()
            Placeholder(name = "ListingDetail($listingId)")
        }

        composable(Screen.CreateListing.route) {
            Placeholder(name = "CreateListing")
        }

        composable(
            route = Screen.EditListing.route,
            arguments = listOf(
                navArgument(Screen.EditListing.ARG_LISTING_ID) { type = NavType.StringType }
            )
        ) { entry ->
            val listingId = entry.arguments?.getString(Screen.EditListing.ARG_LISTING_ID).orEmpty()
            Placeholder(name = "EditListing($listingId)")
        }

        composable(
            route = Screen.CreateOrder.route,
            arguments = listOf(
                navArgument(Screen.CreateOrder.ARG_LISTING_ID) { type = NavType.StringType }
            )
        ) { entry ->
            val listingId = entry.arguments?.getString(Screen.CreateOrder.ARG_LISTING_ID).orEmpty()
            Placeholder(name = "CreateOrder($listingId)")
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument(Screen.OrderDetail.ARG_ORDER_ID) { type = NavType.StringType }
            )
        ) { entry ->
            val orderId = entry.arguments?.getString(Screen.OrderDetail.ARG_ORDER_ID).orEmpty()
            Placeholder(name = "OrderDetail($orderId)")
        }

        composable(
            route = Screen.CreateReview.route,
            arguments = listOf(
                navArgument(Screen.CreateReview.ARG_ORDER_ID) { type = NavType.StringType }
            )
        ) { entry ->
            val orderId = entry.arguments?.getString(Screen.CreateReview.ARG_ORDER_ID).orEmpty()
            Placeholder(name = "CreateReview($orderId)")
        }

        composable(Screen.EditProfile.route) {
            Placeholder(name = "EditProfile")
        }

        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(
                navArgument(Screen.UserProfile.ARG_USER_ID) { type = NavType.StringType }
            )
        ) { entry ->
            val userId = entry.arguments?.getString(Screen.UserProfile.ARG_USER_ID).orEmpty()
            Placeholder(name = "UserProfile($userId)")
        }
    }
}

@Composable
private fun Placeholder(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Segera hadir: $name")
    }
}

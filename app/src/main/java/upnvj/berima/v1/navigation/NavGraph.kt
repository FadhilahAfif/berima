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
import upnvj.berima.v1.ui.home.HomeScreen
import upnvj.berima.v1.ui.home.SearchScreen
import upnvj.berima.v1.ui.listing.CreateListingScreen
import upnvj.berima.v1.ui.listing.EditListingScreen
import upnvj.berima.v1.ui.listing.ListingDetailScreen
import upnvj.berima.v1.ui.order.CreateOrderScreen
import upnvj.berima.v1.ui.order.OrderDetailScreen
import upnvj.berima.v1.ui.order.OrdersScreen
import upnvj.berima.v1.ui.profile.EditProfileScreen
import upnvj.berima.v1.ui.profile.ProfileScreen
import upnvj.berima.v1.ui.profile.UserProfileScreen
import upnvj.berima.v1.ui.review.CreateReviewScreen
import upnvj.berima.v1.ui.splash.SplashScreen
import upnvj.berima.v1.ui.verification.IdentityVerificationScreen
import upnvj.berima.v1.ui.verification.SkillVerificationScreen
import upnvj.berima.v1.ui.verification.VerificationCenterScreen

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
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Orders.route) {
            OrdersScreen(
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToCreateListing = {
                    navController.navigate(Screen.CreateListing.route)
                },
                onNavigateToVerificationCenter = {
                    navController.navigate(Screen.VerificationCenter.route)
                },
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ListingDetail.route,
            arguments = listOf(
                navArgument(Screen.ListingDetail.ARG_LISTING_ID) { type = NavType.StringType }
            )
        ) {
            ListingDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { listingId ->
                    navController.navigate(Screen.CreateOrder.createRoute(listingId))
                },
                onEditClick = { listingId ->
                    navController.navigate(Screen.EditListing.createRoute(listingId))
                },
                onSellerClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        composable(Screen.CreateListing.route) {
            CreateListingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditListing.route,
            arguments = listOf(
                navArgument(Screen.EditListing.ARG_LISTING_ID) { type = NavType.StringType }
            )
        ) {
            EditListingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CreateOrder.route,
            arguments = listOf(
                navArgument(Screen.CreateOrder.ARG_LISTING_ID) { type = NavType.StringType }
            )
        ) {
            CreateOrderScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderCreated = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId)) {
                        popUpTo(Screen.ListingDetail.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument(Screen.OrderDetail.ARG_ORDER_ID) { type = NavType.StringType }
            )
        ) {
            OrderDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onReviewClick = { orderId ->
                    navController.navigate(Screen.CreateReview.createRoute(orderId))
                }
            )
        }

        composable(
            route = Screen.CreateReview.route,
            arguments = listOf(
                navArgument(Screen.CreateReview.ARG_ORDER_ID) { type = NavType.StringType }
            )
        ) {
            CreateReviewScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.VerificationCenter.route) {
            VerificationCenterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIdentity = {
                    navController.navigate(Screen.IdentityVerification.route)
                },
                onNavigateToSkill = {
                    navController.navigate(Screen.SkillVerification.route)
                }
            )
        }

        composable(Screen.IdentityVerification.route) {
            IdentityVerificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SkillVerification.route) {
            SkillVerificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(
                navArgument(Screen.UserProfile.ARG_USER_ID) { type = NavType.StringType }
            )
        ) {
            UserProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                }
            )
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

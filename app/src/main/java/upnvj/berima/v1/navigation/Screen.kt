package upnvj.berima.v1.navigation

/**
 * All navigation destinations. Routes with arguments expose a
 * `createRoute(...)` helper so callers never build the string by hand.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object Home : Screen("home")
    data object Orders : Screen("orders")
    data object Profile : Screen("profile")

    data object ListingDetail : Screen("listing/{listingId}") {
        const val ARG_LISTING_ID = "listingId"
        fun createRoute(listingId: String) = "listing/$listingId"
    }

    data object CreateListing : Screen("listing/create")

    data object EditListing : Screen("listing/edit/{listingId}") {
        const val ARG_LISTING_ID = "listingId"
        fun createRoute(listingId: String) = "listing/edit/$listingId"
    }

    data object CreateOrder : Screen("order/create/{listingId}") {
        const val ARG_LISTING_ID = "listingId"
        fun createRoute(listingId: String) = "order/create/$listingId"
    }

    data object OrderDetail : Screen("order/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String) = "order/$orderId"
    }

    data object CreateReview : Screen("review/create/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String) = "review/create/$orderId"
    }

    data object EditProfile : Screen("profile/edit")

    data object UserProfile : Screen("user/{userId}") {
        const val ARG_USER_ID = "userId"
        fun createRoute(userId: String) = "user/$userId"
    }
}

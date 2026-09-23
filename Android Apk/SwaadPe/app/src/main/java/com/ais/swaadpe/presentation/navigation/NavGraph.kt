package com.ais.swaadpe.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import com.ais.swaadpe.presentation.screens.auth.LoginScreen
import com.ais.swaadpe.presentation.screens.auth.SplashScreen
import com.ais.swaadpe.presentation.screens.auth.OnboardingScreen
import com.ais.swaadpe.presentation.screens.home.HomeScreen
import com.ais.swaadpe.presentation.screens.menu.RestaurantMenuScreen
import com.ais.swaadpe.presentation.screens.cart.CartScreen
import com.ais.swaadpe.presentation.screens.checkout.CheckoutScreen
import com.ais.swaadpe.presentation.screens.order.OrderStatusScreen
import com.ais.swaadpe.presentation.screens.order.OrderSuccessScreen
import com.ais.swaadpe.presentation.screens.order.OrderDetailScreen
import com.ais.swaadpe.presentation.screens.profile.ProfileScreen
import com.ais.swaadpe.presentation.screens.profile.EditProfileScreen
import com.ais.swaadpe.presentation.screens.profile.OrderHistoryScreen
import com.ais.swaadpe.presentation.screens.profile.SupportScreen
import com.ais.swaadpe.presentation.screens.address.AddressBookScreen
import com.ais.swaadpe.presentation.screens.search.SearchScreen
import com.ais.swaadpe.presentation.screens.address.AddAddressScreen
import com.ais.swaadpe.presentation.screens.offer.CouponsScreen
import com.ais.swaadpe.presentation.viewmodels.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object Menu : Screen("menu/{restaurantId}/{restaurantName}") {
        fun createRoute(id: Int, name: String) = "menu/$id/$name"
    }
    object Cart : Screen("cart")
    object Coupons : Screen("coupons/{fromCart}") {
        fun createRoute(fromCart: Boolean) = "coupons/$fromCart"
    }
    object AddAddress : Screen("add_address")
    object AddressBook : Screen("address_book")
    object Checkout : Screen("checkout")
    object OrderSuccess : Screen("order_success/{orderNumber}") {
        fun createRoute(orderNumber: String) = "order_success/$orderNumber"
    }
    object OrderStatus : Screen("order_status/{orderNumber}") {
        fun createRoute(orderNumber: String) = "order_status/$orderNumber"
    }
    object OrderDetail : Screen("order_detail/{orderNumber}") {
        fun createRoute(orderNumber: String) = "order_detail/$orderNumber"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object OrderHistory : Screen("order_history")
    object Support : Screen("support")
}

@Composable
fun NavGraph(navController: NavHostController, preferenceManager: PreferenceManager) {
    // Safe Back Pop Function to prevent Black Screen
    val onBack: () -> Unit = {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                preferenceManager = preferenceManager,
                onSplashFinished = { isLoggedIn ->
                    val destination = if (isLoggedIn) Screen.Home.route else Screen.Onboarding.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { isNewUser ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGuestClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val cartViewModel: CartViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                cartViewModel = cartViewModel,
                onRestaurantClick = { id, name ->
                    navController.navigate(Screen.Menu.createRoute(id, name))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onTrackOrderClick = { orderNum ->
                    navController.navigate(Screen.OrderStatus.createRoute(orderNum))
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = onBack,
                onRestaurantClick = { id, name ->
                    navController.navigate(Screen.Menu.createRoute(id, name))
                }
            )
        }

        composable(
            route = Screen.Menu.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.IntType },
                navArgument("restaurantName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("restaurantId") ?: 0
            val name = backStackEntry.arguments?.getString("restaurantName") ?: ""
            val menuViewModel: MenuViewModel = hiltViewModel()
            val cartViewModel: CartViewModel = hiltViewModel()
            RestaurantMenuScreen(
                restaurantId = id,
                restaurantName = name,
                menuViewModel = menuViewModel,
                cartViewModel = cartViewModel,
                onBackClick = onBack,
                onGoToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = hiltViewModel()
            CartScreen(
                viewModel = cartViewModel,
                onBackClick = onBack,
                onCheckoutClick = {
                    navController.navigate(Screen.Checkout.route)
                },
                onAddNewAddressClick = {
                    navController.navigate(Screen.AddAddress.route)
                },
                onOffersClick = {
                    navController.navigate(Screen.Coupons.createRoute(true))
                }
            )
        }

        composable(
            route = Screen.Coupons.route,
            arguments = listOf(navArgument("fromCart") { type = NavType.BoolType })
        ) { backStackEntry ->
            val fromCart = backStackEntry.arguments?.getBoolean("fromCart") ?: false
            val cartEntry = try {
                navController.getBackStackEntry(Screen.Cart.route)
            } catch (e: Exception) {
                null
            }
            val cartViewModel: CartViewModel = if (cartEntry != null) hiltViewModel(cartEntry) else hiltViewModel()
            CouponsScreen(
                viewModel = cartViewModel,
                isFromCart = fromCart,
                onBackClick = onBack
            )
        }

        composable(Screen.AddAddress.route) {
            val cartViewModel: CartViewModel = hiltViewModel()
            AddAddressScreen(
                onBackClick = onBack,
                onSaveClick = { tag, fullAddress, houseNo, lat, lng, name, phone ->
                    cartViewModel.addNewAddress(
                        tag = tag,
                        fullAddress = fullAddress,
                        houseNo = houseNo,
                        lat = lat,
                        lng = lng,
                        receiverName = name,
                        receiverPhone = phone,
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }

        composable(Screen.Checkout.route) { backStackEntry ->
            val cartEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Cart.route)
            }
            val cartViewModel: CartViewModel = hiltViewModel(cartEntry)

            CheckoutScreen(
                viewModel = cartViewModel,
                onBackClick = onBack,
                onOrderSuccess = {
                    val status = cartViewModel.orderStatus.value
                    val orderNum = if (status is OrderStatus.Success) status.orderNumber else "PENDING"
                    navController.navigate(Screen.OrderSuccess.createRoute(orderNum)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderNum = backStackEntry.arguments?.getString("orderNumber") ?: "N/A"
            OrderSuccessScreen(
                orderNumber = orderNum,
                onTrackOrderClick = { num ->
                    navController.navigate(Screen.OrderStatus.createRoute(num)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onContinueShoppingClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderStatus.route,
            arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderNum = backStackEntry.arguments?.getString("orderNumber") ?: "N/A"
            val orderViewModel: OrderViewModel = hiltViewModel()
            OrderStatusScreen(
                orderNumber = orderNum,
                viewModel = orderViewModel,
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderNum = backStackEntry.arguments?.getString("orderNumber") ?: ""
            val detailViewModel: OrderDetailsViewModel = hiltViewModel()
            OrderDetailScreen(
                orderNumber = orderNum,
                viewModel = detailViewModel,
                onBackClick = onBack
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = onBack,
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onOrdersClick = {
                    navController.navigate(Screen.OrderHistory.route)
                },
                onAddressBookClick = {
                    navController.navigate(Screen.AddressBook.route)
                },
                onCouponsClick = {
                    navController.navigate(Screen.Coupons.createRoute(false))
                },
                onSupportClick = {
                    navController.navigate(Screen.Support.route)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            EditProfileScreen(
                viewModel = profileViewModel,
                onBackClick = onBack
            )
        }

        composable(Screen.OrderHistory.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            OrderHistoryScreen(
                viewModel = profileViewModel,
                onBackClick = onBack,
                onOrderClick = { num ->
                    navController.navigate(Screen.OrderDetail.createRoute(num))
                },
                onViewMenuClick = { id, name ->
                    navController.navigate(Screen.Menu.createRoute(id, name))
                },
                onTrackOrderClick = { num ->
                    navController.navigate(Screen.OrderStatus.createRoute(num))
                }
            )
        }

        composable(Screen.AddressBook.route) {
            val cartViewModel: CartViewModel = hiltViewModel()
            AddressBookScreen(
                viewModel = cartViewModel,
                onBackClick = onBack,
                onAddNewAddressClick = {
                    navController.navigate(Screen.AddAddress.route)
                }
            )
        }

        composable(Screen.Support.route) {
            SupportScreen(onBackClick = onBack)
        }
    }
}

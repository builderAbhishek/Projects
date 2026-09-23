package com.ais.swaadpe.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ais.swaadpe.presentation.navigation.Screen

@Composable
fun ModernBottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Orders", Screen.OrderHistory.route, Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
        BottomNavItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
    )

    // Show only on main tabs
    val mainRoutes = listOf(Screen.Home.route, Screen.OrderHistory.route, Screen.Profile.route)
    val isVisible = currentDestination?.route in mainRoutes
    if (!isVisible) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Bar Container
        Row(
            modifier = Modifier
                .height(56.dp) 
                .fillMaxWidth(0.75f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.2f),
                    spotColor = Color(0xFF2E7D32).copy(alpha = 0.3f)
                )
                // Consume all clicks on the nav bar itself so they don't reach items behind it
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Do nothing, just consume click */ }
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color(0xFFF9FFF9).copy(alpha = 0.95f)
                        )
                    )
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                BottomNavItemView(
                    item = item,
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val themeColor = Color(0xFF2E7D32)
    val iconColor by animateColorAsState(
        targetValue = if (selected) themeColor else Color.Gray.copy(alpha = 0.6f),
        label = "color"
    )
    val scale by animateFloatAsState(if (selected) 1.15f else 1f, label = "scale")

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier
                    .size(22.dp)
                    .scale(scale)
            )
            
            if (selected) {
                Text(
                    text = item.label,
                    color = themeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

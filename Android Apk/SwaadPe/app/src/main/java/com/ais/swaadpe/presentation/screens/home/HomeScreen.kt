package com.ais.swaadpe.presentation.screens.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ais.swaadpe.R
import com.ais.swaadpe.domain.model.*
import com.ais.swaadpe.presentation.viewmodels.HomeUiState
import com.ais.swaadpe.presentation.viewmodels.HomeViewModel
import com.ais.swaadpe.presentation.viewmodels.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    onRestaurantClick: (Int, String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onTrackOrderClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchLoading by viewModel.isSearchLoading.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartConflict by cartViewModel.cartConflict.collectAsState(initial = null)
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.refreshLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        cartViewModel.uiMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (cartConflict != null) {
        AlertDialog(
            onDismissRequest = { cartViewModel.resetConflict() },
            title = { Text("Replace cart items?",
                style = TextStyle(color = Color(0xFF000000), fontSize = 20.sp, fontWeight = FontWeight.Bold)) },
            text = { Text("Your cart contains items from another restaurant. Do you want to clear the cart and add items from ${cartConflict?.vendorName} instead?",
                style = TextStyle(color = Color(0xFF1A1A1A))) },
            confirmButton = {
                Button(
                    onClick = { cartViewModel.clearAndAddToCart(cartConflict!!) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Replace", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cartViewModel.resetConflict() }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (searchQuery.isNotEmpty()) {
        BackHandler {
            viewModel.onSearchQueryChange("")
            focusManager.clearFocus()
        }
    }

    Scaffold(
        topBar = {
            val location = (uiState as? HomeUiState.Success)?.locationText ?: "Fetching location..."
            PremiumHomeHeader(location, onProfileClick) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        containerColor = Color(0xFFF7F9F7) 
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    placeholder = { Text("Search for dishes (e.g. Paneer, Thali)...", fontSize = 14.sp) },
                    leadingIcon = { 
                        if (isSearchLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF4CAF50))
                        } else {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF4CAF50)) 
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.onSearchQueryChange("")
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Default.Clear, null, tint = Color.Gray)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "HomeScreenContent"
                ) { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF4CAF50))
                            }
                        }
                        is HomeUiState.Error -> {
                            Column(modifier = Modifier.fillMaxSize().padding(32.dp), 
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                Icon(painterResource(R.drawable.logo), null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                                Spacer(Modifier.height(16.dp))
                                Text(text = state.message, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Spacer(Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.fetchHomeData() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) { Text("Retry") }
                            }
                        }
                        is HomeUiState.Success -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 160.dp)
                            ) {
                                if (searchQuery.isEmpty()) {
                                    item(key = "active_order") {
                                        state.activeOrder?.let { order ->
                                            ActiveOrderBanner(order) {
                                                onTrackOrderClick(order.orderNumber)
                                            }
                                        }
                                    }
                                    
                                    if (state.banners.isNotEmpty()) {
                                        item(key = "banners") { PromoCarousel(state.banners) }
                                    }

                                    if (state.categories.isNotEmpty()) {
                                        item(key = "cat_title") { SectionTitle("Quick Cravings") }
                                        item(key = "categories") { PremiumCategoryRow(state.categories) }
                                    }

                                    if (state.topPicks.isNotEmpty()) {
                                        item(key = "top_picks") {
                                            TopPicksSection(state.topPicks) { pick ->
                                                val restaurant = state.restaurants.find { it.id == pick.vendorId }
                                                cartViewModel.addToCart(
                                                    MenuItem(pick.id, pick.name, "", pick.price, pick.imageUrl, true),
                                                    pick.vendorId,
                                                    pick.vendorName,
                                                    isOpen = restaurant?.isOpen ?: true
                                                )
                                            }
                                        }
                                    }
                                    
                                    item(key = "rest_title") { SectionTitle("Top Restaurants") }
                                    
                                    items(
                                        items = state.restaurants,
                                        key = { it.id },
                                        contentType = { "restaurant" }
                                    ) { restaurant ->
                                        PremiumRestaurantCard(restaurant) {
                                            onRestaurantClick(restaurant.id, restaurant.name)
                                        }
                                    }
                                } else {
                                    item(key = "search_results_title") { SectionTitle("Matched Dishes") }
                                    
                                    if (state.matchedItems.isEmpty() && !isSearchLoading) {
                                        item(key = "no_results") {
                                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                                Text("No dishes found. Try searching 'Thali' or 'Paneer'", color = Color.Gray)
                                            }
                                        }
                                    } else {
                                        items(
                                            items = state.matchedItems,
                                            key = { "dish_${it.id}" },
                                            contentType = { "dish" }
                                        ) { item ->
                                            val restaurant = state.restaurants.find { it.id == item.vendorId }
                                            SearchItemCard(
                                                item = item,
                                                onAddClick = {
                                                    cartViewModel.addToCart(
                                                        item = MenuItem(
                                                            id = item.id,
                                                            name = item.name,
                                                            description = item.description,
                                                            price = item.price,
                                                            imageUrl = item.imageUrl,
                                                            isAvailable = item.isAvailable
                                                        ),
                                                        vendorId = item.vendorId,
                                                        vendorName = item.vendorName,
                                                        isOpen = restaurant?.isOpen ?: true
                                                    )
                                                }
                                            )
                                        }
                                    }
                                    
                                    if (state.restaurants.isNotEmpty()) {
                                        item(key = "matching_rest_title") { SectionTitle("Matching Restaurants") }
                                        items(
                                            items = state.restaurants,
                                            key = { "matching_rest_${it.id}" },
                                            contentType = { "restaurant" }
                                        ) { restaurant ->
                                            PremiumRestaurantCard(restaurant) {
                                                onRestaurantClick(restaurant.id, restaurant.name)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (cartItems.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 78.dp)
                            .padding(horizontal = 12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(12.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onCartClick()
                                    cartViewModel.syncCartWithServer()
                                },
                            color = Color(0xFF2E7D32)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cartItems.size.toString(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "₹${cartViewModel.getTotalPrice().toInt()}",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "plus taxes",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "VIEW CART",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopPicksSection(items: List<TopPick>, onAddClick: (TopPick) -> Unit) {
    Column {
        SectionTitle("Recommended for You")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items, key = { "top_pick_${it.id}" }) { item ->
                Card(
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(144.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, maxLines = 1)
                        Text(item.vendorName, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("₹${item.price.toInt()}", fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                            IconButton(onClick = { onAddClick(item) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.AddCircle, null, tint = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveOrderBanner(order: ActiveOrder, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        color = Color(0xFFFFF3E0),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFB74D))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Fastfood, null, tint = Color(0xFFFF9800))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Ongoing Order #${order.orderNumber}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                Text("Status: ${order.status}", fontSize = 12.sp, color = Color.DarkGray)
            }
            Text("Track ➔", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun SearchItemCard(item: MatchedItem, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.logo)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black))
                Text(text = "from ${item.vendorName}", style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                Text(text = "₹${item.price.toInt()}", style = TextStyle(fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32)))
            }
            Button(
                onClick = onAddClick,
                enabled = item.isAvailable,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = if (item.isAvailable) "ADD" else "N/A", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PremiumHomeHeader(location: String, onProfileClick: () -> Unit, onLocationClick: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding() 
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onLocationClick() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = location,
                    style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black),
                    maxLines = 1
                )
                Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Black, modifier = Modifier.size(20.dp))
            }
            Text(
                text = "Tap to refresh location",
                style = TextStyle(fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Normal)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.AccountCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun PromoCarousel(banners: List<Banner>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(banners, key = { it.id }) { banner ->
            Card(
                modifier = Modifier
                    .width(310.dp)
                    .height(170.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun PremiumCategoryRow(categories: List<Category>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {}.width(75.dp)
            ) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = category.iconUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(R.drawable.logo)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    category.name,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PremiumRestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                startY = 300f
                            )
                        )
                )

                if (restaurant.isPureVeg) {
                    Box(
                        modifier = Modifier.padding(12.dp).align(Alignment.TopEnd).background(Color.White.copy(0.9f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text("100% VEG", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        }
                    }
                }
                
                Box(
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).background(Color(0xFF4CAF50), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        restaurant.distanceDisplay,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                if (!restaurant.isOpen) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CLOSED NOW", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            restaurant.name,
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        )
                        Text(
                            restaurant.cuisineTags ?: "Multi-Cuisine",
                            style = TextStyle(fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        )
                    }
                    Box(
                        modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                restaurant.rating.toString(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = Color(0xFF2E7D32)
                            )
                            Icon(Icons.Default.Star, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F1F1))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.spverified), 
                            contentDescription = null, 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("SwaadPe Verified", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Text(
                        "🕒 ${restaurant.deliveryTime}",
                        style = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black),
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp)
    )
}

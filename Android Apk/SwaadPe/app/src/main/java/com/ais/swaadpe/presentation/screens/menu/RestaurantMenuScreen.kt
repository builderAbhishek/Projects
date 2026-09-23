package com.ais.swaadpe.presentation.screens.menu

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ais.swaadpe.R
import com.ais.swaadpe.domain.model.MenuCategory
import com.ais.swaadpe.domain.model.MenuItem
import com.ais.swaadpe.domain.model.VendorDetails
import com.ais.swaadpe.presentation.viewmodels.CartItem
import com.ais.swaadpe.presentation.viewmodels.CartViewModel
import com.ais.swaadpe.presentation.viewmodels.MenuUiState
import com.ais.swaadpe.presentation.viewmodels.MenuViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuScreen(
    restaurantId: Int,
    restaurantName: String,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onGoToCart: () -> Unit
) {
    val uiState by menuViewModel.uiState.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartConflict by cartViewModel.cartConflict.collectAsState(initial = null)
    
    val totalInCart = remember(cartItems) { cartViewModel.getTotalPrice() }
    val animatedPrice by animateIntAsState(targetValue = totalInCart.toInt(), label = "PriceAnimation")
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(restaurantId) {
        menuViewModel.fetchMenu(restaurantId)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = restaurantName, 
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
                        )
                        Text(
                            text = "Shohratgarh • Pure Veg",
                            style = TextStyle(fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = cartItems.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { 
                            onGoToCart() 
                            cartViewModel.syncCartWithServer()
                        },
                    color = Color(0xFF2E7D32)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${cartItems.size} Item${if(cartItems.size > 1) "s" else ""} | ₹$animatedPrice", 
                                color = Color.White, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontSize = 16.sp
                            )
                            Text("Click to checkout", color = Color.White.copy(0.9f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("VIEW CART", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White.copy(0.2f), CircleShape)
                                    .border(1.dp, Color.White.copy(0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                                    contentDescription = null, 
                                    tint = Color.White, 
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is MenuUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                }
                is MenuUiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = Color.Red, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { menuViewModel.fetchMenu(restaurantId) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { 
                            Text("Retry") 
                        }
                    }
                }
                is MenuUiState.Success -> {
                    val vendor = state.vendor
                    Column {
                        CategoryTabs(
                            categories = state.menu,
                            onCategoryClick = { index ->
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index + 1)
                                }
                            }
                        )

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            item(key = "header") { RestaurantInfoHeader(vendor) }
                            
                            state.menu.forEach { category ->
                                item(key = "cat_header_${category.name}") {
                                    Text(
                                        text = category.name,
                                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A)),
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 12.dp)
                                    )
                                }
                                items(
                                    items = category.items,
                                    key = { "${category.name}_${it.id}" },
                                    contentType = { "menu_item" }
                                ) { item ->
                                    val currentCartItem = cartItems.find { it.id == item.id }
                                    MenuItemCard(
                                        item = item,
                                        quantity = currentCartItem?.quantity ?: 0,
                                        onAddClick = { cartViewModel.addToCart(item, vendor.id, vendor.name) },
                                        onRemoveClick = { cartViewModel.removeFromCart(item.id) }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEFEFEF))
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
fun CategoryTabs(categories: List<MenuCategory>, onCategoryClick: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(
            items = categories,
            key = { _, cat -> cat.name }
        ) { index, category ->
            Surface(
                modifier = Modifier.clickable { onCategoryClick(index) },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF5F5F5),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Text(
                    text = category.name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                )
            }
        }
    }
}

@Composable
fun RestaurantInfoHeader(vendor: VendorDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = vendor.name, style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A)))
                    Spacer(Modifier.height(4.dp))
                    Text(text = vendor.address ?: "Pure Veg Restaurant", style = TextStyle(color = Color(0xFF616161), fontSize = 13.sp, fontWeight = FontWeight.Medium))
                }
                AsyncImage(
                    model = vendor.image,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoItem(Icons.Default.Star, "${vendor.rating} (100+)", Color(0xFF2E7D32))
                InfoItem(painterResource(R.drawable.logo), vendor.deliveryTime, Color(0xFF1A1A1A))
                InfoItem(null, "Min ₹${vendor.minOrder.toInt()}", Color(0xFF616161))
            }
        }
    }
}

@Composable
fun InfoItem(icon: Any?, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon is androidx.compose.ui.graphics.vector.ImageVector) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        } else if (icon is androidx.compose.ui.graphics.painter.Painter) {
            Icon(icon, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(6.dp))
        Text(text = text, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = if(color == Color(0xFF616161)) color else Color(0xFF1A1A1A))
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem, 
    quantity: Int,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val isAvailable = item.isAvailable
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(if (isAvailable) 1f else 0.6f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .border(1.dp, Color(0xFF4CAF50))
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A)))
            Text(text = "₹${item.price.toInt()}", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242)))
            
            if (item.description != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = item.description, style = TextStyle(fontSize = 12.sp, color = Color(0xFF757575), lineHeight = 16.sp), maxLines = 3)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Box(contentAlignment = Alignment.BottomCenter) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )
            
            if (isAvailable) {
                if (quantity > 0) {
                    Surface(
                        modifier = Modifier
                            .offset(y = 12.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(onClick = onRemoveClick, modifier = Modifier.size(32.dp)) {
                                Text("-", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                            }
                            Text(
                                text = quantity.toString(),
                                modifier = Modifier.padding(horizontal = 10.dp),
                                style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF4CAF50))
                            )
                            IconButton(onClick = onAddClick, modifier = Modifier.size(32.dp)) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                            }
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .offset(y = 12.dp)
                            .clickable { onAddClick() }
                            .shadow(8.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Text(
                            "ADD", 
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 10.dp),
                            color = Color(0xFF2E7D32), 
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.offset(y = 12.dp),
                    color = Color.White.copy(0.95f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(
                        "UNAVAILABLE", 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Color.Gray, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

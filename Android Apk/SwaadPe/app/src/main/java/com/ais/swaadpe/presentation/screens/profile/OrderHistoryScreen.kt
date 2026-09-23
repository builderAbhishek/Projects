package com.ais.swaadpe.presentation.screens.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ais.swaadpe.data.remote.dto.MyOrderDto
import com.ais.swaadpe.presentation.viewmodels.OrderHistoryState
import com.ais.swaadpe.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    onViewMenuClick: (Int, String) -> Unit,
    onTrackOrderClick: (String) -> Unit
) {
    val historyState by viewModel.orderHistoryState.collectAsState()
    val activeState by viewModel.activeOrdersState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val themeColor = Color(0xFF3BB742)
    val backgroundColor = Color(0xFFF7F9F7)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Orders", style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // --- Search Bar ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                placeholder = { Text("Search by restaurant or dish", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFFE53935)) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Active Orders Section
                if (activeState is OrderHistoryState.Success) {
                    val activeOrders = (activeState as OrderHistoryState.Success).orders
                    if (activeOrders.isNotEmpty()) {
                        item {
                            Text(text = "Active Orders", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = themeColor)
                        }
                        items(activeOrders) { order ->
                            OrderCard(
                                order = order, 
                                themeColor = themeColor, 
                                onClick = { onOrderClick(order.orderNumber) },
                                onViewMenuClick = { onViewMenuClick(order.vendor.id, order.vendor.name) },
                                onTrackOrderClick = { onTrackOrderClick(order.orderNumber) }
                            )
                        }
                    }
                }

                // Past Orders Section
                item {
                    Text(text = "Past Orders", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black)
                }

                when (val state = historyState) {
                    is OrderHistoryState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = themeColor)
                            }
                        }
                    }
                    is OrderHistoryState.Success -> {
                        val filteredOrders = state.orders.filter { 
                            it.vendor.name.contains(searchQuery, ignoreCase = true) || 
                            it.items.any { item -> item.name.contains(searchQuery, ignoreCase = true) }
                        }
                        
                        if (filteredOrders.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("No orders match your search", color = Color.Gray, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            items(filteredOrders) { order ->
                                OrderCard(
                                    order = order, 
                                    themeColor = themeColor, 
                                    onClick = { onOrderClick(order.orderNumber) },
                                    onViewMenuClick = { onViewMenuClick(order.vendor.id, order.vendor.name) },
                                    onTrackOrderClick = { onTrackOrderClick(order.orderNumber) }
                                )
                            }
                        }
                    }
                    is OrderHistoryState.Error -> {
                        item {
                            Text(text = state.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "swaadpe", 
                        style = TextStyle(
                            fontSize = 48.sp, 
                            fontWeight = FontWeight.Black, 
                            color = Color.LightGray.copy(alpha = 0.3f),
                            textAlign = TextAlign.Left
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: MyOrderDto, 
    themeColor: Color, 
    onClick: () -> Unit,
    onViewMenuClick: () -> Unit,
    onTrackOrderClick: () -> Unit
) {
    val isFailed = order.paymentStatus.lowercase() == "failed" || order.orderStatus.lowercase() == "failed" || order.orderStatus.lowercase() == "cancelled"
    val isActive = order.orderStatus.lowercase() != "delivered" && !isFailed
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Top Section: Vendor Info
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = order.vendor.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.vendor.name, 
                        style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color.Black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = order.vendor.address,
                        style = TextStyle(fontSize = 13.sp, color = Color.Gray),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.clickable { onViewMenuClick() }
                    ) {
                        Text(text = "View menu", style = TextStyle(fontSize = 12.sp, color = Color(
                            0xFF002BFF
                        ), fontWeight = FontWeight.Medium))
                        Icon(Icons.Default.PlayArrow, null, tint = Color(0xFF002BFF), modifier = Modifier.size(10.dp))
                    }
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (isActive) {
                            DropdownMenuItem(
                                text = { Text("Track Order") },
                                onClick = {
                                    showMenu = false
                                    onTrackOrderClick()
                                },
                                leadingIcon = { Icon(Icons.Outlined.TrackChanges, null) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Support") },
                            onClick = { showMenu = false }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

            // 2. Middle Section: Items with Food Type Icons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                order.items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Food Type Indicator (Veg/Non-Veg)
                        val iconColor = if (item.foodType.lowercase() == "veg") Color(0xFF4CAF50) else Color(0xFFE53935)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.dp, iconColor)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(5.dp).background(iconColor, CircleShape))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${item.quantity} x ${item.name}", 
                            style = TextStyle(fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            DashedDivider()
            Spacer(Modifier.height(12.dp))

            // 3. Info Section: Date and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.formattedDate, 
                        style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                    )
                    if (!isFailed) {
                        Text(
                            text = order.orderStatus, 
                            style = TextStyle(
                                fontSize = 13.sp, 
                                color = if(order.orderStatus == "Delivered") Color.Gray else themeColor, 
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${order.amount.toInt()}", 
                        style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.Black)
                    )
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }

            // 4. Footer Section: Status specific
            if (isFailed) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ErrorOutline, null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if(order.paymentStatus.lowercase() == "failed") "Payment failed" else "Order ${order.orderStatus}", 
                                color = Color(0xFFE53935), 
                                fontSize = 13.sp, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "If the payment is successful, the refund will be initiated within 72 hours.", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                    Button(
                        onClick = { /* Reorder Logic using order.vendor.id */ },
                        enabled = order.vendor.isOpen,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935).copy(0.9f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Replay, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reorder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DashedDivider() {
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = Color.LightGray.copy(alpha = 0.5f),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = 1.dp.toPx()
        )
    }
}

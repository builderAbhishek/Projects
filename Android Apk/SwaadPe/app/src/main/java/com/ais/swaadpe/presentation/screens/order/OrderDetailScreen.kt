package com.ais.swaadpe.presentation.screens.order

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ais.swaadpe.data.remote.dto.*
import com.ais.swaadpe.presentation.viewmodels.OrderDetailsState
import com.ais.swaadpe.presentation.viewmodels.OrderDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderNumber: String,
    viewModel: OrderDetailsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderNumber) {
        viewModel.getOrderDetails(orderNumber)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Order Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    TextButton(onClick = { /* Support Action */ }) {
                        Icon(Icons.Outlined.HeadsetMic, contentDescription = null, tint = Color(
                            0xFFFF0000
                        ), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Support", color = Color(0xFFFF0303), fontWeight = FontWeight.Medium)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF4F6F9)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val currentState = state) {
                is OrderDetailsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFE23744))
                }
                is OrderDetailsState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(currentState.message, textAlign = TextAlign.Center, color = Color.Black)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.getOrderDetails(orderNumber) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is OrderDetailsState.Success -> {
                    val data = currentState.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Block 1: Header Status
                        item {
                            StatusCard(data.header)
                        }

                        // Block 2: Combined Vendor & Items Card
                        item {
                            VendorAndItemsCard(data.vendor, data.orderItems)
                        }

                        // Block 3: Bill Summary
                        item {
                            BillSummaryCard(data.billSummary)
                        }

                        // Dynamic Tap to Pay Button
                        if (data.paymentInfo.showPayNowButton) {
                            item {
                                TapToPayButton(
                                    amount = data.paymentInfo.payableAmount,
                                    onClick = { /* Launch Payment Gateway */ }
                                )
                            }
                        }

                        // Block 4: Customer & Payment Details
                        item {
                            OrderInfoCard(data.customer, data.paymentInfo)
                        }
                        
                        // FSSAI Info
                        item {
                            FssaiFooter(data.fssaiInfo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(header: OrderDetailsHeader) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = Color(0xFF43A047),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = header.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun VendorAndItemsCard(vendor: OrderDetailsVendor, orderItems: OrderDetailsItems) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Vendor Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = vendor.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(vendor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(vendor.address, fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(
                    onClick = { /* Call Action */ },
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, Color(0xFFE8F5E9), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = null,
                        tint = Color(0xFF43A047),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

            // Items Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(orderItems.orderIdDisplay, fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.height(12.dp))
            orderItems.items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VegNonVegIndicator(isVeg = item.isVeg)
                    Spacer(Modifier.width(8.dp))
                    Text(item.displayText, fontSize = 14.sp, modifier = Modifier.weight(1f), color = Color.Black)
                    Text(item.totalPriceFormatted, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun TapToPayButton(amount: Double, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "tap_to_pay_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF43A047), Color(0xFF66BB6A))
                )
            )
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text(
                text = "TAP TO PAY ₹$amount",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun VegNonVegIndicator(isVeg: Boolean) {
    val color = if (isVeg) Color(0xFF43A047) else Color(0xFFBF360C)
    Box(
        modifier = Modifier
            .size(14.dp)
            .border(1.dp, color, RoundedCornerShape(2.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
fun BillSummaryCard(bill: OrderDetailBillSummaryDto) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Receipt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Bill Summary", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* Download */ }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(
                            0xFF007205
                        )
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                
                BillRow("Item total", bill.itemTotal)
                BillRow("GST (govt. taxes)", bill.gstTaxes)
                
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("Delivery partner fee", fontSize = 14.sp, color = Color.Gray)
                    Spacer(Modifier.weight(1f))
                    if (bill.isDeliveryFree) {
                        Text(bill.deliveryFee, fontSize = 14.sp, color = Color.Gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                        Spacer(Modifier.width(4.dp))
                        Text("FREE", fontSize = 14.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    } else {
                        Text(bill.deliveryFee, fontSize = 14.sp, color = Color.Black)
                    }
                }
                
                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Grand total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    Text(bill.grandTotal, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Discount", fontSize = 14.sp, color = Color(0xFF3BC441))
                    Spacer(Modifier.weight(1f))
                    Text(bill.discount, fontSize = 14.sp, color = Color(0xFF3BC441))
                }
                
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Paid", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    Text(bill.paid, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
        
        if (bill.savingsBannerShow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(bill.savingsMessage, color = Color(0xFF1976D2), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun BillRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        Text(value, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
fun OrderInfoCard(customer: OrderDetailsCustomer, payment: OrderDetailPaymentInfoDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Customer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F2F5)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(customer.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(customer.maskedMobile, fontSize = 13.sp, color = Color.Gray)
                }
            }
            
            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))
            
            // Payment Method
            InfoRow(Icons.Outlined.Payment, "Payment method", payment.method)
            
            Spacer(Modifier.height(16.dp))
            
            // Payment Date
            InfoRow(Icons.Outlined.CalendarToday, "Payment date", payment.date)
            
            Spacer(Modifier.height(16.dp))
            
            // Address
            InfoRow(Icons.Outlined.LocationOn, "Delivery address", payment.deliveryAddress)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String, value: String) {
    Row {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(value, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun FssaiFooter(fssaiInfo: FssaiInfoDto) {
    if (fssaiInfo.showFssai) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("fssai", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
            Text(fssaiInfo.licenseText, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

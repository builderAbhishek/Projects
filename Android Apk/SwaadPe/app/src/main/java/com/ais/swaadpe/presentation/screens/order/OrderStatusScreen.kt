package com.ais.swaadpe.presentation.screens.order

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.data.remote.dto.TrackingData
import com.ais.swaadpe.presentation.viewmodels.OrderViewModel
import com.ais.swaadpe.presentation.viewmodels.TrackingUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(
    orderNumber: String,
    viewModel: OrderViewModel,
    onHomeClick: () -> Unit
) {
    val uiState by viewModel.trackingState.collectAsState()
    val themeColor = Color(0xFF2CC72C)
    val backgroundColor = Color(0xFFF7F9F7)

    // Polling starts here
    DisposableEffect(orderNumber) {
        viewModel.startPolling(orderNumber)
        onDispose {
            viewModel.stopPolling()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order", style = TextStyle(fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 20.sp)) },
                navigationIcon = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.Close, tint = Color.Black, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is TrackingUiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = themeColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Connecting to server...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                is TrackingUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color.Red, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = state.message, color = Color.Black, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.startPolling(orderNumber) }, colors = ButtonDefaults.buttonColors(containerColor = themeColor)) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is TrackingUiState.Success -> {
                    TrackingSuccessContent(
                        data = state.data,
                        themeColor = themeColor,
                        onHomeClick = onHomeClick
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun TrackingSuccessContent(
    data: TrackingData,
    themeColor: Color,
    onHomeClick: () -> Unit
) {
    // Show progress chronologically: Oldest (top) to Newest (bottom)
    val chronologicalTimeline = remember(data.trackingTimeline) {
        data.trackingTimeline.reversed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = themeColor.copy(0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when(data.orderInfo.currentStatus.lowercase()) {
                        "delivered" -> Icons.Default.CheckCircle
                        "out for delivery" -> Icons.AutoMirrored.Filled.DirectionsBike
                        else -> Icons.Default.Restaurant
                    },
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(45.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.orderInfo.currentStatus,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        )
        Text(
            text = "Order ID: #${data.orderInfo.orderNumber}",
            style = TextStyle(fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Rider Section ---
        data.riderInfo?.let { rider ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.5.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(themeColor.copy(0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Outlined.DirectionsBike, null, tint = themeColor)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = rider.name, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black))
                        Text(text = "Delivery Partner Assigned", style = TextStyle(color = Color.Gray, fontSize = 12.sp))
                    }
                    IconButton(onClick = { /* Call Phone Logic */ }, modifier = Modifier.background(themeColor, CircleShape)) {
                        Icon(Icons.Outlined.Call, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- Tracking Timeline ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.5.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Order Tracking",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                chronologicalTimeline.forEachIndexed { index, log ->
                    TimelineRow(
                        title = log.status,
                        description = log.message,
                        time = log.createdAt,
                        isLast = index == chronologicalTimeline.size - 1,
                        themeColor = themeColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { /* Help */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Outlined.SupportAgent, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Support", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onHomeClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Go Home", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TimelineRow(
    title: String,
    description: String,
    time: String,
    isLast: Boolean,
    themeColor: Color
) {
    val formattedTime = remember(time) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a, dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(time)
            date?.let { outputFormat.format(it) } ?: time
        } catch (e: Exception) {
            time
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier.width(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(if (isLast) 24.dp else 12.dp)
                    .clip(CircleShape)
                    .background(themeColor),
                contentAlignment = Alignment.Center
            ) {
                if (isLast) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(themeColor.copy(alpha = 0.3f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = if (isLast) FontWeight.ExtraBold else FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(text = formattedTime, style = TextStyle(fontSize = 11.sp, color = Color.Gray))
            }
            Text(
                text = description,
                style = TextStyle(fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Normal)
            )
        }
    }
}

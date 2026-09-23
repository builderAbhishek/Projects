package com.ais.swaadpe.presentation.screens.offer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.data.remote.dto.CouponDto
import com.ais.swaadpe.presentation.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponsScreen(
    viewModel: CartViewModel,
    isFromCart: Boolean = false,
    onBackClick: () -> Unit
) {
    val coupons by viewModel.availableCoupons.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchCoupons()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Coupons", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F8FA)
    ) { padding ->
        if (isSyncing && coupons.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFEF4F5F))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Best Offers For You",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                if (coupons.isEmpty() && !isSyncing) {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No coupons available right now.", color = Color.Gray)
                        }
                    }
                }

                items(coupons) { coupon ->
                    CouponItem(
                        coupon = coupon,
                        isFromCart = isFromCart,
                        onApply = { 
                            viewModel.applyCoupon(coupon.code)
                            onBackClick()
                        },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Coupon Code", coupon.code)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Code ${coupon.code} Copied!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CouponItem(coupon: CouponDto, isFromCart: Boolean, onApply: () -> Unit, onCopy: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().alpha(if (coupon.isApplicable) 1f else 0.7f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (coupon.isApplicable) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (coupon.isApplicable) Icons.Default.LocalOffer else Icons.Default.Lock, 
                        contentDescription = null, 
                        tint = if (coupon.isApplicable) Color(0xFF2E7D32) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = coupon.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = coupon.subtitle,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (coupon.isApplicable && isFromCart) {
                    TextButton(onClick = onApply) {
                        Text(
                            text = "APPLY",
                            color = Color(0xFFEF4F5F),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8F9FA))
                        .border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(8.dp))
                        .clickable { onCopy() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = coupon.code,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = coupon.validTill, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    if (!coupon.isApplicable && coupon.lockedMessage.isNotEmpty()) {
                        Text(
                            text = coupon.lockedMessage,
                            fontSize = 11.sp,
                            color = Color(0xFFEF4F5F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

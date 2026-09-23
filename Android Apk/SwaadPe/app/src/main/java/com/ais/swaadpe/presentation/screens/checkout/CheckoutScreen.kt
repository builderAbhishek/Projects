package com.ais.swaadpe.presentation.screens.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.presentation.viewmodels.CartViewModel
import com.ais.swaadpe.presentation.viewmodels.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val billSummary by viewModel.billSummary.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val orderStatus by viewModel.orderStatus.collectAsState()
    
    // Using flows from ViewModel to ensure data is synced from CartScreen
    val receiverName by viewModel.receiverName.collectAsState()
    val receiverPhone by viewModel.receiverPhone.collectAsState()

    val paymentMethods = listOf("Cash on Delivery", "UPI on Delivery")
    var selectedPaymentMethod by remember { mutableStateOf(paymentMethods[0]) }

    val totalToPay = billSummary?.toPay ?: 0
    val totalSavings = ((billSummary?.itemSavings ?: 0.0) + (billSummary?.couponDiscount ?: 0.0) + (billSummary?.deliveryDiscount ?: 0.0)).toInt()

    LaunchedEffect(orderStatus) {
        if (orderStatus is OrderStatus.Success) {
            onOrderSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Checkout Review", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
                        Text(
                            "Confirm your details & pay",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        viewModel.placeOrder(
                            address = selectedAddress?.fullAddress ?: "",
                            paymentMethod = selectedPaymentMethod
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                        .navigationBarsPadding(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    enabled = selectedAddress != null && orderStatus !is OrderStatus.Loading
                ) {
                    if (orderStatus is OrderStatus.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Place Order • ₹$totalToPay", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        },
        containerColor = Color(0xFFF7F9F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Delivery Summary
            Text("Delivering to", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = selectedAddress?.addressTag ?: "Address",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = selectedAddress?.fullAddress ?: "No address selected",
                        style = TextStyle(fontSize = 14.sp, color = Color.Black, lineHeight = 20.sp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = receiverName.ifEmpty { "N/A" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = receiverPhone.ifEmpty { "N/A" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }

            // 2. Payment Review
            Text("Payment Method", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.selectableGroup().padding(vertical = 8.dp)) {
                    paymentMethods.forEach { method ->
                        val isSelected = method == selectedPaymentMethod
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .selectable(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    role = androidx.compose.ui.semantics.Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if(method.contains("UPI")) Icons.Outlined.AccountBalanceWallet else Icons.Outlined.Payments,
                                null,
                                tint = if(isSelected) Color(0xFF2E7D32) else Color.Gray
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = method,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if(isSelected) Color.Black else Color.DarkGray
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2E7D32))
                            )
                        }
                    }
                }
            }

            // 3. Complete Bill Breakdown
            Text("Bill Summary", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val originalItemTotal = (billSummary?.itemTotal ?: 0.0) + (billSummary?.itemSavings ?: 0.0)
                    DetailedBillRow(
                        label = "Item total",
                        value = "₹${billSummary?.itemTotal?.toInt() ?: 0}",
                        originalValue = if (originalItemTotal > (billSummary?.itemTotal ?: 0.0)) "₹${originalItemTotal.toInt()}" else null
                    )

                    if ((billSummary?.packagingCharges ?: 0.0) > 0) {
                        DetailedBillRow("Restaurant packaging charges", "₹${billSummary?.packagingCharges?.toInt() ?: 0}")
                    }

                    val originalDeliveryFee = (billSummary?.deliveryFee ?: 0.0) + (billSummary?.deliveryDiscount ?: 0.0)
                    DetailedBillRow(
                        label = "Delivery partner fee",
                        value = "₹${billSummary?.deliveryFee?.toInt() ?: 0}",
                        originalValue = if (originalDeliveryFee > (billSummary?.deliveryFee ?: 0.0)) "₹${originalDeliveryFee.toInt()}" else null
                    )

                    if ((billSummary?.platformFee ?: 0.0) > 0) {
                        DetailedBillRow("Platform fee", "₹${billSummary?.platformFee?.toInt() ?: 0}")
                    }

                    if ((billSummary?.gstTaxes ?: 0.0) > 0) {
                        DetailedBillRow("GST and Taxes", "₹${billSummary?.gstTaxes?.toInt() ?: 0}")
                    }

                    if ((billSummary?.couponDiscount ?: 0.0) > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Coupon Discount", color = Color(0xFF2E7D32), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("-₹${billSummary?.couponDiscount?.toInt() ?: 0}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)

                    DetailedBillRow(
                        label = "Grand Total",
                        value = "₹${billSummary?.grandTotal?.toInt() ?: 0}",
                        isBold = true
                    )

                    if ((billSummary?.roundOff ?: 0.0) != 0.0) {
                        DetailedBillRow("Cash round off", "₹${String.format("%.2f", billSummary?.roundOff ?: 0.0)}")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("To pay", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                        Text("₹$totalToPay", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.Black)
                    }

                    if (totalSavings > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🥳 You saved ₹$totalSavings on this order",
                                style = TextStyle(color = Color(0xFF1976D2), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            )
                        }
                    }
                }
            }

            // 4. Confirmation Badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Review your items one last time before placing order.",
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DetailedBillRow(
    label: String,
    value: String,
    originalValue: String? = null,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = if (isBold) Color.Black else Color.DarkGray,
            fontSize = 15.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (originalValue != null) {
                Text(
                    text = originalValue,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = value,
                fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}

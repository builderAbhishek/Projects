package com.ais.swaadpe.presentation.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val phoneNumber = "8917738295"
    val emailAddress = "support@swaadpe.com"

    val faqs = listOf(
        FAQItem("How to place an order?", "Browse your favorite restaurant, add items to cart, and proceed to checkout. Make sure your address is correct!"),
        FAQItem("How to track my order?", "Go to 'Profile' -> 'Order History' and tap on your current order to see its status."),
        FAQItem("Can I cancel my order?", "Orders can be cancelled within 60 seconds of placement. After that, restaurant might have started preparing it."),
        FAQItem("What if items are missing in my order?", "We're sorry! Please use the 'Call Us' option below and share your order ID with our team."),
        FAQItem("How to apply a coupon?", "In the cart screen, look for 'Apply Coupon' section before clicking on 'Proceed'."),
        FAQItem("What are SwaadPe points?", "These are loyalty rewards you earn on every order. 1 point = ₹1, which can be used for future orders.")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Help & Support", 
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4F5F).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HeadsetMic, 
                                contentDescription = null, 
                                tint = Color(0xFFEF4F5F),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "How can we help you?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2D2D2D)
                        )
                        Text(
                            "Search for your queries below",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            item {
                Text(
                    "Top Queries",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(faqs) { faq ->
                FAQCard(faq)
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ContactOptionsSection(
                    onCallClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                        context.startActivity(intent)
                    },
                    onEmailClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress"))
                        context.startActivity(intent)
                    },
                    phoneNumber = phoneNumber,
                    emailAddress = emailAddress
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

data class FAQItem(val question: String, val answer: String)

@Composable
fun FAQCard(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faq.question,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D2D2D)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (expanded) Color(0xFFEF4F5F) else Color.Gray
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F1F1))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 14.sp,
                        color = Color(0xFF555555),
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContactOptionsSection(
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    phoneNumber: String,
    emailAddress: String
) {
    Column {
        Text(
            "Contact Us",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ContactCard(
            icon = Icons.Default.Call,
            title = "Call Support",
            subtitle = "+91 $phoneNumber",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF2196F3),
            onClick = onCallClick
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ContactCard(
            icon = Icons.Default.Email,
            title = "Email Us",
            subtitle = emailAddress,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF9C27B0),
            onClick = onEmailClick
        )
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

package com.ais.swaadpe.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.presentation.viewmodels.ProfileState
import com.ais.swaadpe.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressBookClick: () -> Unit,
    onCouponsClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val themeColor = Color(0xFF2E7D32) // Green Theme Color

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Color.Black) },
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                ModernUserBanner(profileState, viewModel.userMobile, themeColor, onEditProfileClick)
            }

            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // Row 1: Orders & Addresses
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.ShoppingBag,
                            title = "Orders",
                            subtitle = "My History",
                            color = themeColor,
                            onClick = onOrdersClick
                        )
                        ModernActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.LocationOn,
                            title = "Addresses",
                            subtitle = "Saved Places",
                            color = Color(0xFFE91E63),
                            onClick = onAddressBookClick
                        )
                    }
                    // Row 2: Coupons & Support
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.ConfirmationNumber,
                            title = "Coupons",
                            subtitle = "Offers",
                            color = Color(0xFFFF9800),
                            onClick = onCouponsClick
                        )
                        ModernActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.SupportAgent,
                            title = "Support",
                            subtitle = "Help Center",
                            color = Color(0xFF2196F3),
                            onClick = onSupportClick
                        )
                    }
                }
            }

            item { SectionHeader("Account Information") }
            item {
                SettingsGroup {
                    ModernProfileMenuItem(Icons.Outlined.Lock, "Privacy Policy")
                    ModernProfileMenuItem(Icons.Outlined.Description, "Terms & Conditions")
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        viewModel.logout()
                        onLogoutClick()
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ModernUserBanner(state: ProfileState, fallbackMobile: String, themeColor: Color, onEditClick: () -> Unit) {
    val name = (state as? ProfileState.Success)?.profile?.name?.ifEmpty { "User" } ?: "Loading..."
    val mobile = (state as? ProfileState.Success)?.profile?.mobile ?: fallbackMobile

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(themeColor, themeColor.copy(alpha = 0.7f)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(mobile, fontSize = 14.sp, color = Color.DarkGray)
            }
            
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.background(Color(0xFFF5F5F5), CircleShape)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Black)
            }
        }
    }
}

@Composable
fun ModernActionCard(
    modifier: Modifier = Modifier, 
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    color: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 12.sp, color = Color.DarkGray)
                Text(subtitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun ModernProfileMenuItem(icon: ImageVector, title: String, trailingText: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFFF7F8FA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color.Black)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        if (trailingText != null) {
            Text(trailingText, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 8.dp))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

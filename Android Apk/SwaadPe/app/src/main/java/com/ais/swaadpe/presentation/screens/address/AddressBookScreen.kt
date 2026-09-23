package com.ais.swaadpe.presentation.screens.address

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.data.remote.dto.AddressDto
import com.ais.swaadpe.presentation.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressBookScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onAddNewAddressClick: () -> Unit
) {
    val addresses by viewModel.addresses.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAddresses()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Saved Places", 
                        style = TextStyle(
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewAddressClick,
                containerColor = Color(0xFFEF4F5F),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add New Address", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        if (addresses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No saved addresses found.", color = Color.Gray, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses) { address ->
                    AddressItemCard(address)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AddressItemCard(address: AddressDto) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDEBED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (address.addressTag.equals("Home", ignoreCase = true)) 
                        Icons.Outlined.Home else Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFEF4F5F),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.addressTag,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address.fullAddress,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF555555),
                        lineHeight = 20.sp
                    )
                )
                
                if (!address.receiverName.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${address.receiverName} • ${address.receiverPhone}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFF888888),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

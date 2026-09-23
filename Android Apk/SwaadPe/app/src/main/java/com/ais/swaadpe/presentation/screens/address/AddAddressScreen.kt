package com.ais.swaadpe.presentation.screens.address

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    onBackClick: () -> Unit,
    onSaveClick: (tag: String, fullAddress: String, houseNo: String, lat: Double, lng: Double, name: String, phone: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeColor = Color(0xFF2E7D32)

    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var fetchedLocationName by remember { mutableStateOf("Locating...") }
    var fullAddress by remember { mutableStateOf("") }
    var houseNoDetails by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Home") }
    var dontUseLocation by remember { mutableStateOf(false) }

    val receiverName = "Abhishek Chaudhary"
    val receiverPhone = "9137470661"

    fun fetchAddressName(lat: Double, lng: Double) {
        scope.launch {
            fetchedLocationName = "Fetching address..."
            val address = getAddressFromCoords(context, lat, lng)
            fetchedLocationName = address
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getCurrentLocation(context) { lat, lng ->
                latitude = lat
                longitude = lng
                fetchAddressName(lat, lng)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!dontUseLocation) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Address", fontWeight = FontWeight.ExtraBold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F8FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Checkbox: Don't use current location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { dontUseLocation = !dontUseLocation },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = dontUseLocation,
                    onCheckedChange = { dontUseLocation = it },
                    colors = CheckboxDefaults.colors(checkedColor = themeColor)
                )
                Text(
                    text = "Don't use current location coordinates",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Location Card (Visible only if dontUseLocation is false)
            if (!dontUseLocation) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(themeColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MyLocation, null, tint = themeColor, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-detected Area", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = fetchedLocationName,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }
                        Text(
                            "Refresh",
                            color = themeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                getCurrentLocation(context) { lat, lng ->
                                    latitude = lat
                                    longitude = lng
                                    fetchAddressName(lat, lng)
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            Text("Address Details", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            // Manual Full Address Box
            OutlinedTextField(
                value = fullAddress,
                onValueChange = { fullAddress = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                label = { Text("Area / Street / Sector*", color = Color.Gray) },
                placeholder = { Text("e.g. Ward No 4, Shohratgarh", color = Color.LightGray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = themeColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))

            // House No Field
            OutlinedTextField(
                value = houseNoDetails,
                onValueChange = { houseNoDetails = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                label = { Text("House No. / Flat / Landmark", color = Color.Gray) },
                placeholder = { Text("e.g. Near Shiv Temple", color = Color.LightGray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = themeColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(24.dp))

            Text("Save Address As", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ModernAddressTag("Home", Icons.Outlined.Home, selectedTag == "Home", themeColor) { selectedTag = "Home" }
                ModernAddressTag("Work", Icons.Outlined.WorkOutline, selectedTag == "Work", themeColor) { selectedTag = "Work" }
                ModernAddressTag("Other", Icons.Outlined.LocationOn, selectedTag == "Other", themeColor) { selectedTag = "Other" }
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    onSaveClick(
                        selectedTag,
                        fullAddress,
                        houseNoDetails,
                        if (dontUseLocation) 0.0 else latitude,
                        if (dontUseLocation) 0.0 else longitude,
                        receiverName,
                        receiverPhone
                    )
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = fullAddress.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Text("Save and Proceed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationReceived: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(it.latitude, it.longitude)
        }
    }
}

suspend fun getAddressFromCoords(context: Context, lat: Double, lng: Double): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { continuation ->
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        val result = if (addresses.isNotEmpty()) {
                            addresses[0].getAddressLine(0) ?: "Coordinates fetched"
                        } else "Coordinates fetched"
                        continuation.resume(result)
                    }
                }
            } else {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0) ?: "Coordinates fetched"
                } else {
                    "Coordinates fetched"
                }
            }
        } catch (e: Exception) {
            "Coordinates fetched"
        }
    }
}

@Composable
fun ModernAddressTag(label: String, icon: ImageVector, isSelected: Boolean, themeColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) themeColor else Color(0xFFE0E0E0)),
        color = if (isSelected) themeColor.copy(alpha = 0.08f) else Color.White
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) themeColor else Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, color = if (isSelected) themeColor else Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

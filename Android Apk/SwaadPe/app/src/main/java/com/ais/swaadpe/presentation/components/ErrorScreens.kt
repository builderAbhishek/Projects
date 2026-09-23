package com.ais.swaadpe.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FullScreenError(
    title: String,
    message: String,
    buttonText: String = "Try Again",
    icon: ImageVector = Icons.Default.Warning,
    iconColor: Color = Color(0xFFEF4F5F),
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message,
                fontSize = 15.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun NoInternetScreen(onRetry: () -> Unit) {
    FullScreenError(
        title = "No Connection",
        message = "Please check your internet connection and try again to enjoy your favorite meals.",
        buttonText = "Try Again",
        icon = Icons.Default.CloudOff,
        onButtonClick = onRetry
    )
}

@Composable
fun PermissionDeniedScreen(onGrant: () -> Unit) {
    FullScreenError(
        title = "Location Access Needed",
        message = "We need your location to show nearby restaurants and deliver your food accurately.",
        buttonText = "Grant Permission",
        icon = Icons.Default.LocationOff,
        iconColor = Color(0xFF4A90E2),
        onButtonClick = onGrant
    )
}

@Composable
fun ServerErrorScreen(onRetry: () -> Unit) {
    FullScreenError(
        title = "Something Went Wrong",
        message = "Our servers are taking a small break. Please try again after a few moments.",
        buttonText = "Try Again",
        icon = Icons.Default.Warning,
        iconColor = Color(0xFFF5A623),
        onButtonClick = onRetry
    )
}

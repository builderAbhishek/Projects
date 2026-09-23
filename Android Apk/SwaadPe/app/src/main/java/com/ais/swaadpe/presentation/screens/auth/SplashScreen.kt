package com.ais.swaadpe.presentation.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.R
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    preferenceManager: PreferenceManager,
    onSplashFinished: (Boolean) -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1500)
        onSplashFinished(preferenceManager.isLoggedIn())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "SwaadPe Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SwaadPe",
                style = TextStyle(
                    color = Color(0xFF4CAF50),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Shohratgarh Ka Apna Swaad.",
                style = TextStyle(
                    color = Color(0xFF757575),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

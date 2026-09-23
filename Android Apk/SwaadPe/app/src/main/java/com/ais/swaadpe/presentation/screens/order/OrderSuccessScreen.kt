package com.ais.swaadpe.presentation.screens.order

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OrderSuccessScreen(
    orderNumber: String,
    onTrackOrderClick: (String) -> Unit,
    onContinueShoppingClick: () -> Unit
) {
    val primaryColor = Color(0xFF4CAF50)
    val backgroundColor = Color(0xFFF7F9F7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // --- Custom SVG-like Animation ---
            Box(
                modifier = Modifier.size(250.dp),
                contentAlignment = Alignment.Center
            ) {
                OrderSuccessAnimation()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Order Placed!",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your order #$orderNumber has been received.",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { onTrackOrderClick(orderNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(
                    text = "Track Order",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onContinueShoppingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    text = "Back to Home",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                )
            }
        }
    }
}

@Composable
fun OrderSuccessAnimation() {
    val transition = rememberInfiniteTransition(label = "particles")
    
    // Circle drawing animation
    val circleProgress = remember { Animatable(0f) }
    // Checkmark drawing animation
    val checkProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        circleProgress.animateTo(1f, animationSpec = tween(700, easing = LinearOutSlowInEasing))
        delay(200)
        checkProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }

    // Particle animations
    val particleAlpha by transition.animateFloat(
        initialValue = 0f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500), repeatMode = RepeatMode.Reverse), label = ""
    )

    val glossyGreen = Brush.linearGradient(
        colors = listOf(Color(0xFF86EFAC), Color(0xFF22C55E), Color(0xFF14532D))
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width * 0.45f // Slightly larger radius

        // 1. Draw Circle Path - Increased thickness from 8dp to 12dp
        drawArc(
            color = Color(0xFF22C55E),
            startAngle = -90f,
            sweepAngle = 360f * circleProgress.value,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Draw Sparkling Particles
        if (circleProgress.value > 0.5f) {
            val offsets = listOf(
                Offset(-0.7f, -0.6f), Offset(0.6f, -0.8f), Offset(0.8f, 0.3f), Offset(-0.6f, 0.7f),
                Offset(-0.9f, 0.2f), Offset(0.5f, 0.8f), Offset(0.2f, -0.95f), Offset(-0.3f, -1.0f)
            )
            offsets.forEach { multiplier ->
                drawCircle(
                    color = Color(0xFFA7F3D0).copy(alpha = particleAlpha),
                    radius = 4.dp.toPx(),
                    center = Offset(
                        center.x + (multiplier.x * radius * 1.2f),
                        center.y + (multiplier.y * radius * 1.2f) - (particleAlpha * 20f)
                    )
                )
            }
        }

        // 3. Draw Checkmark Path
        if (checkProgress.value > 0f) {
            val path = Path().apply {
                moveTo(size.width * 0.33f, size.height * 0.52f) // Adjusted for more central look
                lineTo(size.width * 0.45f, size.height * 0.64f)
                lineTo(size.width * 0.68f, size.height * 0.38f)
            }
            
            val pathMeasure = android.graphics.PathMeasure(path.asAndroidPath(), false)
            val length = pathMeasure.length
            val partialPath = android.graphics.Path()
            pathMeasure.getSegment(0f, length * checkProgress.value, partialPath, true)

            // Increased thickness from 12dp to 18dp for a bolder look
            drawPath(
                path = partialPath.asComposePath(),
                brush = glossyGreen,
                style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

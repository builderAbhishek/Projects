package com.ais.swaadpe.presentation.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.R
import com.ais.swaadpe.presentation.viewmodels.AuthViewModel
import com.ais.swaadpe.presentation.viewmodels.LoginState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (Boolean) -> Unit,
    onGuestClick: () -> Unit
) {
    var mobileNumber by remember { mutableStateOf("") }
    val isButtonEnabled = mobileNumber.length == 10
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                onLoginSuccess((loginState as LoginState.Success).isNewUser)
                viewModel.resetState()
            }
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Pattern / Image
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.03f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo & Brand
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "SwaadPe Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to SwaadPe",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
            )

            Text(
                text = "Discover the best food in Shohratgarh",
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Login or Signup",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { input ->
                            if (input.length <= 10 && input.all { it.isDigit() }) {
                                mobileNumber = input
                            }
                        },
                        placeholder = { Text("Enter mobile number", color = Color.Gray) },
                        prefix = { Text("+91 ", style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.login(mobileNumber) },
                        enabled = isButtonEnabled && loginState !is LoginState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4F5F), // Zomato-like Red/Pink
                            contentColor = Color.White
                        )
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "Continue",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                Text(
                    text = " OR ",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = onGuestClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Continue as Guest",
                    style = TextStyle(color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "By continuing, you agree to our\nTerms of Service & Privacy Policy",
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

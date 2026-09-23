package com.ais.swaadpe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import com.ais.swaadpe.presentation.components.ModernBottomNav
import com.ais.swaadpe.presentation.components.NoInternetScreen
import com.ais.swaadpe.presentation.navigation.NavGraph
import com.ais.swaadpe.presentation.theme.SwaadPeTheme
import com.ais.swaadpe.utils.ConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            SwaadPeTheme {
                val status by connectivityObserver.observe().collectAsState(
                    initial = ConnectivityObserver.Status.Available
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        // NavGraph hamesha composition mein rahega taaki state save rahe
                        NavGraph(navController = navController, preferenceManager = preferenceManager)
                        
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            ModernBottomNav(navController = navController)
                        }

                        // No Internet Overlay - Ye screen ke upar dikhega bina state reset kiye
                        if (status != ConnectivityObserver.Status.Available) {
                            NoInternetScreen(onRetry = {
                                // Status auto-update hoga
                            })
                        }
                    }
                }
            }
        }
    }
}

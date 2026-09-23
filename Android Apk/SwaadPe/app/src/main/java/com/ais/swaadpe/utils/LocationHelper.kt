package com.ais.swaadpe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationHelper @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()
        
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                val address = getAddressFromLocation(location.latitude, location.longitude)
                continuation.resume(LocationResult(location.latitude, location.longitude, address))
            } else {
                continuation.resume(null)
            }
        }.addOnFailureListener {
            continuation.resume(null)
        }

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }

    private fun getAddressFromLocation(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea ?: ""
                val state = address.adminArea ?: ""
                if (city.isNotEmpty()) "$city, $state" else state
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            "Unknown Location"
        }
    }
}

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

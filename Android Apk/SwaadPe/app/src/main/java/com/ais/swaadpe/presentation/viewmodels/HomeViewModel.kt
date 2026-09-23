package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import com.ais.swaadpe.domain.model.*
import com.ais.swaadpe.domain.repository.HomeRepository
import com.ais.swaadpe.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val preferenceManager: PreferenceManager,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    private var searchJob: Job? = null

    // Default coordinates (Shohratgarh) - Now used as fallback
    private var currentLat: Double = 27.394879
    private var currentLng: Double = 82.959087
    private var currentAddress: String = "Shohratgarh, UP"

    init {
        // Initial fetch with defaults or saved location
        fetchHomeData()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            locationHelper.getCurrentLocation()?.let { result ->
                currentLat = result.latitude
                currentLng = result.longitude
                currentAddress = result.address
                fetchHomeData()
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double, address: String) {
        currentLat = lat
        currentLng = lng
        currentAddress = address
        fetchHomeData()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) {
                _isSearchLoading.value = true
                delay(600)
            } else {
                _isSearchLoading.value = false
            }
            fetchHomeData(query)
        }
    }

    fun fetchHomeData(query: String? = null) {
        viewModelScope.launch {
            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }

            val userId = preferenceManager.getUserId()

            repository.getHomeData(
                userId = userId,
                lat = currentLat,
                lng = currentLng,
                address = currentAddress,
                query = query?.ifEmpty { null }
            ).onSuccess { data ->
                _uiState.value = HomeUiState.Success(
                    locationText = data.locationText,
                    activeOrder = data.activeOrder,
                    banners = data.banners,
                    categories = data.categories,
                    topPicks = data.topPicks,
                    restaurants = data.restaurants,
                    matchedItems = data.matchedItems
                )
                _isSearchLoading.value = false
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(error.message ?: "Something went wrong")
                _isSearchLoading.value = false
            }
        }
    }
}

private fun String?.ifEmpty(block: () -> String?): String? = if (this.isNullOrEmpty()) block() else this

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val locationText: String,
        val activeOrder: ActiveOrder?,
        val banners: List<Banner>,
        val categories: List<Category>,
        val topPicks: List<TopPick>,
        val restaurants: List<Restaurant>,
        val matchedItems: List<MatchedItem> = emptyList()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

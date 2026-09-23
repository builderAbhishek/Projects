package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.domain.model.MenuCategory
import com.ais.swaadpe.domain.model.VendorDetails
import com.ais.swaadpe.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    fun fetchMenu(vendorId: Int) {
        viewModelScope.launch {
            _uiState.value = MenuUiState.Loading
            repository.getVendorMenu(vendorId).onSuccess { data ->
                _uiState.value = MenuUiState.Success(data.vendor, data.menu)
            }.onFailure { error ->
                _uiState.value = MenuUiState.Error(error.message ?: "Failed to load menu")
            }
        }
    }
}

sealed class MenuUiState {
    object Loading : MenuUiState()
    data class Success(val vendor: VendorDetails, val menu: List<MenuCategory>) : MenuUiState()
    data class Error(val message: String) : MenuUiState()
}

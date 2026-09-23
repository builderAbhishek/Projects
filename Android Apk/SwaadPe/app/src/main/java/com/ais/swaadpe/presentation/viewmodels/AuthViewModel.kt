package com.ais.swaadpe.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ais.swaadpe.data.local.prefs.PreferenceManager
import com.ais.swaadpe.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(mobile: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.login(mobile, null) // FCM Token null for now
                if (response.isSuccessful && response.body()?.status == true) {
                    val userData = response.body()?.userData
                    if (userData != null) {
                        preferenceManager.saveUserSession(
                            userId = userData.userId,
                            name = userData.name,
                            mobile = userData.mobile,
                            email = userData.email,
                            walletBalance = userData.walletBalance
                        )
                        _loginState.value = LoginState.Success(response.body()?.isNewUser ?: false)
                    } else {
                        _loginState.value = LoginState.Error("User data is missing")
                    }
                } else {
                    _loginState.value = LoginState.Error(response.body()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val isNewUser: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}

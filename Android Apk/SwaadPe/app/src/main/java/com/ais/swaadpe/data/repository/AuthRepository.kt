package com.ais.swaadpe.data.repository

import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.data.remote.dto.LoginRequest
import com.ais.swaadpe.data.remote.dto.LoginResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: SwaadPeApi
) {
    suspend fun login(mobile: String, fcmToken: String?): Response<LoginResponse> {
        return api.login(LoginRequest(mobile, fcmToken))
    }
}

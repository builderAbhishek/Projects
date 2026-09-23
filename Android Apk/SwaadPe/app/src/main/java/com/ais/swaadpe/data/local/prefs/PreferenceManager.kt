package com.ais.swaadpe.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("swaadpe_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_MOBILE = "user_mobile"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_WALLET_BALANCE = "wallet_balance"
    }

    fun saveUserSession(userId: Int, name: String?, mobile: String, email: String?, walletBalance: Double) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_MOBILE, mobile)
            putString(KEY_USER_EMAIL, email)
            putFloat(KEY_WALLET_BALANCE, walletBalance.toFloat())
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUserMobile(): String? = prefs.getString(KEY_USER_MOBILE, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

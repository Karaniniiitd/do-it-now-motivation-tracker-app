package com.karan.do_it_now_motivation_tracker.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPrefsManager private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString("user_name", "WARRIOR") ?: "WARRIOR"
        set(value) { prefs.edit().putString("user_name", value).apply() }

    var userClass: String
        get() = prefs.getString("user_class", "Warrior") ?: "Warrior"
        set(value) { prefs.edit().putString("user_class", value).apply() }

    var hasOnboarded: Boolean
        get() = prefs.getBoolean("has_onboarded", false)
        set(value) { prefs.edit().putBoolean("has_onboarded", value).apply() }

    enum class ThemeMode { SYSTEM, LIGHT, DARK }

    var themeMode: ThemeMode
        get() = _themeModeFlow.value
        set(value) {
            prefs.edit().putString("theme_mode", value.name).apply()
            _themeModeFlow.value = value
        }

    private val _themeModeFlow = MutableStateFlow(
        try { ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name) } 
        catch (e: Exception) { ThemeMode.DARK }
    )
    val themeModeFlow = _themeModeFlow.asStateFlow()

    companion object {
        @Volatile
        private var instance: UserPrefsManager? = null

        fun getInstance(context: Context): UserPrefsManager =
            instance ?: synchronized(this) {
                instance ?: UserPrefsManager(context.applicationContext).also { instance = it }
            }
    }
}

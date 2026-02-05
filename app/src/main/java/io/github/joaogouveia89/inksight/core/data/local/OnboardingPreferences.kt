package io.github.joaogouveia89.inksight.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "onboarding_prefs")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val SHOW_ONBOARDING_KEY = booleanPreferencesKey("show_onboarding")
    }

    val showOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_ONBOARDING_KEY] ?: true
        }

    suspend fun setShowOnboarding(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_ONBOARDING_KEY] = show
        }
    }
}

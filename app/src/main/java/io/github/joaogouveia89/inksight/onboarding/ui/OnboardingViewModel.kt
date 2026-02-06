package io.github.joaogouveia89.inksight.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.joaogouveia89.inksight.onboarding.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: OnboardingRepository
) : ViewModel() {

    /**
     * UI state representing whether to show the onboarding.
     * Initially null while loading from DataStore.
     */
    val showOnboarding: StateFlow<Boolean?> = repository.showOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Completes the onboarding process by saving the user's preference.
     * @param showAgain whether the user wants to see the onboarding again.
     */
    fun completeOnboarding(showAgain: Boolean) {
        viewModelScope.launch {
            repository.setShowOnboarding(showAgain)
        }
    }
}

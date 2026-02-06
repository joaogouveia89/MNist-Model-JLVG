package io.github.joaogouveia89.inksight.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.joaogouveia89.inksight.digit_recognition.domain.repository.DigitRecognitionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val digitRecognitionRepository: DigitRecognitionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            digitRecognitionRepository.getAllInferences()
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { _ ->
                    _uiState.update { it.copy(isLoading = false, items = emptyList()) }
                }
                .collect { items ->
                    val accuracy = if (items.isNotEmpty()) {
                        items.count { it.isCorrect }.toFloat() / items.size
                    } else {
                        0f
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            accuracyRate = accuracy
                        )
                    }
                }
        }
    }
}

package io.github.joaogouveia89.inksight.history.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.joaogouveia89.inksight.R
import io.github.joaogouveia89.inksight.core.ui.theme.SuccessGreen
import io.github.joaogouveia89.inksight.core.ui.theme.ErrorRed
import io.github.joaogouveia89.inksight.core.ui.theme.MNistModelAppTheme
import io.github.joaogouveia89.inksight.core.ui.theme.spacing
import io.github.joaogouveia89.inksight.history.domain.model.HistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryUiState = HistoryUiState(),
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                LoadingState()
            } else if (state.items.isEmpty()) {
                EmptyState()
            } else {
                AccuracyHeader(accuracyRate = state.accuracyRate)
                HistoryGrid(items = state.items)
            }
        }
    }
}

@Composable
fun AccuracyHeader(accuracyRate: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.large)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.history_accuracy_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${(accuracyRate * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun HistoryGrid(items: List<HistoryItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall), // 12.dp
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall), // 12.dp
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            HistoryCard(item = item)
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                item.image?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.history_number_label, item.predictedNumber),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.predictedNumber.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }

                // Feedback Icon (Correct/Incorrect)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(MaterialTheme.spacing.small),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (item.isCorrect) SuccessGreen else ErrorRed,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (item.isCorrect) stringResource(R.string.common_correct) else stringResource(R.string.common_incorrect),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(MaterialTheme.spacing.medium)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.history_number_label, item.predictedNumber),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = stringResource(R.string.common_confidence, item.confidence),
                    style = MaterialTheme.typography.bodySmall,
                    color = getConfidenceColor(item.confidence)
                )
            }
        }
    }
}

// Helper to replace the missing color property from CharacterPrediction
@Composable
private fun getConfidenceColor(confidence: Int): Color {
    return when {
        confidence >= 70 -> SuccessGreen
        confidence >= 40 -> Color(0xFFFFA500) // Orange
        else -> ErrorRed
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(tripleExtraLargeSize()),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        Text(
            text = stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = stringResource(R.string.history_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun tripleExtraLargeSize() = 64.dp // Keep it fixed or move to Dimens if reused

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    MNistModelAppTheme {
        HistoryScreen(
            state = HistoryUiState(
                items = listOf(
                    HistoryItem(
                        predictedNumber = 5,
                        confidence = 98,
                        image = null,
                        isCorrect = true,
                        timestamp = System.currentTimeMillis()
                    ),
                    HistoryItem(
                        predictedNumber = 3,
                        confidence = 45,
                        image = null,
                        isCorrect = false,
                        timestamp = System.currentTimeMillis()
                    ),
                    HistoryItem(
                        predictedNumber = 8,
                        confidence = 75,
                        image = null,
                        isCorrect = true,
                        timestamp = System.currentTimeMillis()
                    ),
                    HistoryItem(
                        predictedNumber = 1,
                        confidence = 92,
                        image = null,
                        isCorrect = true,
                        timestamp = System.currentTimeMillis()
                    )
                ),
                accuracyRate = 0.75f
            ),
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenEmptyPreview() {
    MNistModelAppTheme {
        HistoryScreen(
            state = HistoryUiState(items = emptyList()),
            onBack = {}
        )
    }
}

package io.github.joaogouveia89.inksight.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.joaogouveia89.inksight.R
import io.github.joaogouveia89.inksight.core.ui.theme.spacing
import io.github.joaogouveia89.inksight.onboarding.ui.components.OnboardingCameraPage
import io.github.joaogouveia89.inksight.onboarding.ui.components.OnboardingHistoryPage
import io.github.joaogouveia89.inksight.onboarding.ui.components.OnboardingReadyPage
import io.github.joaogouveia89.inksight.onboarding.ui.components.OnboardingWelcomePage
import io.github.joaogouveia89.inksight.onboarding.ui.model.OnboardingPage
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: (showAgain: Boolean) -> Unit
) {
    val pages = OnboardingPage.entries
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    var showAgain by remember { mutableStateOf(true) }

    val isLastPage = pagerState.currentPage == pages.size - 1

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onFinish(showAgain) }) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                when (pages[pageIndex]) {
                    OnboardingPage.WELCOME -> OnboardingWelcomePage()
                    OnboardingPage.CAMERA -> OnboardingCameraPage()
                    OnboardingPage.HISTORY -> OnboardingHistoryPage()
                    OnboardingPage.READY -> OnboardingReadyPage()
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLastPage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                    ) {
                        Checkbox(
                            checked = !showAgain,
                            onCheckedChange = { showAgain = !it }
                        )
                        Text(
                            text = stringResource(R.string.onboarding_dont_show_again),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Button(
                    onClick = {
                        if (!isLastPage) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish(showAgain)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isLastPage)
                            stringResource(R.string.onboarding_get_started)
                        else
                            stringResource(R.string.onboarding_next)
                    )
                    if (!isLastPage) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

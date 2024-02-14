package ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton
import ca.josephroque.bowlingcompanion.feature.overview.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPlayOnboardingTopBar(onAction: (QuickPlayOnboardingUiAction) -> Unit) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.quick_play_how_to_use),
				style = MaterialTheme.typography.titleMedium,
			)
		},
		navigationIcon = {
			CloseButton(onClick = { onAction(QuickPlayOnboardingUiAction.BackClicked) })
		},
	)
}

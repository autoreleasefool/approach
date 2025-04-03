package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.ReadableContent
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.AppNameChangeUiAction

@Composable
fun ApproachAppDescription(
	isVisible: Boolean,
	onAction: (AppNameChangeUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember { MutableTransitionState(false) }

	LaunchedEffect(isVisible) {
		visibleState.targetState = isVisible
	}

	Description(
		visibleState = visibleState,
		onAction = onAction,
		modifier = modifier,
	)
}

@Composable
private fun Description(
	visibleState: MutableTransitionState<Boolean>,
	onAction: (AppNameChangeUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	AnimatedVisibility(
		visibleState = visibleState,
		enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
		modifier = modifier,
	) {
		ReadableContent {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.fillMaxWidth()
					.verticalScroll(rememberScrollState())
					.padding(horizontal = 16.dp),
			) {
				Text(
					text = stringResource(R.string.onboarding_legacy_user_title_is_taking_a_new),
					style = MaterialTheme.typography.titleMedium,
					fontStyle = FontStyle.Italic,
				)

				Text(
					text = stringResource(R.string.onboarding_legacy_user_title_approach),
					style = MaterialTheme.typography.headlineMedium,
					modifier = Modifier.padding(bottom = 16.dp),
				)

				Text(
					text = stringResource(R.string.onboarding_legacy_user_description_updated),
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(bottom = 8.dp),
				)

				Text(
					text = stringResource(R.string.onboarding_legacy_user_description_wish),
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(bottom = 16.dp),
				)

				Text(
					text = stringResource(R.string.onboarding_legacy_user_description_vancouver),
					style = MaterialTheme.typography.bodySmall,
					fontWeight = FontWeight.Bold,
					fontStyle = FontStyle.Italic,
					modifier = Modifier.padding(bottom = 16.dp),
				)

				Button(
					onClick = { onAction(AppNameChangeUiAction.GetStartedClicked) },
				) {
					Text(
						text = stringResource(R.string.onboarding_legacy_user_get_started),
						style = MaterialTheme.typography.bodyLarge,
					)
				}
			}
		}
	}
}

package ca.josephroque.bowlingcompanion.feature.analytics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.designsystem.components.Link
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.feature.analytics.ui.R

@Composable
fun AnalyticsSettings(
	modifier: Modifier = Modifier,
	state: AnalyticsSettingsUiState.Success,
	onToggleOptInStatus: (Boolean?) -> Unit,
) {
	Column(
			modifier = modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
	) {
		Text(
			text = stringResource(R.string.analytics_info_first),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 8.dp),
		)

		Text(
			text = stringResource(R.string.analytics_info_second),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 8.dp),
		)

		LabeledSwitch(
			checked = state.analyticsOptInStatus == AnalyticsOptInStatus.OPTED_IN,
			onCheckedChange = onToggleOptInStatus,
			titleResourceId = R.string.analytics_share_anonymous_analytics
		)

		Text(
			text = stringResource(R.string.analytics_opt_out),
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		val uriHandler = LocalUriHandler.current
		val context = LocalContext.current
		Link(
			titleResourceId = R.string.analytics_privacy_policy,
			iconResourceId = RCoreDesign.drawable.ic_open_in_new,
			onClick = { uriHandler.openUri(context.resources.getString(R.string.analytics_privacy_policy_url)) },
			modifier = Modifier.padding(top = 16.dp),
		)
	}
}

sealed interface AnalyticsSettingsUiState {
	data object Loading: AnalyticsSettingsUiState
	data class Success(
		val analyticsOptInStatus: AnalyticsOptInStatus,
	): AnalyticsSettingsUiState
}
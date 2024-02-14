package ca.josephroque.bowlingcompanion.feature.settings.ui.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
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
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.feature.settings.ui.R

@Composable
fun AnalyticsSettings(
	state: AnalyticsSettingsUiState,
	onAction: (AnalyticsSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		Text(
			text = stringResource(R.string.analytics_settings_info_first),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 8.dp),
		)

		Text(
			text = stringResource(R.string.analytics_settings_info_second),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)

		Divider()

		LabeledSwitch(
			checked = state.analyticsOptInStatus == AnalyticsOptInStatus.OPTED_IN,
			onCheckedChange = { onAction(AnalyticsSettingsUiAction.OptInStatusToggled(it)) },
			titleResourceId = R.string.analytics_settings_share_anonymous_analytics,
		)

		Divider()

		Text(
			text = stringResource(R.string.analytics_settings_opt_out),
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(top = 16.dp),
		)

		val uriHandler = LocalUriHandler.current
		val context = LocalContext.current
		Link(
			titleResourceId = R.string.analytics_settings_privacy_policy,
			iconResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_open_in_new,
			onClick = {
				uriHandler.openUri(context.resources.getString(R.string.analytics_settings_privacy_policy_url))
			},
			modifier = Modifier.padding(top = 16.dp),
		)
	}
}

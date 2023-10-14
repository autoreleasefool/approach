package ca.josephroque.bowlingcompanion.feature.analytics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.components.Link

@Composable
internal fun AnalyticsSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AnalyticsSettingsViewModel = hiltViewModel(),
) {
	val analyticsSettingsState by viewModel.uiState.collectAsStateWithLifecycle()

	AnalyticsSettingsScreen(
		analyticsSettingsState = analyticsSettingsState,
		onBackPressed = onBackPressed,
		onToggleOptInStatus = viewModel::toggleAnalyticsOptInStatus,
		modifier = modifier,
	)
}

@Composable
fun AnalyticsSettingsScreen(
	analyticsSettingsState: AnalyticsSettingsUiState,
	onBackPressed: () -> Unit,
	onToggleOptInStatus: (Boolean?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AnalyticsSettingsTopBar(onBackPressed = onBackPressed)
		}
	) { padding ->
		Column(
			modifier = modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(padding)
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

			when (analyticsSettingsState) {
				AnalyticsSettingsUiState.Loading -> Unit
				is AnalyticsSettingsUiState.Success -> {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween,
						modifier = Modifier
							.fillMaxWidth()
							.clickable(onClick = { onToggleOptInStatus(null) })
							.padding(16.dp)
					) {
						Text(
							text = stringResource(R.string.analytics_share_anonymous_analytics),
							style = MaterialTheme.typography.bodyMedium,
							modifier = Modifier.weight(1f),
						)

						Switch(
							checked = analyticsSettingsState.analyticsOptInStatus == AnalyticsOptInStatus.OPTED_IN,
							onCheckedChange = { onToggleOptInStatus(it) },
						)
					}
				}
			}

			Text(
				text = stringResource(R.string.analytics_opt_out),
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.padding(horizontal = 16.dp),
			)

			val uriHandler = LocalUriHandler.current
			val context = LocalContext.current
			Link(
				titleResourceId = R.string.analytics_privacy_policy,
				iconResourceId = R.drawable.ic_open_in_new,
				onClick = { uriHandler.openUri(context.resources.getString(R.string.analytics_privacy_policy_url)) },
				modifier = Modifier.padding(top = 16.dp),
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsSettingsTopBar(
	onBackPressed: () -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.settings_item_analytics),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			IconButton(onClick = onBackPressed) {
				Icon(
					imageVector = Icons.Default.ArrowBack,
					contentDescription = stringResource(R.string.cd_back),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}

@Preview
@Composable
fun AnalyticsSettingsScreenPreview() {
	Surface {
		AnalyticsSettingsScreen(
			analyticsSettingsState = AnalyticsSettingsUiState.Success(analyticsOptInStatus = AnalyticsOptInStatus.OPTED_IN),
			onBackPressed = {},
			onToggleOptInStatus = {},
		)
	}
}
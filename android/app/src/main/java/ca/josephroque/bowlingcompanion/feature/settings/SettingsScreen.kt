package ca.josephroque.bowlingcompanion.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.settings.ui.NavigationItem
import ca.josephroque.bowlingcompanion.feature.settings.ui.SectionFooter
import ca.josephroque.bowlingcompanion.feature.settings.ui.SectionHeader
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsLink

@Composable
internal fun SettingsRoute(
	openOpponents: () -> Unit,
	openStatisticsSettings: () -> Unit,
	openAcknowledgements: () -> Unit,
	openAnalyticsSettings: () -> Unit,
	openDataImportSettings: () -> Unit,
	openDataExportSettings: () -> Unit,
	openDeveloperSettings: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = hiltViewModel(),
) {
	val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

	SettingsScreen(
		settingsState = settingsState,
		openOpponents = openOpponents,
		openStatisticsSettings = openStatisticsSettings,
		openAcknowledgements = openAcknowledgements,
		openAnalyticsSettings = openAnalyticsSettings,
		openDataImportSettings = openDataImportSettings,
		openDataExportSettings = openDataExportSettings,
		openDeveloperSettings = openDeveloperSettings,
		modifier = modifier,
	)
}

@Composable
internal fun SettingsScreen(
	settingsState: SettingsUiState,
	openOpponents: () -> Unit,
	openStatisticsSettings: () -> Unit,
	openAcknowledgements: () -> Unit,
	openAnalyticsSettings: () -> Unit,
	openDataImportSettings: () -> Unit,
	openDataExportSettings: () -> Unit,
	openDeveloperSettings: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			SettingsTopBar()
		}
	) { padding ->
		Column(
			modifier = modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(padding),
		) {
			NavigationItem(
				titleResourceId = R.string.settings_item_opponents_title,
				descriptionResourceId = R.string.settings_item_opponents_description,
				onClick = openOpponents,
			)

			NavigationItem(
				titleResourceId = R.string.settings_item_statistics_title,
				descriptionResourceId = R.string.settings_item_statistics_description,
				onClick = openStatisticsSettings,
			)

			Divider()

			SectionHeader(titleResourceId = R.string.settings_item_help)

			val context = LocalContext.current
			SettingsLink(
				titleResourceId = R.string.settings_item_report_bug,
				iconResourceId = R.drawable.ic_send,
				onClick = {
					val recipient = context.resources.getString(R.string.bug_report_email_recipient)
					val subject = context.resources.getString(
						R.string.bug_report_email_subject,
						BuildConfig.VERSION_NAME,
						BuildConfig.VERSION_CODE.toString(),
					)
					val emailIntent = Intent(Intent.ACTION_SEND).apply {
						setDataAndType(Uri.parse("mailto:"), "message/rfc822")
						putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
						putExtra(Intent.EXTRA_SUBJECT, subject)
					}

					context.startActivity(Intent.createChooser(emailIntent, context.resources.getString(R.string.action_send_email)))
				},
			)

			SettingsLink(
				titleResourceId = R.string.settings_item_send_feedback,
				iconResourceId = R.drawable.ic_send,
				onClick = {
					val recipient = context.resources.getString(R.string.feedback_email_recipient)
					val emailIntent = Intent(Intent.ACTION_SEND).apply {
						setDataAndType(Uri.parse("mailto:"), "message/rfc822")
						putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
					}

					context.startActivity(Intent.createChooser(emailIntent, context.resources.getString(R.string.action_send_email)))
				},
			)

			NavigationItem(
				titleResourceId = R.string.settings_item_acknowledgements,
				onClick = openAcknowledgements,
			)

			NavigationItem(
				titleResourceId = R.string.settings_item_analytics,
				onClick = openAnalyticsSettings,
			)

			Divider()

			if (settingsState.isDataSectionVisible) {
				SectionHeader(titleResourceId = R.string.settings_item_data)

				if (settingsState.isDataImportsEnabled) {
					NavigationItem(
						titleResourceId = R.string.settings_item_import_data,
						onClick = openDataImportSettings
					)
				}

				if (settingsState.isDataExportsEnabled) {
					NavigationItem(
						titleResourceId = R.string.settings_item_export_data,
						onClick = openDataExportSettings
					)
				}

				Divider()
			}

			SectionHeader(titleResourceId = R.string.settings_item_development)

			NavigationItem(
				titleResourceId = R.string.settings_item_developer,
				onClick = openDeveloperSettings
			)

			val uriHandler = LocalUriHandler.current
			SettingsLink(
				titleResourceId = R.string.settings_item_view_source,
				iconResourceId = R.drawable.ic_open_in_new,
				onClick = {
					val viewSourceUri = context.resources.getString(R.string.app_source_repository_url)
					uriHandler.openUri(viewSourceUri)
				},
			)

			SectionFooter(titleResourceId = R.string.settings_item_development_footer)

			Divider()

			SectionHeader(titleResourceId = R.string.settings_item_app_info)

			val clipboardManager = LocalClipboardManager.current
			val versionString = stringResource(
				R.string.settings_item_version_detail,
				BuildConfig.VERSION_NAME,
				BuildConfig.VERSION_CODE.toString(),
			)
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = { clipboardManager.setText(AnnotatedString(versionString)) })
					.padding(16.dp),
			) {
				Text(
					text = stringResource(R.string.settings_item_version),
					style = MaterialTheme.typography.titleMedium,
				)
				Text(
					text = versionString,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}

			SectionFooter(titleResourceId = R.string.settings_item_copyright)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar() {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.settings_title),
				style = MaterialTheme.typography.titleLarge,
			)
		}
	)
}

@Preview
@Composable
private fun SettingsScreenPreview() {
	Surface {
		SettingsScreen(
			settingsState = SettingsUiState(isDataExportsEnabled = true, isDataImportsEnabled = true),
			openOpponents = {},
			openStatisticsSettings = {},
			openAcknowledgements = {},
			openAnalyticsSettings = {},
			openDataExportSettings = {},
			openDataImportSettings = {},
			openDeveloperSettings = {},
		)
	}
}
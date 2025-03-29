package ca.josephroque.bowlingcompanion.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.sendEmail
import ca.josephroque.bowlingcompanion.feature.settings.ui.components.Footer
import ca.josephroque.bowlingcompanion.feature.settings.ui.components.Header
import ca.josephroque.bowlingcompanion.feature.settings.ui.components.Link
import ca.josephroque.bowlingcompanion.feature.settings.ui.components.NavigationItem

@Composable
fun Settings(state: SettingsUiState, onAction: (SettingsUiAction) -> Unit, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		if (state.isDevelopmentModeEnabled) {
			DevelopmentModeSection(onAction = onAction)

			HorizontalDivider()
		}

		MainSection(isAchievementsEnabled = state.isAchievementsEnabled, onAction = onAction)

		HorizontalDivider()

		ArchivesSection(onAction = onAction)

		HorizontalDivider()

		HelpSection(
			versionName = state.versionName,
			versionCode = state.versionCode,
			onAction = onAction,
		)

		HorizontalDivider()

		if (state.isDataSectionVisible) {
			DataSection(
				isDataImportsEnabled = state.isDataImportsEnabled,
				isDataExportsEnabled = state.isDataExportsEnabled,
				onAction = onAction,
			)

			HorizontalDivider()
		}

		DevelopmentSection(onAction = onAction)

		HorizontalDivider()

		AppInfoSection(
			versionName = state.versionName,
			versionCode = state.versionCode,
		)
	}
}

@Composable
private fun DevelopmentModeSection(onAction: (SettingsUiAction) -> Unit) {
	Header(
		titleResourceId = R.string.settings_item_development_mode,
		color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
	)

	NavigationItem(
		titleResourceId = R.string.settings_item_feature_flags,
		descriptionResourceId = R.string.settings_item_feature_flags_description,
		onClick = { onAction(SettingsUiAction.FeatureFlagsClicked) },
	)

	Text(
		text = stringResource(R.string.settings_item_force_crash),
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.error,
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = { onAction(SettingsUiAction.ForceCrashClicked) })
			.padding(16.dp),
	)
}

@Composable
private fun MainSection(isAchievementsEnabled: Boolean, onAction: (SettingsUiAction) -> Unit) {
	NavigationItem(
		titleResourceId = R.string.settings_item_opponents_title,
		descriptionResourceId = R.string.settings_item_opponents_description,
		onClick = { onAction(SettingsUiAction.OpponentsClicked) },
	)

	NavigationItem(
		titleResourceId = R.string.settings_item_statistics_title,
		descriptionResourceId = R.string.settings_item_statistics_description,
		onClick = { onAction(SettingsUiAction.StatisticsSettingsClicked) },
	)

	if (isAchievementsEnabled) {
		NavigationItem(
			titleResourceId =R.string.settings_item_achievements_title,
			descriptionResourceId = R.string.settings_item_achievements_description,
			onClick = { onAction(SettingsUiAction.AchievementsClicked) },
		)
	}
}

@Composable
private fun ArchivesSection(onAction: (SettingsUiAction) -> Unit) {
	NavigationItem(
		titleResourceId = R.string.settings_item_archives_title,
		descriptionResourceId = R.string.settings_item_archives_description,
		onClick = { onAction(SettingsUiAction.ArchivesClicked) },
	)
}

@Composable
private fun HelpSection(versionName: String, versionCode: String, onAction: (SettingsUiAction) -> Unit) {
	Header(titleResourceId = R.string.settings_item_help)

	val context = LocalContext.current
	Link(
		titleResourceId = R.string.settings_item_report_bug,
		iconResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_send,
		onClick = {
			onAction(SettingsUiAction.ReportBugClicked)
			val recipient = context.resources.getString(R.string.bug_report_email_recipient)
			val subject = context.resources.getString(
				R.string.bug_report_email_subject,
				versionName,
				versionCode,
			)
			val intentTitle = context.resources.getString(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email,
			)
			sendEmail(
				recipient = recipient,
				subject = subject,
				intentTitle = intentTitle,
				context = context,
			)
		},
	)

	Link(
		titleResourceId = R.string.settings_item_send_feedback,
		iconResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_send,
		onClick = {
			onAction(SettingsUiAction.SendFeedbackClicked)
			val recipient = context.resources.getString(R.string.feedback_email_recipient)
			val intentTitle = context.resources.getString(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email,
			)
			sendEmail(recipient = recipient, intentTitle = intentTitle, context = context)
		},
	)

	NavigationItem(
		titleResourceId = R.string.settings_item_acknowledgements,
		onClick = { onAction(SettingsUiAction.AcknowledgementsClicked) },
	)

	NavigationItem(
		titleResourceId = R.string.settings_item_analytics,
		onClick = { onAction(SettingsUiAction.AnalyticsSettingsClicked) },
	)
}

@Composable
private fun DataSection(
	isDataImportsEnabled: Boolean,
	isDataExportsEnabled: Boolean,
	onAction: (SettingsUiAction) -> Unit,
) {
	Header(titleResourceId = R.string.settings_item_data)

	if (isDataImportsEnabled) {
		NavigationItem(
			titleResourceId = R.string.settings_item_import_data,
			onClick = { onAction(SettingsUiAction.DataImportSettingsClicked) },
		)
	}

	if (isDataExportsEnabled) {
		NavigationItem(
			titleResourceId = R.string.settings_item_export_data,
			onClick = { onAction(SettingsUiAction.DataExportSettingsClicked) },
		)
	}
}

@Composable
private fun DevelopmentSection(onAction: (SettingsUiAction) -> Unit) {
	val context = LocalContext.current

	Header(titleResourceId = R.string.settings_item_development)

	NavigationItem(
		titleResourceId = R.string.settings_item_developer,
		onClick = { onAction(SettingsUiAction.DeveloperSettingsClicked) },
	)

	val uriHandler = LocalUriHandler.current
	Link(
		titleResourceId = R.string.settings_item_view_source,
		iconResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_open_in_new,
		onClick = {
			onAction(SettingsUiAction.ViewSourceClicked)
			val viewSourceUri = context.resources.getString(R.string.app_source_repository_url)
			uriHandler.openUri(viewSourceUri)
		},
	)

	Footer(titleResourceId = R.string.settings_item_development_footer)
}

@Composable
private fun AppInfoSection(versionName: String, versionCode: String) {
	Header(titleResourceId = R.string.settings_item_app_info)

	val clipboardManager = LocalClipboardManager.current
	val versionString = stringResource(
		R.string.settings_item_version_detail,
		versionName,
		versionCode,
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

	Footer(titleResourceId = R.string.settings_item_copyright)
}

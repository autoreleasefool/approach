import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

struct HelpSection: View {
	let onTapReportBugButton: () -> Void
	let onTapSendFeedbackButton: () -> Void
	let onAcknowledgementsFirstAppear: () -> Void

	var body: some View {
		Section(Strings.Settings.Help.title) {
			Button(Strings.Settings.Help.reportBug, action: onTapReportBugButton)

			Button(Strings.Settings.Help.sendFeedback, action: onTapSendFeedbackButton)

			SettingsLink(
				title: Strings.Settings.Acknowledgements.title,
				destination: SettingsList.SettingsItem.acknowledgements
			)

			SettingsLink(
				title: Strings.Settings.Analytics.title,
				destination: SettingsList.SettingsItem.analytics
			)
		}
	}
}

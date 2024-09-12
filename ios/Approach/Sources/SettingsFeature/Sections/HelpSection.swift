import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

struct HelpSection: View {
	let isForceCrashButtonVisible: Bool
	let onTapReportBugButton: () -> Void
	let onTapSendFeedbackButton: () -> Void
	let onTapForceCrashButton: () -> Void
	let onTapAnalyticsButton: () -> Void
	let onAcknowledgementsFirstAppear: () -> Void

	var body: some View {
		Section(Strings.Settings.Help.title) {
			Button(Strings.Settings.Help.reportBug, action: onTapReportBugButton)
			Button(Strings.Settings.Help.sendFeedback, action: onTapSendFeedbackButton)
			if isForceCrashButtonVisible {
				Button(Strings.Settings.Help.forceCrash, action: onTapForceCrashButton)
			}
			NavigationLink(
				Strings.Settings.Help.acknowledgements,
				destination: AcknowledgementsView()
					.onFirstAppear(perform: onAcknowledgementsFirstAppear)
			)
			Button(Strings.Settings.Analytics.title, action: onTapAnalyticsButton)
				.buttonStyle(.navigation)
		}
	}
}

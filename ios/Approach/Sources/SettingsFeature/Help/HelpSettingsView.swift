import ComposableArchitecture
import ConstantsLibrary
import FoundationExtensionsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct HelpSettingsView: View {
	let store: StoreOf<HelpSettings>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			Section(Strings.Settings.Help.title) {
				Button(Strings.Settings.Help.reportBug) { viewStore.send(.didTapReportBugButton) }
				Button(Strings.Settings.Help.sendFeedback) { viewStore.send(.didTapSendFeedbackButton) }
				NavigationLink(
					Strings.Settings.Help.acknowledgements,
					destination: AcknowledgementsView()
						.onAppear { viewStore.send(.didShowAcknowledgements) }
				)
			}

			Section {
				NavigationLink(
					Strings.Settings.Help.developer,
					destination: DeveloperDetailsView()
						.onAppear { viewStore.send(.didShowDeveloperDetails) }
				)
				Button(Strings.Settings.Help.viewSource) { viewStore.send(.didTapViewSource) }
				// TODO: enable tip jar
//				NavigationLink("Tip Jar", destination: TipJarView())
			} header: {
				Text(Strings.Settings.Help.Development.title)
			} footer: {
				Text(Strings.Settings.Help.Development.help(AppConstants.appName))
			}
			.sheet(isPresented: viewStore.$isShowingBugReportEmail) {
				EmailView(
					content: .init(
						recipients: [Strings.Settings.Help.ReportBug.email],
						subject: Strings.Settings.Help.ReportBug.subject(
							Strings.Settings.AppInfo.appVersion(Bundle.main.appVersionLong, Bundle.main.appBuild)
						)
					)
				)
			}
			.sheet(isPresented: viewStore.$isShowingSendFeedbackEmail) {
				EmailView(
					content: .init(
						recipients: [Strings.Settings.Help.SendFeedback.email]
					)
				)
			}
		})
	}
}

import ComposableArchitecture
import ConstantsLibrary
import StringsLibrary
import SwiftUI

public struct HelpSettingsView: View {
	let store: StoreOf<HelpSettings>

	public struct ViewState: Equatable {
		init(state: HelpSettings.State) {}
	}

	public enum ViewAction {
		case didTapReportBugButton
		case didTapSendFeedbackButton
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: HelpSettings.Action.init) { viewStore in
			Section(Strings.Settings.Help.title) {
				Button(Strings.Settings.Help.reportBug) { viewStore.send(.didTapReportBugButton) }
				Button(Strings.Settings.Help.sendFeedback) { viewStore.send(.didTapSendFeedbackButton) }
				NavigationLink(Strings.Settings.Help.acknowledgements, destination: AcknowledgementsView())
			}

			Section {
				NavigationLink(Strings.Settings.Help.developer, destination: DeveloperDetailsView())
				Link(Strings.Settings.Help.viewSource, destination: AppConstants.openSourceRepositoryUrl)
				// TODO: enable tip jar
//				NavigationLink("Tip Jar", destination: TipJarView())
			} header: {
				Text(Strings.Settings.Help.Development.title)
			} footer: {
				Text(Strings.Settings.Help.Development.help(AppConstants.appName))
			}
		}
	}
}

extension HelpSettings.Action {
	init(action: HelpSettingsView.ViewAction) {
		switch action {
		case .didTapReportBugButton:
			self = .view(.didTapReportBugButton)
		case .didTapSendFeedbackButton:
			self = .view(.didTapSendFeedbackButton)
		}
	}
}

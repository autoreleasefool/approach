import ComposableArchitecture
import ConstantsLibrary
import SwiftUI

public struct HelpSettingsView: View {
	let store: StoreOf<HelpSettings>

	public struct ViewState: Equatable {
		init(state: HelpSettings.State) {}
	}

	public enum ViewAction {
		case reportBugButtonTapped
		case sendFeedbackButtonTapped
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: HelpSettings.Action.init) { viewStore in
			Section("Help") {
				Button("Report Bug") { viewStore.send(.reportBugButtonTapped) }
				Button("Send Feedback") { viewStore.send(.sendFeedbackButtonTapped) }
				NavigationLink("Acknowledgements", destination: AcknowledgementsView())
			}

			Section {
				NavigationLink("Developer", destination: DeveloperDetailsView())
				Link("View Source", destination: AppConstants.openSourceRepositoryUrl)
//				NavigationLink("Tip Jar", destination: TipJarView())
			} header: {
				Text("Development")
			} footer: {
				Text("\(AppConstants.appName) is an open source project you can aid in the development of by using the links above")
			}
		}
	}
}

extension HelpSettings.Action {
	init(action: HelpSettingsView.ViewAction) {
		switch action {
		case .reportBugButtonTapped:
			self = .reportBugButtonTapped
		case .sendFeedbackButtonTapped:
			self = .sendFeedbackButtonTapped
		}
	}
}

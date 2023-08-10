import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary
import FoundationExtensionsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct AnalyticsSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var analyticsOptInStatus: Analytics.OptInStatus

		public init() {
			@Dependency(\.analytics) var analytics
			self.analyticsOptInStatus = analytics.getOptInStatus()
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setAnalyticsOptInStatus(Analytics.OptInStatus)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case updatedOptInStatus(Analytics.OptInStatus)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.analytics) var analytics

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setAnalyticsOptInStatus(status):
					state.analyticsOptInStatus = status
					return .run { send in
						await send(.internal(.updatedOptInStatus(analytics.setOptInStatus(status))))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .updatedOptInStatus(status):
					state.analyticsOptInStatus = status
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct AnalyticsSettingsView: View {
	let store: StoreOf<AnalyticsSettings>

	init(store: StoreOf<AnalyticsSettings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					Text(Strings.Settings.Analytics.Info.paragraphOne)
					Text(Strings.Settings.Analytics.Info.paragraphTwo)
				}
				.listRowBackground(Color.clear)
				.listRowSeparator(.hidden)

				Section {
					switch viewStore.analyticsOptInStatus {
					case .optedIn:
						Text(Strings.Settings.Analytics.OptedIn.message)
						Button(Strings.Settings.Analytics.optOut) {
							viewStore.send(.setAnalyticsOptInStatus(.optedOut), animation: .easeInOut)
						}
					case .optedOut:
						Text(Strings.Settings.Analytics.OptedOut.message)
						Button(Strings.Settings.Analytics.optIn) {
							viewStore.send(.setAnalyticsOptInStatus(.optedIn), animation: .easeInOut)
						}
					}
				}
			}
			.navigationTitle(Strings.Settings.Analytics.title)
		})
	}
}

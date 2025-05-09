import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct AnalyticsSettings: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var analyticsOptIn: Bool

		public init() {
			@Dependency(\.analytics) var analytics
			switch analytics.getOptInStatus() {
			case .optedIn:
				self.analyticsOptIn = true
			case .optedOut:
				self.analyticsOptIn = false
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case updatedOptInStatus(Analytics.OptInStatus)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.analytics) var analytics

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .updatedOptInStatus(status):
					switch status {
					case .optedIn:
						state.analyticsOptIn = true
					case .optedOut:
						state.analyticsOptIn = false
					}
					return .none
				}

			case .binding(\.analyticsOptIn):
				return .run { [optedIn = state.analyticsOptIn] send in
					let status = optedIn ? Analytics.OptInStatus.optedIn : Analytics.OptInStatus.optedOut
					await send(.internal(.updatedOptInStatus((try? analytics.setOptInStatus(status)) ?? status)))
				}

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

@ViewAction(for: AnalyticsSettings.self)
public struct AnalyticsSettingsView: View {
	@Bindable public var store: StoreOf<AnalyticsSettings>

	init(store: StoreOf<AnalyticsSettings>) {
		self.store = store
	}

	public var body: some View {
		List {
			Section {
				Text(Strings.Settings.Analytics.Info.paragraphOne)
				Text(Strings.Settings.Analytics.Info.paragraphTwo)
			}
			.listRowInsets(EdgeInsets(top: .standardSpacing, leading: 0, bottom: 0, trailing: 0))
			.listRowBackground(Color.clear)
			.listRowSeparator(.hidden)

			Section {
				Toggle(
					Strings.Settings.Analytics.shareAnonymousAnalytics,
					isOn: $store.analyticsOptIn
				)
			} footer: {
				Text(Strings.Settings.Analytics.ShareAnonymousAnalytics.footer)
			}

			Section {
				Link(
					Strings.Settings.Analytics.privacyPolicy,
					destination: AppConstants.privacyPolicyUrl
				)
			}
		}
		.navigationTitle(Strings.Settings.Analytics.title)
		.onAppear { send(.onAppear) }
	}
}

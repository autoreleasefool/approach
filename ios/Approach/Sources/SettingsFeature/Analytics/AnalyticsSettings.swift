import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct AnalyticsSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var analyticsOptIn: Bool

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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable { case doNothing }
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
		BindingReducer(action: /Action.view)

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .binding(\.$analyticsOptIn):
					return .run { [optedIn = state.analyticsOptIn] send in
						let status = optedIn ? Analytics.OptInStatus.optedIn : Analytics.OptInStatus.optedOut
						await send(.internal(.updatedOptInStatus(analytics.setOptInStatus(status))))
					}

				case .binding:
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

			case .delegate:
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
				.listRowInsets(EdgeInsets(top: .standardSpacing, leading: 0, bottom: 0, trailing: 0))
				.listRowBackground(Color.clear)
				.listRowSeparator(.hidden)

				Section {
					Toggle(
						Strings.Settings.Analytics.shareAnonymousAnalytics,
						isOn: viewStore.$analyticsOptIn
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
			.onAppear { viewStore.send(.onAppear) }
		})
	}
}

#if DEBUG
struct AnalyticsSettingsPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			AnalyticsSettingsView(store: .init(
				initialState: withDependencies {
					$0.analytics = .init(
						initialize: { },
						setGlobalProperty: { _, _ in },
						trackEvent: { _ in },
						breadcrumb: { _ in },
						resetGameSessionID: { },
						getOptInStatus: { .optedIn },
						setOptInStatus: { _ in .optedIn },
						forceCrash: {}
					)
				} operation: {
					AnalyticsSettings.State()
				},
				reducer: AnalyticsSettings.init
			))
		}
	}
}
#endif

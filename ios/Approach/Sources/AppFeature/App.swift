import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import OnboardingFeature
import PreferenceServiceInterface
import SettingsFeature

public struct App: Reducer {
	public enum State: Equatable {
		case onboarding(Onboarding.State)
		case content(TabbedContent.State)

		public init() {
			@Dependency(\.preferences) var preferences: PreferenceService
			if preferences.bool(forKey: .appDidCompleteOnboarding) == true {
				self = .content(.init())
			} else {
				self = .onboarding(.init())
			}
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case onboarding(Onboarding.Action)
			case content(TabbedContent.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	@Dependency(\.analytics) var analytics
	@Dependency(\.preferences) var preferences

	public init() {}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .content(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .onboarding(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishOnboarding:
						state = .content(.init())
						return .merge(
							.run { _ in preferences.setKey(.appDidCompleteOnboarding, toBool: true) },
							.run { _ in await analytics.trackEvent(Analytics.App.OnboardingCompleted()) }
						)
					}

				case .content(.internal), .content(.view):
					return .none

				case .onboarding(.internal), .onboarding(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifCaseLet(/State.content, action: /Action.internal..Action.InternalAction.content) {
			TabbedContent()
		}
		.ifCaseLet(/State.onboarding, action: /Action.internal..Action.InternalAction.onboarding) {
			Onboarding()
		}
	}
}

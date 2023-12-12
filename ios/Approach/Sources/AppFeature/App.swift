import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import LaunchServiceInterface
import OnboardingFeature
import PreferenceServiceInterface
import SettingsFeature
import StatisticsRepositoryInterface

@Reducer
public struct App: Reducer {
	public enum State: Equatable {
		case onboarding(Onboarding.State)
		case content(TabbedContent.State)

		public init() {
			@Dependency(\.preferences) var preferences
			if preferences.bool(forKey: .appDidCompleteOnboarding) == true {
				self = .content(.init())
			} else {
				self = .onboarding(.init())
			}
		}
	}

	public enum Action: FeatureAction {
		public enum ViewAction {
			case didFirstAppear
		}
		public enum DelegateAction { case doNothing }
		@CasePathable public enum InternalAction {
			case onboarding(Onboarding.Action)
			case content(TabbedContent.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	@Dependency(\.preferences) var preferences
	@Dependency(\.statistics) var statistics

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					switch state {
					case .content:
						return .none
					case .onboarding:
						return .run { _ in await statistics.hideNewStatisticLabels() }
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .content(.delegate(.doNothing)):
					return .none

				case let .onboarding(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishOnboarding:
						state = .content(.init())
						return .run { _ in preferences.setKey(.appDidCompleteOnboarding, toBool: true) }
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
		.ifCaseLet(\.content, action: \.internal.content) {
			TabbedContent()
		}
		.ifCaseLet(\.onboarding, action: \.internal.onboarding) {
			Onboarding()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.onboarding(.delegate(.didFinishOnboarding))):
				return Analytics.App.OnboardingCompleted()
			default:
				return nil
			}
		}
	}
}

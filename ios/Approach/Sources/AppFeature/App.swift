import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import AppInfoServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import OnboardingFeature
import PreferenceServiceInterface
import SettingsFeature
import StatisticsRepositoryInterface

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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didFirstAppear
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case onboarding(Onboarding.Action)
			case content(TabbedContent.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	@Dependency(\.appInfo) var appInfo
	@Dependency(\.preferences) var preferences
	@Dependency(\.statistics) var statistics

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					let launchEvents: Effect<Action> = .merge(
						.run { _ in await appInfo.recordNewSession() },
						.run { _ in await appInfo.recordInstallDate() }
					)

					switch state {
					case .content:
						return launchEvents
					case .onboarding:
						// TODO: Move this to a 'first launch' service
						return .merge(
							.run { _ in await statistics.hideNewStatisticLabels() },
							launchEvents
						)
					}
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
		.ifCaseLet(/State.content, action: /Action.internal..Action.InternalAction.content) {
			TabbedContent()
		}
		.ifCaseLet(/State.onboarding, action: /Action.internal..Action.InternalAction.onboarding) {
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

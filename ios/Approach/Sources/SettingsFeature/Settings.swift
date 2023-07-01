import ComposableArchitecture
import DatabaseMockingServiceInterface
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface
import OpponentsListFeature

public struct Settings: Reducer {
	public struct State: Equatable {
		public var isShowingDeveloperOptions: Bool
		public var helpSettings = HelpSettings.State()
		public let hasOpponentsEnabled: Bool

		@PresentationState public var destination: Destination.State?

		public init() {
			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.isShowingDeveloperOptions = featureFlags.isEnabled(.developerOptions)
			self.hasOpponentsEnabled = featureFlags.isEnabled(.opponents)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapPopulateDatabase
			case didTapFeatureFlags
			case didTapOpponents
			case didTapStatistics
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case helpSettings(HelpSettings.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case featureFlags(FeatureFlagsList.State)
			case opponentsList(OpponentsList.State)
			case statistics(StatisticsSettings.State)
		}

		public enum Action: Equatable {
			case featureFlags(FeatureFlagsList.Action)
			case opponentsList(OpponentsList.Action)
			case statistics(StatisticsSettings.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.featureFlags, action: /Action.featureFlags) {
				FeatureFlagsList()
			}
			Scope(state: /State.opponentsList, action: /Action.opponentsList) {
				OpponentsList()
			}
			Scope(state: /State.statistics, action: /Action.statistics) {
				StatisticsSettings()
			}
		}
	}

	@Dependency(\.databaseMocking) var databaseMocking

	public init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.helpSettings, action: /Action.internal..Action.InternalAction.helpSettings) {
			HelpSettings()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapPopulateDatabase:
					return .run { _ in try await databaseMocking.mockDatabase() }

				case .didTapOpponents:
					state.destination = .opponentsList(.init())
					return .none

				case .didTapFeatureFlags:
					state.destination = .featureFlags(.init())
					return .none

				case .didTapStatistics:
					state.destination = .statistics(.init())
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .helpSettings(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.featureFlags(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.opponentsList(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.statistics(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.featureFlags(.internal))),
						.destination(.presented(.featureFlags(.view))),
						.destination(.presented(.statistics(.internal))),
						.destination(.presented(.statistics(.view))),
						.destination(.presented(.statistics(.binding))),
						.destination(.presented(.opponentsList(.internal))),
						.destination(.presented(.opponentsList(.view))):
					return .none

				case .helpSettings(.internal), .helpSettings(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}
}

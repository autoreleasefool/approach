import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface
import OpponentsListFeature

public struct Settings: Reducer {
	public struct State: Equatable {
		public var showsFeatures: Bool
		public var helpSettings = HelpSettings.State()
		public let hasOpponentsEnabled: Bool

		@PresentationState public var destination: Destination.State?

		public init() {
			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.showsFeatures = featureFlags.isEnabled(.developerOptions)
			self.hasOpponentsEnabled = featureFlags.isEnabled(.opponents)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapFeatureFlags
			case didTapOpponents
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
		}

		public enum Action: Equatable {
			case featureFlags(FeatureFlagsList.Action)
			case opponentsList(OpponentsList.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.featureFlags, action: /Action.featureFlags) {
				FeatureFlagsList()
			}
			Scope(state: /State.opponentsList, action: /Action.opponentsList) {
				OpponentsList()
			}
		}
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.helpSettings, action: /Action.internal..Action.InternalAction.helpSettings) {
			HelpSettings()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapOpponents:
					state.destination = .opponentsList(.init())
					return .none

				case .didTapFeatureFlags:
					state.destination = .featureFlags(.init())
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

				case .destination(.dismiss),
						.destination(.presented(.featureFlags(.internal))),
						.destination(.presented(.featureFlags(.view))),
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

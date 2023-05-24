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
		public var featureFlagsList = FeatureFlagsList.State()
		public var opponentsList = OpponentsList.State()
		public let hasOpponentsEnabled: Bool

		public init() {
			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.showsFeatures = featureFlags.isEnabled(.developerOptions)
			self.hasOpponentsEnabled = featureFlags.isEnabled(.opponents)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case opponentsList(OpponentsList.Action)
			case helpSettings(HelpSettings.Action)
			case featureFlagsList(FeatureFlagsList.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		Scope(state: \.helpSettings, action: /Action.internal..Action.InternalAction.helpSettings) {
			HelpSettings()
		}
		Scope(state: \.featureFlagsList, action: /Action.internal..Action.InternalAction.featureFlagsList) {
			FeatureFlagsList()
		}
		Scope(state: \.opponentsList, action: /Action.internal..Action.InternalAction.opponentsList) {
			OpponentsList()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case let .featureFlagsList(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .helpSettings(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .opponentsList(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .featureFlagsList(.internal), .featureFlagsList(.view):
					return .none

				case .helpSettings(.internal), .helpSettings(.view):
					return .none

				case.opponentsList(.view), .opponentsList(.internal):
					return .none
				}

			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

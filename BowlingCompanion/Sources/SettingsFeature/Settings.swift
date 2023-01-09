import ComposableArchitecture
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface
import OpponentsListFeature

public struct Settings: ReducerProtocol {
	public struct State: Equatable {
		public var showsFeatures: Bool
		public var helpSettings = HelpSettings.State()
		public var featureFlagsList = FeatureFlagsList.State()
		public var opponentsList = OpponentsList.State()
		public let hasOpponentsEnabled: Bool

		public init(hasDeveloperFeature: Bool, hasOpponentsEnabled: Bool) {
			self.showsFeatures = hasDeveloperFeature
			self.hasOpponentsEnabled = hasOpponentsEnabled
		}
	}

	public enum Action: Equatable {
		case opponentsList(OpponentsList.Action)
		case helpSettings(HelpSettings.Action)
		case featureFlagsList(FeatureFlagsList.Action)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.helpSettings, action: /Settings.Action.helpSettings) {
			HelpSettings()
		}
		Scope(state: \.featureFlagsList, action: /Settings.Action.featureFlagsList) {
			FeatureFlagsList()
		}
		Scope(state: \.opponentsList, action: /Settings.Action.opponentsList) {
			OpponentsList()
		}

		Reduce { _, action in
			switch action {
			case .featureFlagsList, .helpSettings, .opponentsList:
				return .none
			}
		}
	}
}

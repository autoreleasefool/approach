import ComposableArchitecture
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface

public struct Settings: ReducerProtocol {
	public struct State: Equatable {
		public var showsFeatures: Bool
		public var helpSettings = HelpSettings.State()
		public var featureFlagsList = FeatureFlagsList.State()

		public init(hasDeveloperFeature: Bool) {
			self.showsFeatures = hasDeveloperFeature
		}
	}

	public enum Action: Equatable {
		case helpSettings(HelpSettings.Action)
		case featureFlagsList(FeatureFlagsList.Action)
		case placeholder
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.helpSettings, action: /Settings.Action.helpSettings) {
			HelpSettings()
		}
		Scope(state: \.featureFlagsList, action: /Settings.Action.featureFlagsList) {
			FeatureFlagsList()
		}

		Reduce { _, action in
			switch action {
			case .featureFlagsList, .helpSettings:
				return .none

			case .placeholder:
				return .none
			}
		}
	}
}

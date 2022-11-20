import ComposableArchitecture
import FeatureFlagListFeature
import FeatureFlagServiceInterface

public struct Settings: ReducerProtocol {
	public struct State: Equatable {
		public var showsFeatures: Bool
		public var helpSettings = HelpSettings.State()
		public var featureFlagList = FeatureFlagList.State()

		public init() {
			@Dependency(\.featureFlags) var featureFlags: FeatureFlagService
			self.showsFeatures = featureFlags.isEnabled(.developerOptions)
		}
	}

	public enum Action: Equatable {
		case helpSettings(HelpSettings.Action)
		case featureFlagList(FeatureFlagList.Action)
		case placeholder
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.helpSettings, action: /Settings.Action.helpSettings) {
			HelpSettings()
		}
		Scope(state: \.featureFlagList, action: /Settings.Action.featureFlagList) {
			FeatureFlagList()
		}

		Reduce { _, action in
			switch action {
			case .featureFlagList, .helpSettings:
				return .none

			case .placeholder:
				return .none
			}
		}
	}
}

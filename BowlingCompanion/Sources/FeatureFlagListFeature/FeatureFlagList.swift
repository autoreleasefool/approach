import ComposableArchitecture
import FeatureFlagLibrary
import FeatureFlagServiceInterface

public struct FeatureFlagList: ReducerProtocol {
	public struct State: Equatable {
		public var featureFlags: [FeatureFlagItem] = []

		public init() {}
	}

	public enum Action: Equatable {
		case subscribeToFlags
		case flagsResponse([FeatureFlagItem])
		case toggle(FeatureFlag)
		case resetOverridesButtonTapped
	}

	public struct FeatureFlagItem: Equatable {
		let flag: FeatureFlag
		let enabled: Bool
	}

	public init() {}

	@Dependency(\.featureFlags) var featureFlagService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToFlags:
				return .run { send in
					let observedFlags = FeatureFlag.allFlags
					for await flags in featureFlagService.observeAll(observedFlags) {
						await send(.flagsResponse(zip(observedFlags, flags).map(FeatureFlagItem.init)))
					}
				}

			case let .flagsResponse(featureFlags):
				state.featureFlags = featureFlags
				return .none

			case let .toggle(featureFlag):
				let isEnabled = featureFlagService.isEnabled(featureFlag)
				featureFlagService.setEnabled(featureFlag, !isEnabled)
				return .none

			case .resetOverridesButtonTapped:
				FeatureFlag.allFlags.forEach { featureFlagService.setEnabled($0, nil) }
				return .none
			}
		}
	}
}

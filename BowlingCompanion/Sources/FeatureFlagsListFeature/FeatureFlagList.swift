import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface

public struct FeatureFlagsList: ReducerProtocol {
	public struct State: Equatable {
		public var featureFlags: [FeatureFlagItem] = []

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didStartObservingFlags
			case didToggle(FeatureFlag)
			case didTapResetOverridesButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadFlags([FeatureFlagItem])
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct FeatureFlagItem: Equatable {
		let flag: FeatureFlag
		let enabled: Bool
	}

	public init() {}

	@Dependency(\.featureFlags) var featureFlagService

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartObservingFlags:
					return .run { send in
						let observedFlags = FeatureFlag.allFlags
						for await flags in featureFlagService.observeAll(observedFlags) {
							await send(.internal(.didLoadFlags(zip(observedFlags, flags).map(FeatureFlagItem.init))))
						}
					}

					case let .didToggle(featureFlag):
						let isEnabled = featureFlagService.isEnabled(featureFlag)
						featureFlagService.setEnabled(featureFlag, !isEnabled)
						return .none

				case .didTapResetOverridesButton:
					for flag in FeatureFlag.allFlags {
						featureFlagService.setEnabled(flag, nil)
					}
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadFlags(featureFlags):
					state.featureFlags = featureFlags
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface

@Reducer
public struct FeatureFlagsList: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var featureFlags: [FeatureFlagItem] = []

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didStartObservingFlags
			case didToggle(FeatureFlag)
			case didTapResetOverridesButton
			case didTapMatchReleaseButton
			case didTapMatchDevelopmentButton
			case didTapMatchTestButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didLoadFlags([FeatureFlagItem])
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public struct FeatureFlagItem: Equatable {
		let flag: FeatureFlag
		let enabled: Bool
	}

	public init() {}

	@Dependency(\.featureFlags) var featureFlagService

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartObservingFlags:
					return .run { send in
						let observedFlags = FeatureFlag.allFlags
						for await flags in featureFlagService.observeAll(observedFlags) {
							await send(.internal(.didLoadFlags(observedFlags.map {
								FeatureFlagItem(flag: $0, enabled: flags[$0] ?? false)
							})))
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

				case .didTapMatchReleaseButton:
					for flag in FeatureFlag.allFlags where flag.isOverridable {
						featureFlagService.setEnabled(flag, flag.stage >= .release)
					}
					return .none

				case .didTapMatchDevelopmentButton:
					for flag in FeatureFlag.allFlags where flag.isOverridable {
						featureFlagService.setEnabled(flag, flag.stage >= .development)
					}
					return .none

				case .didTapMatchTestButton:
					for flag in FeatureFlag.allFlags where flag.isOverridable {
						featureFlagService.setEnabled(flag, flag.stage >= .test)
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

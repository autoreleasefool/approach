import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary

public struct FeatureFlagItem: Equatable {
	let flag: FeatureFlag
	var enabled: Bool
}

@Reducer
public struct FeatureFlagsList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var featureFlags: IdentifiedArrayOf<FeatureFlagToggle.State> = []

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didStartObservingFlags
			case didTapResetOverridesButton
			case didTapMatchReleaseButton
			case didTapMatchDevelopmentButton
			case didTapMatchTestButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didLoadFlags([FeatureFlagItem])
			case featureFlagToggle(IdentifiedActionOf<FeatureFlagToggle>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
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
						for await flags in try featureFlagService.observeAll(observedFlags) {
							await send(.internal(.didLoadFlags(observedFlags.map {
								FeatureFlagItem(flag: $0, enabled: flags[$0] ?? false)
							})))
						}
					}

				case .didTapResetOverridesButton:
					return .run { _ in
						for flag in FeatureFlag.allFlags {
							featureFlagService.setEnabled(flag, nil)
						}
					}

				case .didTapMatchReleaseButton:
					return .run { _ in
						for flag in FeatureFlag.allFlags where flag.isOverridable {
							featureFlagService.setEnabled(flag, flag.stage >= .release)
						}
					}

				case .didTapMatchDevelopmentButton:
					return .run { _ in
						for flag in FeatureFlag.allFlags where flag.isOverridable {
							featureFlagService.setEnabled(flag, flag.stage >= .development)
						}
					}

				case .didTapMatchTestButton:
					return .run { _ in
						for flag in FeatureFlag.allFlags where flag.isOverridable {
							featureFlagService.setEnabled(flag, flag.stage >= .test)
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadFlags(featureFlags):
					state.featureFlags = .init(uniqueElements: featureFlags.map { FeatureFlagToggle.State(flag: $0 ) })
					return .none

				case let .featureFlagToggle(.element(id, .binding(\.flag))):
					guard let flag = FeatureFlag.find(byId: id) else { return .none }
					return .run { _ in
						let isEnabled = featureFlagService.isFlagEnabled(flag)
						featureFlagService.setEnabled(flag, !isEnabled)
					}

				case .featureFlagToggle:
					return .none
				}

			case .delegate:
				return .none
			}
		}.forEach(\.featureFlags, action: \.internal.featureFlagToggle) {
			FeatureFlagToggle()
		}
	}
}

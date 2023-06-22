import ComposableArchitecture
import FeatureFlagsLibrary
import StringsLibrary
import SwiftUI

public struct FeatureFlagsListView: View {
	let store: StoreOf<FeatureFlagsList>

	struct ViewState: Equatable {
		let featureFlags: [FeatureFlagsList.FeatureFlagItem]

		init(state: FeatureFlagsList.State) {
			self.featureFlags = state.featureFlags
		}
	}

	enum ViewAction {
		case didStartObservingFlags
		case didToggle(flag: FeatureFlag)
		case didTapResetOverridesButton
		case didTapMatchReleaseButton
		case didTapMatchTestButton
		case didTapMatchDevelopmentButton
	}

	public init(store: StoreOf<FeatureFlagsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: FeatureFlagsList.Action.init) { viewStore in
			List {
				Section {
					Button(Strings.Action.reset) { viewStore.send(.didTapResetOverridesButton) }
					Button(Strings.Settings.FeatureFlags.matchRelease) { viewStore.send(.didTapMatchReleaseButton) }
					Button(Strings.Settings.FeatureFlags.matchTest) { viewStore.send(.didTapMatchTestButton) }
					Button(Strings.Settings.FeatureFlags.matchDevelopment) { viewStore.send(.didTapMatchDevelopmentButton) }
				}

				Section(Strings.Settings.FeatureFlags.title) {
					ForEach(viewStore.featureFlags, id: \.flag.id) { item in
						Toggle(
							item.flag.name,
							isOn: viewStore.binding(
								get: { _ in item.enabled },
								send: { _ in ViewAction.didToggle(flag: item.flag) }
							)
						).disabled(!item.flag.isOverridable)
					}
				}
			}
			.navigationTitle(Strings.Settings.FeatureFlags.title)
			.task { await viewStore.send(.didStartObservingFlags).finish() }
		}
	}
}

extension FeatureFlagsList.Action {
	init(action: FeatureFlagsListView.ViewAction) {
		switch action {
		case .didStartObservingFlags:
			self = .view(.didStartObservingFlags)
		case let .didToggle(featureFlag):
			self = .view(.didToggle(featureFlag))
		case .didTapResetOverridesButton:
			self = .view(.didTapResetOverridesButton)
		case .didTapMatchReleaseButton:
			self = .view(.didTapMatchReleaseButton)
		case .didTapMatchTestButton:
			self = .view(.didTapMatchTestButton)
		case .didTapMatchDevelopmentButton:
			self = .view(.didTapMatchDevelopmentButton)
		}
	}
}

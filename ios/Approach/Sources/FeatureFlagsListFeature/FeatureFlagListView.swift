import ComposableArchitecture
import FeatureFlagsLibrary
import StringsLibrary
import SwiftUI

@ViewAction(for: FeatureFlagsList.self)
public struct FeatureFlagsListView: View {
	public var store: StoreOf<FeatureFlagsList>

	public init(store: StoreOf<FeatureFlagsList>) {
		self.store = store
	}

	public var body: some View {
		List {
			Section {
				Button(Strings.Action.reset) { send(.didTapResetOverridesButton) }
				Button(Strings.Settings.FeatureFlags.matchRelease) { send(.didTapMatchReleaseButton) }
				Button(Strings.Settings.FeatureFlags.matchTest) { send(.didTapMatchTestButton) }
				Button(Strings.Settings.FeatureFlags.matchDevelopment) { send(.didTapMatchDevelopmentButton) }
			}

			Section(Strings.Settings.FeatureFlags.title) {
				ForEach(store.scope(state: \.featureFlags, action: \.internal.featureFlagToggle)) { store in
					FeatureFlagToggleView(store: store)
				}
			}
		}
		.navigationTitle(Strings.Settings.FeatureFlags.title)
		.task { await send(.didStartObservingFlags).finish() }
	}
}

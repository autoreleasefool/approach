import ComposableArchitecture
import FeatureFlagsLibrary
import StringsLibrary
import SwiftUI

@ViewAction(for: FeatureFlagsList.self)
public struct FeatureFlagsListView: View {
	@Perception.Bindable public var store: StoreOf<FeatureFlagsList>

	public init(store: StoreOf<FeatureFlagsList>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				Section {
					Button(Strings.Action.reset) { send(.didTapResetOverridesButton) }
					Button(Strings.Settings.FeatureFlags.matchRelease) { send(.didTapMatchReleaseButton) }
					Button(Strings.Settings.FeatureFlags.matchTest) { send(.didTapMatchTestButton) }
					Button(Strings.Settings.FeatureFlags.matchDevelopment) { send(.didTapMatchDevelopmentButton) }
				}

				// FIXME: Can't using store.binding here to bind feature flag state
//				Section(Strings.Settings.FeatureFlags.title) {
//					ForEach(store.featureFlags, id: \.flag.id) { item in
//						Toggle(
//							item.flag.name,
//							isOn: store.binding(
//								get: { _ in item.enabled },
//								send: { _ in .didToggle(item.flag) }
//							)
//						).disabled(!item.flag.isOverridable)
//					}
//				}
			}
			.navigationTitle(Strings.Settings.FeatureFlags.title)
			.task { await send(.didStartObservingFlags).finish() }
		}
	}
}

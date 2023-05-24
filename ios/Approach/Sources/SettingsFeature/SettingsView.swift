import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsListFeature
import OpponentsListFeature
import StringsLibrary
import SwiftUI

public struct SettingsView: View {
	let store: StoreOf<Settings>

	struct ViewState: Equatable {
		let showsFeatures: Bool
		let showsOpponents: Bool

		init(state: Settings.State) {
			self.showsFeatures = state.showsFeatures
			self.showsOpponents = state.hasOpponentsEnabled
		}
	}

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init) { viewStore in
			List {
				if viewStore.showsFeatures {
					Section {
						NavigationLink(
							Strings.Settings.FeatureFlags.title,
							destination: FeatureFlagsListView(
								store: store.scope(state: \.featureFlagsList, action: /Settings.Action.InternalAction.featureFlagsList)
							)
						)
					}
				}

				if viewStore.showsOpponents {
					Section {
						NavigationLink(
							Strings.Opponent.List.title,
							destination: OpponentsListView(
								store: store.scope(state: \.opponentsList, action: /Settings.Action.InternalAction.opponentsList)
							)
						)
					}
				}

				HelpSettingsView(store: store.scope(state: \.helpSettings, action: /Settings.Action.InternalAction.helpSettings))
				VersionView()
			}
			.navigationTitle(Strings.Settings.title)
		}
	}
}

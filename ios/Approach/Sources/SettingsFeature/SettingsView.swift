import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsListFeature
import OpponentsListFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

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

	enum ViewAction {
		case didTapFeatureFlags
		case didTapOpponents
	}

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: Settings.Action.init) { viewStore in
			List {
				if viewStore.showsFeatures {
					Section {
						Button { viewStore.send(.didTapFeatureFlags) } label: {
							Text(Strings.Settings.FeatureFlags.title)
						}
						.buttonStyle(.navigation)
					}
				}

				if viewStore.showsOpponents {
					Section {
						Button { viewStore.send(.didTapOpponents) } label: {
							Text(Strings.Opponent.List.title)
						}
						.buttonStyle(.navigation)
					}
				}

				HelpSettingsView(store: store.scope(state: \.helpSettings, action: /Settings.Action.InternalAction.helpSettings))
				VersionView()
			}
			.navigationTitle(Strings.Settings.title)
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /Settings.Destination.State.opponentsList,
			action: Settings.Destination.Action.opponentsList
		) { store in
			OpponentsListView(store: store)
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /Settings.Destination.State.featureFlags,
			action: Settings.Destination.Action.featureFlags
		) { store in
			FeatureFlagsListView(store: store)
		}
	}
}

extension Settings.Action {
	init(action: SettingsView.ViewAction) {
		switch action {
		case .didTapFeatureFlags:
			self = .view(.didTapFeatureFlags)
		case .didTapOpponents:
			self = .view(.didTapOpponents)
		}
	}
}

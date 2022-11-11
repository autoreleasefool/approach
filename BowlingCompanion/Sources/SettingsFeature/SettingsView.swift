import ComposableArchitecture
import FeatureFlagListFeature
import SwiftUI

public struct SettingsView: View {
	let store: StoreOf<Settings>

	struct ViewState: Equatable {
		let showsFeatures: Bool

		init(state: Settings.State) {
			self.showsFeatures = state.showsFeatures
		}
	}

	enum ViewAction {
		case placeholder
	}

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: Settings.Action.init) { viewStore in
			List {
				Section {
					if viewStore.showsFeatures {
						NavigationLink(
							"Features",
							destination: FeatureFlagListView(
								store: store.scope(state: \.featureFlagList, action: Settings.Action.featureFlagList)
							)
						)
					}
				}
			}
			.navigationTitle("Settings")
		}
	}
}

extension Settings.Action {
	init(action: SettingsView.ViewAction) {
		switch action {
		case .placeholder:
			self = .placeholder
		}
	}
}

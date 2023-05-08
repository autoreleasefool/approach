import ComposableArchitecture
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GamesSettingsView: View {
	let store: StoreOf<GamesSettings>

	struct ViewState: Equatable {
		init(state: GamesSettings.State) {}
	}

	enum ViewAction {
		case didTapDone
	}

	init(store: StoreOf<GamesSettings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesSettings.Action.init) { viewStore in
			EmptyView()
				.navigationTitle(Strings.Game.Settings.title)
				.toolbar {
					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.done) { viewStore.send(.didTapDone) }
					}
				}
		}
	}
}

extension GamesSettings.Action {
	init(action: GamesSettingsView.ViewAction) {
		switch action {
		case .didTapDone:
			self = .delegate(.didFinish)
		}
	}
}

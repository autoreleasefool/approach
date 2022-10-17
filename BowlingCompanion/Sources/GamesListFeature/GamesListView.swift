import ComposableArchitecture
import DateTimeLibrary
import SharedModelsLibrary
import SwiftUI

public struct GamesListView: View {
	let store: StoreOf<GamesList>

	struct ViewState: Equatable {
		let games: IdentifiedArrayOf<Game>
		let seriesDate: Date

		init(state: GamesList.State) {
			self.games = state.games
			self.seriesDate = state.series.date
		}
	}

	enum ViewAction {
		case subscribeToGames
	}

	public init(store: StoreOf<GamesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesList.Action.init) { viewStore in
			List(viewStore.games) {
				Text("Game \($0.ordinal)")
			}
			.navigationTitle(viewStore.seriesDate.regularDateFormat)
			.task { await viewStore.send(.subscribeToGames).finish() }
		}
	}
}

extension GamesList.Action {
	init(action: GamesListView.ViewAction) {
		switch action {
		case .subscribeToGames:
			self = .subscribeToGames
		}
	}
}

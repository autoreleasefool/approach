import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct MidGameStatisticsDetailsView: View {
	let store: StoreOf<MidGameStatisticsDetails>

	struct ViewState: Equatable {
		let games: IdentifiedArrayOf<Game.Indexed>
		@BindingViewState var selectedGame: Game.ID?
	}

	public init(store: StoreOf<MidGameStatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			VStack {
				StatisticsDetailsListView(
					store: store.scope(state: \.list, action: \.internal.list)
				) {
					Section {
						Picker(
							Strings.Statistics.Filter.filterByGame,
							selection: viewStore.$selectedGame
						) {
							Text(Strings.Statistics.Filter.allGames).tag(nil as Game.ID?)
							ForEach(viewStore.games) {
								Text(Strings.Game.titleWithOrdinal($0.index + 1)).tag(Optional($0.id))
							}
						}
					}
				}
			}
			.navigationTitle(Strings.Statistics.title)
			.navigationBarTitleDisplayMode(.inline)
			.onAppear { viewStore.send(.onAppear) }
			.onFirstAppear { viewStore.send(.didFirstAppear) }
			.task { await viewStore.send(.task).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}
}

extension MidGameStatisticsDetailsView.ViewState {
	init(store: BindingViewStore<MidGameStatisticsDetails.State>) {
		self._selectedGame = store.$selectedGame
		self.games = store.games
	}
}

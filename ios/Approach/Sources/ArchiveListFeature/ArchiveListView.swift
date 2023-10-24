import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ArchiveListView: View {
	let store: StoreOf<ArchiveList>

	public init(store: StoreOf<ArchiveList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					Text(Strings.Archive.List.description)
				}

				Section(Strings.Archive.List.Bowlers.title) {
					if viewStore.archivedBowlers.isEmpty {
						Text(Strings.Archive.List.Bowlers.none)
					}

					ForEach(viewStore.archivedBowlers) { bowler in
						Bowler.ArchivedView(bowler)
							.swipeActions(allowsFullSwipe: true) {
								UnarchiveButton { viewStore.send(.didSwipeBowler(bowler)) }
							}
					}
				}

				Section(Strings.Archive.List.Leagues.title) {
					if viewStore.archivedLeagues.isEmpty {
						Text(Strings.Archive.List.Leagues.none)
					}

					ForEach(viewStore.archivedLeagues) { league in
						League.ArchivedView(league)
							.swipeActions(allowsFullSwipe: true) {
								UnarchiveButton { viewStore.send(.didSwipeLeague(league)) }
							}
					}
				}

				Section(Strings.Archive.List.Series.title) {
					if viewStore.archivedSeries.isEmpty {
						Text(Strings.Archive.List.Series.none)
					}

					ForEach(viewStore.archivedSeries) { series in
						Series.ArchivedView(series)
							.swipeActions(allowsFullSwipe: true) {
								UnarchiveButton { viewStore.send(.didSwipeSeries(series)) }
							}
					}
				}

				Section(Strings.Archive.List.Games.title) {
					if viewStore.archivedGames.isEmpty {
						Text(Strings.Archive.List.Games.none)
					}

					ForEach(viewStore.archivedGames) { game in
						Game.ArchivedView(game)
							.swipeActions(allowsFullSwipe: true) {
								UnarchiveButton { viewStore.send(.didSwipeGame(game)) }
							}
					}
				}
			}
			.navigationTitle(Strings.Archive.title)
			.task { await viewStore.send(.observeData).finish() }
			.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
			.alert(store: store.scope(state: \.$alert, action: { .view(.alert($0)) }))
		})
	}
}

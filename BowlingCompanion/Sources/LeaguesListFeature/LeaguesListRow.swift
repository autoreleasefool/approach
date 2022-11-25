import ComposableArchitecture
import SeriesListFeature
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct LeaguesListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<LeaguesListView.ViewState, LeaguesListView.ViewAction>
	typealias Store = ComposableArchitecture.Store<SeriesList.State?, SeriesList.Action>

	let viewStore: ViewStore
	let destination: Store
	let league: League

	init(viewStore: ViewStore, destination: Store, league: League) {
		self.viewStore = viewStore
		self.destination = destination
		self.league = league
	}

	var body: some View {
		NavigationLink(
			destination: IfLetStore(destination) {
				SeriesListView(store: $0)
			},
			tag: league.id,
			selection: viewStore.binding(
				get: \.selection,
				send: LeaguesListView.ViewAction.setNavigation(selection:)
			)
		) {
			AvatarView(size: .medium, title: league.name)
				.swipeActions(allowsFullSwipe: true) {
					EditButton { viewStore.send(.swipeAction(league, .edit)) }
					DeleteButton { viewStore.send(.swipeAction(league, .delete)) }
				}
		}
	}
}

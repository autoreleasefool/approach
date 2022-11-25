import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct BowlersListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<BowlersListView.ViewState, BowlersListView.ViewAction>
	typealias Store = ComposableArchitecture.Store<LeaguesList.State?, LeaguesList.Action>

	let viewStore: ViewStore
	let destination: Store
	let bowler: Bowler

	init(viewStore: ViewStore, destination: Store, bowler: Bowler) {
		self.viewStore = viewStore
		self.destination = destination
		self.bowler = bowler
	}

	var body: some View {
		NavigationLink(
			destination: IfLetStore(destination) {
				LeaguesListView(store: $0)
			},
			tag: bowler.id,
			selection: viewStore.binding(
				get: \.selection,
				send: BowlersListView.ViewAction.setNavigation(selection:)
			)
		) {
			AvatarView(size: .medium, title: bowler.name)
				.swipeActions(allowsFullSwipe: true) {
					EditButton { viewStore.send(.swipeAction(bowler, .edit)) }
					DeleteButton { viewStore.send(.swipeAction(bowler, .delete)) }
				}
		}
	}
}

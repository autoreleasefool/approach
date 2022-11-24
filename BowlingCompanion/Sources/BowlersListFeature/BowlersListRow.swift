import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary

struct BowlersListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<BowlersListView.ViewState, BowlersListView.ViewAction>

	let viewStore: ViewStore
	let destination: Store<LeaguesList.State?, LeaguesList.Action>
	let bowler: Bowler

	init(viewStore: ViewStore, destination: Store<LeaguesList.State?, LeaguesList.Action>, bowler: Bowler) {
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
			Text(bowler.name)
				.swipeActions(allowsFullSwipe: true) {
					Button {
						viewStore.send(.swipeAction(bowler, .edit))
					} label: {
						Label("Edit", systemImage: "pencil")
					}
					.tint(.blue)

					Button(role: .destructive) {
						viewStore.send(.swipeAction(bowler, .delete))
					} label: {
						Label("Delete", systemImage: "trash")
					}
					.tint(Theme.Colors.destructive)
				}
		}
	}
}

import ComposableArchitecture
import DateTimeLibrary
import SeriesSidebarFeature
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct SeriesListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<SeriesListView.ViewState, SeriesListView.ViewAction>
	typealias Store = ComposableArchitecture.Store<SeriesSidebar.State?, SeriesSidebar.Action>

	let viewStore: ViewStore
	let destination: Store
	let series: Series

	init(viewStore: ViewStore, destination: Store, series: Series) {
		self.viewStore = viewStore
		self.destination = destination
		self.series = series
	}

	var body: some View {
		NavigationLink(
			destination: IfLetStore(destination) {
				SeriesSidebarView(store: $0)
			},
			tag: series.id,
			selection: viewStore.binding(
				get: \.selection,
				send: SeriesListView.ViewAction.setNavigation(selection:)
			)
		) {
			Text(series.date.longFormat)
				.swipeActions(allowsFullSwipe: true) {
					EditButton { viewStore.send(.swipeAction(series, .edit)) }
					DeleteButton { viewStore.send(.swipeAction(series, .delete)) }
				}
		}
	}
}

import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import GamesListFeature
import LeagueEditorFeature
import ModelsLibrary
import SeriesEditorFeature
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String
		let ordering: Series.Ordering
		let preBowlSeries: IdentifiedArrayOf<Series.List>
		let regularSeries: IdentifiedArrayOf<Series.List>

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
			self.ordering = state.ordering

			switch state.ordering {
			case .newestFirst:
				self.preBowlSeries = state.series.filter {
					switch $0.preBowl {
					case .preBowl: return true
					case .regular: return false
					}
				}
				self.regularSeries = state.series.filter {
					switch $0.preBowl {
					case .preBowl: return false
					case .regular: return true
					}
				}
			case .oldestFirst, .highestToLowest, .lowestToHighest:
				self.preBowlSeries = []
				self.regularSeries = state.series
			}
		}
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				if !viewStore.preBowlSeries.isEmpty {
					Section(Strings.Series.PreBowl.title) {
						ForEach(viewStore.preBowlSeries) { series in
							SeriesListItem(series: series) {
								viewStore.send(.didTapSeries(series.id))
							} onEdit: {
								viewStore.send(.didSwipeSeries(.edit, series.id))
							} onDelete: {
								viewStore.send(.didSwipeSeries(.delete, series.id))
							}
						}
					}
				}

				if !viewStore.regularSeries.isEmpty {
					Section(Strings.Series.List.title) {
						ForEach(viewStore.regularSeries) { series in
							SeriesListItem(series: series) {
								viewStore.send(.didTapSeries(series.id))
							} onEdit: {
								viewStore.send(.didSwipeSeries(.edit, series.id))
							} onDelete: {
								viewStore.send(.didSwipeSeries(.delete, series.id))
							}
						}
					}
				}
			}
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					EditButton { viewStore.send(.didTapEditButton) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.didTapAddButton) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.task { await viewStore.send(.didObserveData).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.alert(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.alert,
			action: SeriesList.Destination.Action.alert
		)
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.seriesEditor,
			action: SeriesList.Destination.Action.seriesEditor
		) { store in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.leagueEditor,
			action: SeriesList.Destination.Action.leagueEditor
		) { store in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.sortOrder,
			action: SeriesList.Destination.Action.sortOrder
		) { (store: StoreOf<SortOrderLibrary.SortOrder<Series.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.games,
			action: SeriesList.Destination.Action.games
		) { store in
			GamesListView(store: store)
		}
	}
}

import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import GamesListFeature
import ModelsLibrary
import SeriesEditorFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String
		let preBowlSeries: IdentifiedArrayOf<Series.List>
		let regularSeries: IdentifiedArrayOf<Series.List>

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
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
					AddButton { viewStore.send(.didTapAddButton) }
				}
			}
			.task { await viewStore.send(.didObserveData).finish() }
		})
		.alert(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.alert,
			action: SeriesList.Destination.Action.alert
		)
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /SeriesList.Destination.State.editor,
			action: SeriesList.Destination.Action.editor
		) { store in
			NavigationStack {
				SeriesEditorView(store: store)
			}
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

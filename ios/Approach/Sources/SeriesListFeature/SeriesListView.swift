import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import GamesListFeature
import LeagueEditorFeature
import ModelsLibrary
import ResourceListLibrary
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

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
			self.ordering = state.ordering
		}
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			SectionResourceListView(
				store: store.scope(state: \.list, action: /SeriesList.Action.InternalAction.list)
			) { _, series in
				Button { viewStore.send(.didTapSeries(series.id)) } label: {
					SeriesListItem(series: series)
				}
				.buttonStyle(.plain)
				.listRowInsets(EdgeInsets())
				.alignmentGuide(.listRowSeparatorLeading) { d in
						d[.leading]
				}
			}
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					EditButton { viewStore.send(.didTapEditButton) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.seriesEditor(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.leagueEditor(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.sortOrder(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.gamesList(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
	}
}

@MainActor extension View {
	fileprivate typealias State = PresentationState<SeriesList.Destination.State>
	fileprivate typealias Action = PresentationAction<SeriesList.Destination.Action>

	fileprivate func seriesEditor(_ store: Store<State, Action>) -> some View {
		sheet(
			store: store,
			state: /SeriesList.Destination.State.seriesEditor,
			action: SeriesList.Destination.Action.seriesEditor
		) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func leagueEditor(_ store: Store<State, Action>) -> some View {
		sheet(
			store: store,
			state: /SeriesList.Destination.State.leagueEditor,
			action: SeriesList.Destination.Action.leagueEditor
		) { (store: StoreOf<LeagueEditor>) in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: Store<State, Action>) -> some View {
		sheet(
			store: store,
			state: /SeriesList.Destination.State.sortOrder,
			action: SeriesList.Destination.Action.sortOrder
		) { (store: StoreOf<SortOrderLibrary.SortOrder<Series.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func gamesList(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /SeriesList.Destination.State.games,
			action: SeriesList.Destination.Action.games
		) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}

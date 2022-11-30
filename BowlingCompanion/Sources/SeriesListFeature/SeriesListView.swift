import ComposableArchitecture
import DateTimeLibrary
import SeriesEditorFeature
import SeriesSidebarFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String
		let listState: ListContentState<Series, ListErrorContent>
		let selection: Series.ID?
		let numberOfGames: Int?
		let isNewSeriesCreated: Bool
		let isSeriesEditorPresented: Bool

		init(state: SeriesList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let series = state.series {
				self.listState = .loaded(series)
			} else {
				self.listState = .loading
			}
			self.leagueName = state.league.name
			self.selection = state.selection?.id
			self.numberOfGames = state.league.numberOfGames
			self.isNewSeriesCreated = state.newSeries != nil
			self.isSeriesEditorPresented = state.seriesEditor != nil
		}
	}

	enum ViewAction {
		case refreshList
		case addButtonTapped
		case errorButtonTapped
		case setNavigation(selection: Series.ID?)
		case setEditorFormSheet(isPresented: Bool)
		case dismissNewSeries
		case swipeAction(Series, SeriesList.SwipeAction)
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesList.Action.init) { viewStore in
			ListContent(viewStore.listState) { series in
				Section(Strings.Series.List.sectionTitle) {
					ForEach(series) { series in
						NavigationLink(
							destination: IfLetStore(store.scope(state: \.selection?.value, action: SeriesList.Action.seriesSidebar)) {
								SeriesSidebarView(store: $0)
							},
							tag: series.id,
							selection: viewStore.binding(
								get: \.selection,
								send: SeriesListView.ViewAction.setNavigation(selection:)
							)
						) {
							SeriesRow(
								series: series,
								onEdit: { viewStore.send(.swipeAction(series, .edit)) },
								onDelete: { viewStore.send(.swipeAction(series, .delete)) }
							)
						}
					}
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					.emptySeries,
					title: Strings.Series.Errors.Empty.title,
					message: Strings.Series.Errors.Empty.message
				) {
					EmptyContentAction(title: Strings.Series.List.add) { viewStore.send(.addButtonTapped) }
				}
			} error: { error in
				ListEmptyContent(
					.errorNotFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) { viewStore.send(.errorButtonTapped) }
				}
			}
			.scrollContentBackground(.hidden)
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					NavigationLink(
						destination: IfLetStore(
							store.scope(
								state: \.newSeries,
								action: SeriesList.Action.seriesSidebar
							)
						) {
							SeriesSidebarView(store: $0)
						},
						isActive: viewStore.binding(
							get: \.isNewSeriesCreated,
							send: { $0 ? .addButtonTapped : .dismissNewSeries }
						)
					) {
						Image(systemName: "plus")
					}
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isSeriesEditorPresented,
				send: ViewAction.setEditorFormSheet(isPresented: false)
			)) {
				IfLetStore(store.scope(state: \.seriesEditor, action: SeriesList.Action.seriesEditor)) { scopedStore in
					NavigationView {
						SeriesEditorView(store: scopedStore)
					}
				}
			}
			.onAppear { viewStore.send(.refreshList) }
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case.refreshList:
			self = .refreshList
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case .dismissNewSeries:
			self = .dismissNewSeries
		case let .swipeAction(series, swipeAction):
			self = .swipeAction(series, swipeAction)
		}
	}
}

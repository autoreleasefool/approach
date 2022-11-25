import ComposableArchitecture
import DateTimeLibrary
import SeriesEditorFeature
import SeriesSidebarFeature
import SharedModelsLibrary
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
		let isCreateSeriesFormPresented: Bool
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
			self.isCreateSeriesFormPresented = state.createSeriesForm != nil
			self.isNewSeriesCreated = state.newSeries != nil
			self.isSeriesEditorPresented = state.seriesEditor != nil
		}
	}

	enum ViewAction {
		case subscribeToSeries
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
				Section("All Series") {
					ForEach(series) { series in
						SeriesListRow(
							viewStore: viewStore,
							destination: store.scope(state: \.selection?.value, action: SeriesList.Action.seriesSidebar),
							series: series
						)
					}
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					Theme.Images.EmptyState.series,
					title: "No series found",
					message: "You haven't added a series yet. Create a new series every time you bowl to see your stats mapped accurately over time."
				) {
					EmptyContentAction(title: "Add Series") { viewStore.send(.addButtonTapped) }
				}
			} error: { error in
				ListEmptyContent(
					Theme.Images.Error.notFound,
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
				get: \.isCreateSeriesFormPresented,
				send: ViewAction.dismissNewSeries
			)) {
				IfLetStore(store.scope(state: \.createSeriesForm, action: SeriesList.Action.createSeries)) { scopedStore in
					NavigationView {
						CreateSeriesFormView(store: scopedStore)
					}
					.presentationDetents([.medium])
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
			.task { await viewStore.send(.subscribeToSeries).finish() }
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case.subscribeToSeries:
			self = .subscribeToSeries
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case .addButtonTapped:
			self = .addButtonTapped
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case .dismissNewSeries:
			self = .dismissNewSeries
		case let .swipeAction(series, swipeAction):
			self = .swipeAction(series, swipeAction)
		}
	}
}

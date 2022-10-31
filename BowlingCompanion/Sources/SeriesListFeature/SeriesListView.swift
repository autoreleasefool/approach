import ComposableArchitecture
import DateTimeLibrary
import GamesListFeature
import SeriesEditorFeature
import SharedModelsLibrary
import SwiftUI

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let series: IdentifiedArrayOf<Series>
		let selection: Series.ID?
		let leagueName: String
		let numberOfGames: Int?
		let isCreateSeriesFormPresented: Bool
		let isNewSeriesCreated: Bool
		let isSeriesEditorPresented: Bool

		init(state: SeriesList.State) {
			self.series = state.series
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
		case setNavigation(selection: Series.ID?)
		case setEditorSheet(isPresented: Bool)
		case addSeriesButtonTapped
		case dismissNewSeries
		case swipeAction(Series, SeriesList.SwipeAction)
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesList.Action.init) { viewStore in
			Group {
				List(viewStore.series) { series in
					NavigationLink(
						destination: IfLetStore(
							store.scope(
								state: \.selection?.value,
								action: SeriesList.Action.games
							)
						) {
							GamesListView(store: $0)
						},
						tag: series.id,
						selection: viewStore.binding(
							get: \.selection,
							send: SeriesListView.ViewAction.setNavigation(selection:)
						)
					) {
						Text(series.date.regularDateFormat)
							.swipeActions(allowsFullSwipe: true) {
								Button {
									viewStore.send(.swipeAction(series, .edit))
								} label: {
									Label("Edit", systemImage: "pencil")
								}
								.tint(.blue)

								Button(role: .destructive) {
									viewStore.send(.swipeAction(series, .delete))
								} label: {
									Label("Delete", systemImage: "trash")
								}
							}
					}
				}
			}
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					if let numberOfGames = viewStore.numberOfGames {
						NavigationLink(
							destination: IfLetStore(
								store.scope(
									state: \.newSeries,
									action: SeriesList.Action.games
								)
							) {
								GamesListView(store: $0)
							},
							isActive: viewStore.binding(
								get: \.isNewSeriesCreated,
								send: { $0 ? .addSeriesButtonTapped : .dismissNewSeries }
							)
						) {
							Image(systemName: "plus")
						}
					} else {
						Button {
							viewStore.send(.addSeriesButtonTapped)
						} label: {
							Image(systemName: "plus")
						}
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
				send: ViewAction.setEditorSheet(isPresented: false)
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
		case let .setEditorSheet(isPresented):
			self = .setEditorSheet(isPresented: isPresented)
		case .addSeriesButtonTapped:
			self = .addSeriesButtonTapped
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case .dismissNewSeries:
			self = .dismissNewSeries
		case let .swipeAction(series, swipeAction):
			self = .swipeAction(series, swipeAction)
		}
	}
}

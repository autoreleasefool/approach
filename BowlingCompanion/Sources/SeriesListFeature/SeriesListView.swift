import ComposableArchitecture
import DateTimeLibrary
import GamesListFeature
import SharedModelsLibrary
import SwiftUI

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let series: IdentifiedArrayOf<Series>
		let selection: Series.ID?
		let leagueName: String
		let isNewSeriesCreated: Bool

		init(state: SeriesList.State) {
			self.series = state.series
			self.leagueName = state.league.name
			self.selection = state.selection?.id
			self.isNewSeriesCreated = state.newSeries != nil
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
		case setNavigation(selection: Series.ID?)
		case setFormSheet(isPresented: Bool)
		case addSeriesButtonTapped
		case dismissNewSeries
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
					}
				}
			}
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
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
				}
			}
			.onAppear { viewStore.send(.onAppear) }
			.onDisappear { viewStore.send(.onDisappear) }
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case .onAppear:
			self = .onAppear
		case .onDisappear:
			self = .onDisappear
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case .addSeriesButtonTapped:
			self = .addSeriesButtonTapped
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case .dismissNewSeries:
			self = .dismissNewSeries
		}
	}
}

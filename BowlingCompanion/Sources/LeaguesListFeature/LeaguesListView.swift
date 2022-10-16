import ComposableArchitecture
import LeagueFormFeature
import SeriesListFeature
import SharedModelsLibrary
import SwiftUI

public struct LeaguesListView: View {
	let store: StoreOf<LeaguesList>

	struct ViewState: Equatable {
		let bowlerName: String
		let leagues: IdentifiedArrayOf<League>
		let selection: Identified<League.ID, SeriesList.State>?
		let isLeagueFormPresented: Bool

		init(state: LeaguesList.State) {
			self.leagues = state.leagues
			self.selection = state.selection
			self.bowlerName = state.bowler.name
			self.isLeagueFormPresented = state.leagueForm != nil
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
		case setFormSheet(isPresented: Bool)
		case setNavigation(selection: League.ID?)
	}

	public init(store: StoreOf<LeaguesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeaguesList.Action.init) { viewStore in
			List(viewStore.leagues) { league in
				NavigationLink(
					destination: IfLetStore(
						store.scope(
							state: \.selection?.value,
							action: LeaguesList.Action.series
						)
					) {
						SeriesListView(store: $0)
					},
					tag: league.id,
					selection: viewStore.binding(
						get: \.selection?.id,
						send: LeaguesListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(league.name)
				}
			}
			.navigationTitle(viewStore.bowlerName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button {
						viewStore.send(.setFormSheet(isPresented: true))
					} label: {
						Image(systemName: "plus")
					}
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isLeagueFormPresented,
				send: ViewAction.setFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.leagueForm, action: LeaguesList.Action.leagueForm)) { scopedStore in
					NavigationView {
						LeagueFormView(store: scopedStore)
					}
				}
			}
			.onAppear { viewStore.send(.onAppear) }
			.onDisappear { viewStore.send(.onDisappear) }
		}
	}
}

extension LeaguesList.Action {
	init(action: LeaguesListView.ViewAction) {
		switch action {
		case .onAppear:
			self = .onAppear
		case .onDisappear:
			self = .onDisappear
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		}
	}
}

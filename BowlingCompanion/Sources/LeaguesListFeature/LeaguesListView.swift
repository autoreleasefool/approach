import ComposableArchitecture
import LeagueEditorFeature
import SeriesListFeature
import SharedModelsLibrary
import SwiftUI

public struct LeaguesListView: View {
	let store: StoreOf<LeaguesList>

	struct ViewState: Equatable {
		let bowlerName: String
		let leagues: IdentifiedArrayOf<League>
		let selection: League.ID?
		let isLeagueEditorPresented: Bool

		init(state: LeaguesList.State) {
			self.leagues = state.leagues
			self.selection = state.selection?.id
			self.bowlerName = state.bowler.name
			self.isLeagueEditorPresented = state.leagueEditor != nil
		}
	}

	enum ViewAction {
		case subscribeToLeagues
		case setFormSheet(isPresented: Bool)
		case setNavigation(selection: League.ID?)
		case swipeAction(League, LeaguesList.SwipeAction)
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
						get: \.selection,
						send: LeaguesListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(league.name)
						.swipeActions(allowsFullSwipe: true) {
							Button {
								viewStore.send(.swipeAction(league, .edit))
							} label: {
								Label("Edit", systemImage: "pencil")
							}
							.tint(.blue)

							Button(role: .destructive) {
								viewStore.send(.swipeAction(league, .delete))
							} label: {
								Label("Delete", systemImage: "trash")
							}
						}
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
				get: \.isLeagueEditorPresented,
				send: ViewAction.setFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.leagueEditor, action: LeaguesList.Action.leagueEditor)) { scopedStore in
					NavigationView {
						LeagueEditorView(store: scopedStore)
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: LeaguesList.Action.alert),
				dismiss: .dismissed
			)
			.task { await viewStore.send(.subscribeToLeagues).finish() }
		}
	}
}

extension LeaguesList.Action {
	init(action: LeaguesListView.ViewAction) {
		switch action {
		case .subscribeToLeagues:
			self = .subscribeToLeagues
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(league, swipeAction):
			self = .swipeAction(league, swipeAction)
		}
	}
}

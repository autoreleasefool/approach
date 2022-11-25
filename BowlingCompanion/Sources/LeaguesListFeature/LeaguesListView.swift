import ComposableArchitecture
import LeagueEditorFeature
import SeriesListFeature
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct LeaguesListView: View {
	let store: StoreOf<LeaguesList>

	struct ViewState: Equatable {
		let bowlerName: String
		let listState: ListContentState<League, ListErrorContent>
		let selection: League.ID?
		let isLeagueEditorPresented: Bool

		init(state: LeaguesList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let leagues = state.leagues {
				self.listState = .loaded(leagues)
			} else {
				self.listState = .loading
			}
			self.selection = state.selection?.id
			self.bowlerName = state.bowler.name
			self.isLeagueEditorPresented = state.leagueEditor != nil
		}
	}

	enum ViewAction {
		case subscribeToLeagues
		case addButtonTapped
		case errorButtonTapped
		case setEditorFormSheet(isPresented: Bool)
		case setNavigation(selection: League.ID?)
		case swipeAction(League, LeaguesList.SwipeAction)
	}

	public init(store: StoreOf<LeaguesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeaguesList.Action.init) { viewStore in
			ListContent(viewStore.listState) { leagues in
				Section("All Leagues") {
					ForEach(leagues) { league in
						LeaguesListRow(
							viewStore: viewStore,
							destination: store.scope(state: \.selection?.value, action: LeaguesList.Action.series),
							league: league
						)
					}
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					Theme.Images.EmptyState.leagues,
					title: "No leagues found",
					message: "You haven't added any leagues or events yet. Track your progress week over week for each league you're in. See how you measure up in tournaments with events."
				) {
					EmptyContentAction(title: "Add League") { viewStore.send(.addButtonTapped) }
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
			.navigationTitle(viewStore.bowlerName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.addButtonTapped) }
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isLeagueEditorPresented,
				send: ViewAction.setEditorFormSheet(isPresented:)
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
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(league, swipeAction):
			self = .swipeAction(league, swipeAction)
		}
	}
}

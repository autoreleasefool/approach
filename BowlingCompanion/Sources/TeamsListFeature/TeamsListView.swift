import AssetsLibrary
import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct TeamsListView: View {
	let store: StoreOf<TeamsList>

	struct ViewState: Equatable {
		let listState: ListContentState<Team, ListErrorContent>

		init(state: TeamsList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let teams = state.teams {
				self.listState = .loaded(teams)
			} else {
				self.listState = .loading
			}
		}
	}

	enum ViewAction {
		case observeTeams
		case addButtonTapped
		case errorButtonTapped
		case setEditorFormSheet(isPresented: Bool)
		case setNavigation(selection: Team.ID?)
		case swipeAction(Team, TeamsList.SwipeAction)
	}

	public init(store: StoreOf<TeamsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TeamsList.Action.init) { viewStore in
			ListContent(viewStore.listState) { teams in
				Section(Strings.Team.List.Title.all) {
					ForEach(teams) { team in
						TeamRow(
							team: team,
							onEdit: { viewStore.send(.swipeAction(team, .edit)) },
							onDelete: { viewStore.send(.swipeAction(team, .delete)) }
						)
					}
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					.emptyTeams,
					title: Strings.Team.Error.Empty.title,
					message: Strings.Team.Error.Empty.message
				) {
					EmptyContentAction(title: Strings.Team.List.add) { viewStore.send(.addButtonTapped) }
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
			.navigationTitle(Strings.Team.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: TeamsList.Action.sortOrder))
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.addButtonTapped) }
				}
			}
			.task { await viewStore.send(.observeTeams).finish() }
		}
	}
}

extension TeamsList.Action {
	init(action: TeamsListView.ViewAction) {
		switch action {
		case .observeTeams:
			self = .observeTeams
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(team, swipeAction):
			self = .swipeAction(team, swipeAction)
		}
	}
}

import AssetsLibrary
import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct OpponentsListView: View {
	let store: StoreOf<OpponentsList>

	struct ViewState: Equatable {
		let listState: ListContentState<Opponent, ListErrorContent>
		let selection: Opponent.ID?

		init(state: OpponentsList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let opponents = state.opponents {
				self.listState = .loaded(opponents)
			} else {
				self.listState = .loading
			}
			self.selection = state.selection?.id
		}
	}

	enum ViewAction {
		case observeOpponents
		case addButtonTapped
		case errorButtonTapped
		case setEditorFormSheet(isPresented: Bool)
		case setNavigation(selection: Opponent.ID?)
		case swipeAction(Opponent, OpponentsList.SwipeAction)
	}

	public init(store: StoreOf<OpponentsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: OpponentsList.Action.init) { viewStore in
			ListContent(viewStore.listState) { opponents in
				ForEach(opponents) { opponent in
					NavigationLink(
						destination: EmptyView(),
						tag: opponent.id,
						selection: viewStore.binding(
							get: \.selection,
							send: OpponentsListView.ViewAction.setNavigation(selection:)
						)
					) {
						OpponentRow(
							opponent: opponent,
							onEdit: { viewStore.send(.swipeAction(opponent, .edit)) },
							onDelete: { viewStore.send(.swipeAction(opponent, .delete)) }
						)
					}
				}
			} empty: {
				ListEmptyContent(
					.emptyOpponents,
					title: Strings.Opponent.Error.Empty.title,
					message: Strings.Opponent.Error.Empty.message
				) {
					EmptyContentAction(title: Strings.Opponent.List.add) { viewStore.send(.addButtonTapped) }
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
			.navigationTitle(Strings.Opponent.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: OpponentsList.Action.sortOrder))
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.addButtonTapped) }
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: OpponentsList.Action.alert),
				dismiss: .dismissed
			)
			.task { await viewStore.send(.observeOpponents).finish() }
		}
	}
}

extension OpponentsList.Action {
	init(action: OpponentsListView.ViewAction) {
		switch action {
		case .observeOpponents:
			self = .observeOpponents
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(bowler, swipeAction):
			self = .swipeAction(bowler, swipeAction)
		}
	}
}

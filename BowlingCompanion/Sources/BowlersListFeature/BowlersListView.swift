import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import StatisticsWidgetsFeature
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let listState: ListContentState<Bowler, BowlersList.ErrorContent>
		let selection: Bowler.ID?
		let isBowlerEditorPresented: Bool

		init(state: BowlersList.State) {
			if let error = state.bowlerError {
				self.listState = .error(error)
			} else if let bowlers = state.bowlers {
				self.listState = .loaded(bowlers)
			} else {
				self.listState = .loading
			}
			self.selection = state.selection?.id
			self.isBowlerEditorPresented = state.bowlerEditor != nil
		}
	}

	enum ViewAction {
		case subscribeToBowlers
		case addBowlerButtonTapped
		case errorButtonTapped
		case setFormSheet(isPresented: Bool)
		case setNavigation(selection: Bowler.ID?)
		case swipeAction(Bowler, BowlersList.SwipeAction)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			ListContent(viewStore.listState) { bowlers in
				Button { viewStore.send(.addBowlerButtonTapped) } label: {
					PlaceholderWidget(size: .medium)
				}
				.buttonStyle(TappableElement())
				.listRowSeparator(.hidden)

				ForEach(bowlers) { bowler in
					Section("All Bowlers") {
						BowlersListRow(
							viewStore: viewStore,
							destination: store.scope(state: \.selection?.value, action: BowlersList.Action.leagues),
							bowler: bowler
						)
					}
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					Theme.Images.EmptyState.bowlers,
					title: "No bowlers found",
					message: "You haven't added any bowlers yet. Try adding yourself to get started."
				) {
					EmptyContentAction(title: "Add Bowler") { viewStore.send(.addBowlerButtonTapped) }
				}
			} error: { error in
				ListEmptyContent(
					Theme.Images.Error.notFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) { viewStore.send(.addBowlerButtonTapped) }
				}
			}
			.scrollContentBackground(.hidden)
			.navigationTitle("Bowlers")
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button {
						viewStore.send(.addBowlerButtonTapped)
					} label: {
						Image(systemName: "plus")
					}
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isBowlerEditorPresented,
				send: ViewAction.setFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.bowlerEditor, action: BowlersList.Action.bowlerEditor)) { scopedStore in
					NavigationView {
						BowlerEditorView(store: scopedStore)
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: BowlersList.Action.alert),
				dismiss: .dismissed
			)
			.task { await viewStore.send(.subscribeToBowlers).finish() }
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .subscribeToBowlers:
			self = .subscribeToBowlers
		case .addBowlerButtonTapped:
			self = .setFormSheet(isPresented: true)
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(bowler, swipeAction):
			self = .swipeAction(bowler, swipeAction)
		}
	}
}

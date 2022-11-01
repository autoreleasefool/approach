import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import SwiftUI

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let bowlers: IdentifiedArrayOf<Bowler>
		let selection: Bowler.ID?
		let isBowlerEditorPresented: Bool

		init(state: BowlersList.State) {
			self.bowlers = state.bowlers
			self.selection = state.selection?.id
			self.isBowlerEditorPresented = state.bowlerEditor != nil
		}
	}

	enum ViewAction {
		case subscribeToBowlers
		case setFormSheet(isPresented: Bool)
		case setNavigation(selection: Bowler.ID?)
		case swipeAction(Bowler, BowlersList.SwipeAction)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			List(viewStore.bowlers) { bowler in
				NavigationLink(
					destination: IfLetStore(
						store.scope(
							state: \.selection?.value,
							action: BowlersList.Action.leagues
						)
					) {
						LeaguesListView(store: $0)
					},
					tag: bowler.id,
					selection: viewStore.binding(
						get: \.selection,
						send: BowlersListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(bowler.name)
						.swipeActions(allowsFullSwipe: true) {
							Button {
								viewStore.send(.swipeAction(bowler, .edit))
							} label: {
								Label("Edit", systemImage: "pencil")
							}
							.tint(.blue)

							Button(role: .destructive) {
								viewStore.send(.swipeAction(bowler, .delete))
							} label: {
								Label("Delete", systemImage: "trash")
							}
						}
				}
			}
			.navigationTitle("Bowlers")
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
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(bowler, swipeAction):
			self = .swipeAction(bowler, swipeAction)
		}
	}
}

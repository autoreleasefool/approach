import BowlerFormFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import SwiftUI

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let bowlers: IdentifiedArrayOf<Bowler>
		let selection: Bowler.ID?
		let isBowlerFormPresented: Bool

		init(state: BowlersList.State) {
			self.bowlers = state.bowlers
			self.selection = state.selection?.id
			self.isBowlerFormPresented = state.bowlerForm != nil
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
		case setFormSheet(isPresented: Bool)
		case setNavigation(selection: Bowler.ID?)
		case delete(Bowler)
		case edit(Bowler)
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
								viewStore.send(.edit(bowler))
							} label: {
								Label("Edit", systemImage: "pencil")
							}
							.tint(.blue)

							Button(role: .destructive) {
								viewStore.send(.delete(bowler))
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
				get: \.isBowlerFormPresented,
				send: ViewAction.setFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.bowlerForm, action: BowlersList.Action.bowlerForm)) { scopedStore in
					NavigationView {
						BowlerFormView(store: scopedStore)
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: BowlersList.Action.alert),
				dismiss: .dismissed
			)
			.onAppear { viewStore.send(.onAppear) }
			.onDisappear { viewStore.send(.onDisappear) }
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .onAppear:
			self = .onAppear
		case .onDisappear:
			self = .onDisappear
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .edit(bowler):
			self = .edit(bowler)
		case let .delete(bowler):
			self = .delete(bowler)
		}
	}
}

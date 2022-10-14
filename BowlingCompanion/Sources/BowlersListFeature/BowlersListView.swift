import BowlerFormFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import SwiftUI

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let bowlers: IdentifiedArrayOf<Bowler>
		let selection: Identified<Bowler.ID, LeaguesList.State>?
		let isSheetPresented: Bool

		init(state: BowlersList.State) {
			self.bowlers = state.bowlers
			self.selection = state.selection
			self.isSheetPresented = state.bowlerForm != nil
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
		case setFormSheet(isPresented: Bool)
		case setNavigation(selection: Bowler.ID?)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			List(viewStore.bowlers) { bowler in
				NavigationLink(
					destination: IfLetStore(
						self.store.scope(
							state: \.selection?.value,
							action: BowlersList.Action.leagues
						)
					) {
						LeaguesListView(store: $0)
					},
					tag: bowler.id,
					selection: viewStore.binding(
						get: \.selection?.id,
						send: BowlersListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(bowler.name)
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
				get: \.isSheetPresented,
				send: ViewAction.setFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.bowlerForm, action: BowlersList.Action.bowlerForm)) { scopedStore in
					NavigationView {
						BowlerFormView(store: scopedStore)
					}
				}
			}
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
		}
	}
}

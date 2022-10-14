import BowlerFormFeature
import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let bowlers: IdentifiedArrayOf<Bowler>
		let isSheetPresented: Bool

		init(state: BowlersList.State) {
			self.bowlers = state.bowlers
			self.isSheetPresented = state.bowlerForm != nil
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
		case setFormSheet(isPresented: Bool)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			List(viewStore.bowlers) { bowler in
				Text(bowler.name)
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
					NavigationStack {
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
		}
	}
}

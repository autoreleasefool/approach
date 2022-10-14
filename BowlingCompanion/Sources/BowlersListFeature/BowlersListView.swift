import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let bowlers: IdentifiedArrayOf<Bowler>

		init(state: BowlersList.State) {
			self.bowlers = state.bowlers
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
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
		}
	}
}

import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesView: View {
	let store: StoreOf<AlleyLanes>

	struct ViewState: Equatable {
		let lanes: IdentifiedArrayOf<Lane>?

		init(state: AlleyLanes.State) {
			self.lanes = state.lanes
		}
	}

	enum ViewAction {
		case didAppear
	}

	public init(store: StoreOf<AlleyLanes>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyLanes.Action.init) { viewStore in
			if let lanes = viewStore.lanes {
				if lanes.isEmpty {
					Text(Strings.Alley.Properties.Lanes.none)
						.listRowBackground(Color(uiColor: .secondarySystemBackground))
				} else {
					ForEach(lanes) { lane in
						LaneRow(lane: lane)
					}
				}
			} else {
				ProgressView()
					.onAppear { viewStore.send(.didAppear) }
			}
		}
	}
}

extension AlleyLanes.Action {
	init(action: AlleyLanesView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		}
	}
}

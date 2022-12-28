import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesView: View {
	let store: StoreOf<AlleyLanes>

	struct ViewState: Equatable {
		let isLoadingInitialData: Bool
		let lanes: IdentifiedArrayOf<Lane>

		init(state: AlleyLanes.State) {
			self.isLoadingInitialData = state.isLoadingInitialData
			self.lanes = state.lanes
		}
	}

	enum ViewAction {
		case onAppear
	}

	public init(store: StoreOf<AlleyLanes>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyLanes.Action.init) { viewStore in
			if viewStore.isLoadingInitialData {
				ProgressView()
					.onAppear { viewStore.send(.onAppear) }
			} else {
				if viewStore.lanes.isEmpty {
					Text(Strings.Alley.Properties.Lanes.none)
						.listRowBackground(Color(uiColor: .secondarySystemBackground))
				} else {
					ForEach(viewStore.lanes) { lane in
						LaneRow(lane: lane)
					}
				}
			}
		}
	}
}

extension AlleyLanes.Action {
	init(action: AlleyLanesView.ViewAction) {
		switch action {
		case .onAppear:
			self = .refreshData
		}
	}
}

import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct TeamMembersView: View {
	let store: StoreOf<TeamMembers>

	struct ViewState: Equatable {
		let isLoadingInitialData: Bool
		let bowlers: IdentifiedArrayOf<Bowler>

		init(state: TeamMembers.State) {
			self.isLoadingInitialData = state.isLoadingInitialData
			self.bowlers = state.bowlers
		}
	}

	enum ViewAction {
		case onAppear
	}

	public init(store: StoreOf<TeamMembers>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TeamMembers.Action.init) { viewStore in
			if viewStore.isLoadingInitialData {
				ProgressView()
					.onAppear { viewStore.send(.onAppear) }
			} else {
				if viewStore.bowlers.isEmpty {
					Text(Strings.Team.Properties.Bowlers.none)
						.listRowBackground(Color(uiColor: .secondarySystemBackground))
				} else {
					ForEach(viewStore.bowlers) { bowler in
						BowlerRow(bowler: bowler)
					}
				}
			}
		}
	}
}

extension TeamMembers.Action {
	init(action: TeamMembersView.ViewAction) {
		switch action {
		case .onAppear:
			self = .refreshData
		}
	}
}

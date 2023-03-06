import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct TeamMembersView: View {
	let store: StoreOf<TeamMembers>

	struct ViewState: Equatable {
		let bowlers: IdentifiedArrayOf<Bowler>?

		init(state: TeamMembers.State) {
			self.bowlers = state.bowlers
		}
	}

	enum ViewAction {
		case didAppear
	}

	public init(store: StoreOf<TeamMembers>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TeamMembers.Action.init) { viewStore in
			if let bowlers = viewStore.bowlers {
				if bowlers.isEmpty {
					Text(Strings.Team.Properties.Bowlers.none)
						.listRowBackground(Color(uiColor: .secondarySystemBackground))
				} else {
					ForEach(bowlers) { bowler in
						BowlerRow(bowler: bowler)
					}
				}
			} else {
				ProgressView()
					.onAppear { viewStore.send(.didAppear) }
			}
		}
	}
}

extension TeamMembers.Action {
	init(action: TeamMembersView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		}
	}
}

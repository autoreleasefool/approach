import BowlersListFeature
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import TeamsListFeature

public struct TeamsAndBowlersListView: View {
	let store: StoreOf<TeamsAndBowlersList>

	struct ViewState: Equatable {
		let selectedTab: TeamsAndBowlersList.Tab

		init(state: TeamsAndBowlersList.State) {
			self.selectedTab = state.selectedTab
		}
	}

	enum ViewAction {
		case tabPicked(tab: TeamsAndBowlersList.Tab)
	}

	public init(store: StoreOf<TeamsAndBowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TeamsAndBowlersList.Action.init) { viewStore in
			VStack(alignment: .leading) {
				Picker(
					Strings.tab,
					selection: viewStore.binding(
						get: \.selectedTab,
						send: ViewAction.tabPicked(tab:)
					)
				) {
					ForEach(TeamsAndBowlersList.Tab.allCases, id: \.self) { tab in
						Text(String(describing: tab))
							.tag(tab)
					}
				}
				.pickerStyle(.segmented)
				.padding(.horizontal)

				IfLetStore(store.scope(state: \.bowlersListSelected, action: TeamsAndBowlersList.Action.bowlersList)) {
					BowlersListView(store: $0)
				}

				IfLetStore(store.scope(state: \.teamsListSelected, action: TeamsAndBowlersList.Action.teamsList)) {
					TeamsListView(store: $0)
				}
			}
		}
	}
}

extension TeamsAndBowlersList.Action {
	init(action: TeamsAndBowlersListView.ViewAction) {
		switch action {
		case let .tabPicked(tab):
			self = .tabPicked(tab: tab)
		}
	}
}

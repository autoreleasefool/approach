import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import TeamEditorFeature
import ViewsLibrary

public struct TeamsListView: View {
	let store: StoreOf<TeamsList>

	struct ViewState: Equatable {
		let isEditorPresented: Bool

		init(state: TeamsList.State) {
			self.isEditorPresented = state.editor != nil
		}
	}

	enum ViewAction {
		case setEditorSheet(isPresented: Bool)
		case setNavigation(selection: Team.ID?)
	}

	public init(store: StoreOf<TeamsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TeamsList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /TeamsList.Action.InternalAction.list)
			) { team in
				TeamRow(team: team)
			}
			.navigationTitle(Strings.Team.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /TeamsList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isEditorPresented,
				send: ViewAction.setEditorSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.editor, action: /TeamsList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						TeamEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension TeamsList.Action {
	init(action: TeamsListView.ViewAction) {
		switch action {
		case let .setEditorSheet(isPresented):
			self = .view(.setEditorSheet(isPresented: isPresented))
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}

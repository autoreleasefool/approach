import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SortOrderLibrary
import StatisticsWidgetsFeature
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let selection: Bowler.ID?
		let isBowlerEditorPresented: Bool

		init(state: BowlersList.State) {
			self.selection = state.selection?.id
			self.isBowlerEditorPresented = state.bowlerEditor != nil
		}
	}

	enum ViewAction {
		case configureStatisticsButtonTapped
		case setEditorFormSheet(isPresented: Bool)
		case setNavigation(selection: Bowler.ID?)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /BowlersList.Action.InternalAction.list)
			) { bowler in
				NavigationLink(
					destination: IfLetStore(
						store.scope(state: \.selection?.value, action: /BowlersList.Action.InternalAction.leagues)
					) {
						LeaguesListView(store: $0)
					},
					tag: bowler.id,
					selection: viewStore.binding(
						get: \.selection,
						send: BowlersListView.ViewAction.setNavigation(selection:)
					)
				) {
					BowlerRow(bowler: bowler)
				}

			} header: {
				Section {
					Button { viewStore.send(.configureStatisticsButtonTapped) } label: {
						PlaceholderWidget(size: .medium)
					}
					.buttonStyle(TappableElement())
				}
				.listRowSeparator(.hidden)
				.listRowInsets(EdgeInsets())
			}
			.navigationTitle(Strings.Bowler.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /BowlersList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isBowlerEditorPresented,
				send: ViewAction.setEditorFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.bowlerEditor, action: /BowlersList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						BowlerEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .configureStatisticsButtonTapped:
			self = .view(.didTapConfigureStatisticsButton)
		case let .setEditorFormSheet(isPresented):
			self = .view(.setEditorFormSheet(isPresented: isPresented))
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}

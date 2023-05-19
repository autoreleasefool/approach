import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import ModelsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StatisticsWidgetsFeature
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let selection: Bowler.ID?

		init(state: BowlersList.State) {
			self.selection = state.selection?.id
		}
	}

	enum ViewAction {
		case didTapConfigureStatisticsButton
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
					HStack {
						Text(bowler.name)
						Spacer()
						Text(format(average: bowler.average))
							.font(.caption)
					}
				}
			} header: {
				Section {
					Button { viewStore.send(.didTapConfigureStatisticsButton) } label: {
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
			.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { scopedStore in
				NavigationView {
					BowlerEditorView(store: scopedStore)
				}
			}
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .didTapConfigureStatisticsButton:
			self = .view(.didTapConfigureStatisticsButton)
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}

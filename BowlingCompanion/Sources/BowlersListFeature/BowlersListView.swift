import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StatisticsWidgetsFeature
import StringsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let listState: ListContentState<Bowler, ListErrorContent>
		let selection: Bowler.ID?
		let isBowlerEditorPresented: Bool

		init(state: BowlersList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let bowlers = state.bowlers {
				self.listState = .loaded(bowlers)
			} else {
				self.listState = .loading
			}
			self.selection = state.selection?.id
			self.isBowlerEditorPresented = state.bowlerEditor != nil
		}
	}

	enum ViewAction {
		case refreshList
		case addButtonTapped
		case errorButtonTapped
		case configureStatisticsButtonTapped
		case setEditorFormSheet(isPresented: Bool)
		case setNavigation(selection: Bowler.ID?)
		case swipeAction(Bowler, BowlersList.SwipeAction)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			ListContent(viewStore.listState) { bowlers in
				Section {
					Button { viewStore.send(.configureStatisticsButtonTapped) } label: {
						PlaceholderWidget(size: .medium)
					}
					.buttonStyle(TappableElement())
				}
				.listRowSeparator(.hidden)
				.listRowInsets(EdgeInsets())

				Section(Strings.Bowlers.List.sectionTitle) {
					ForEach(bowlers) { bowler in
						NavigationLink(
							destination: IfLetStore(store.scope(state: \.selection?.value, action: BowlersList.Action.leagues)) {
								LeaguesListView(store: $0)
							},
							tag: bowler.id,
							selection: viewStore.binding(
								get: \.selection,
								send: BowlersListView.ViewAction.setNavigation(selection:)
							)
						) {
							BowlerRow(
								bowler: bowler,
								onEdit: { viewStore.send(.swipeAction(bowler, .edit)) },
								onDelete: { viewStore.send(.swipeAction(bowler, .delete)) }
							)
						}
					}
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					.emptyBowlers,
					title: Strings.Bowlers.Errors.Empty.title,
					message: Strings.Bowlers.Errors.Empty.message
				) {
					EmptyContentAction(title: Strings.Bowlers.List.add) { viewStore.send(.addButtonTapped) }
				}
			} error: { error in
				ListEmptyContent(
					.errorNotFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) { viewStore.send(.errorButtonTapped) }
				}
			}
			.scrollContentBackground(.hidden)
			.navigationTitle(Strings.Bowlers.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.addButtonTapped) }
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isBowlerEditorPresented,
				send: ViewAction.setEditorFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.bowlerEditor, action: BowlersList.Action.bowlerEditor)) { scopedStore in
					NavigationView {
						BowlerEditorView(store: scopedStore)
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: BowlersList.Action.alert),
				dismiss: .dismissed
			)
			.onAppear { viewStore.send(.refreshList) }
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .refreshList:
			self = .refreshList
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case .errorButtonTapped:
			self = .errorButtonTapped
		case .configureStatisticsButtonTapped:
			self = .configureStatisticsButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case let .setNavigation(selection):
			self = .setNavigation(selection: selection)
		case let .swipeAction(bowler, swipeAction):
			self = .swipeAction(bowler, swipeAction)
		}
	}
}

import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import ErrorsFeature
import GamesListFeature
import LeaguesListFeature
import ModelsLibrary
import QuickLaunchRepositoryInterface
import ResourceListLibrary
import SeriesEditorFeature
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import TipsLibrary
import ToastLibrary
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		let ordering: Bowler.Ordering
		let isShowingWidgets: Bool
		let quickLaunch: QuickLaunchSource?
		let isShowingQuickLaunchTip: Bool

		init(state: BowlersList.State) {
			self.ordering = state.ordering
			self.isShowingWidgets = state.isShowingWidgets
			self.quickLaunch = state.isQuickLaunchEnabled ? state.quickLaunch : nil
			self.isShowingQuickLaunchTip = state.isShowingQuickLaunchTip
		}
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /BowlersList.Action.InternalAction.list)
			) { bowler in
				Button { viewStore.send(.didTapBowler(bowler.id)) } label: {
					LabeledContent(bowler.name, value: format(average: bowler.average))
				}
				.buttonStyle(.navigation)
			} header: {
				quickLaunch(viewStore)
				widgets(viewStore)
			}
			.navigationTitle(Strings.Bowler.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.task { viewStore.send(.didStartTask) }
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.bowlerEditor(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.sortOrder(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.seriesEditor(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.leaguesList(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.gamesList(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
	}

	@ViewBuilder private func quickLaunch(
		_ viewStore: ViewStore<BowlersListView.ViewState, BowlersList.Action.ViewAction>
	) -> some View {
		if let quickLaunch = viewStore.quickLaunch {
			Section {
				Button { viewStore.send(.didTapQuickLaunchButton) } label: {
					HStack(spacing: .standardSpacing) {
						Asset.Media.Icons.rocket.swiftUIImage
							.resizable()
							.scaledToFit()
							.frame(width: .smallIcon, height: .smallIcon)
							.foregroundColor(Asset.Colors.Text.onAction)

						VStack(alignment: .leading) {
							Text(quickLaunch.bowler.name)
								.font(.headline)
								.frame(maxWidth: .infinity, alignment: .leading)
							Text(quickLaunch.league.name)
								.font(.subheadline)
								.frame(maxWidth: .infinity, alignment: .leading)
						}
						.frame(maxWidth: .infinity)
					}
					.contentShape(Rectangle())
				}
				.modifier(PrimaryButton())
			}
			.listRowInsets(EdgeInsets())
			.compactList()

			if viewStore.isShowingQuickLaunchTip {
				Section {
					BasicTipView(
						tip: .quickLaunchTip,
						isDismissable: false,
						onDismiss: {}
					)
				}
				.compactList()
			}
		}
	}

	@ViewBuilder private func widgets(
		_ viewStore: ViewStore<BowlersListView.ViewState, BowlersList.Action.ViewAction>
	) -> some View {
		if viewStore.isShowingWidgets {
			Section {
				StatisticsWidgetLayoutView(store: store.scope(state: \.widgets, action: { .internal(.widgets($0)) }))
			}
			.listRowSeparator(.hidden)
			.listRowInsets(EdgeInsets())
			.listRowBackground(Color.clear)
			.compactList()
		}
	}
}

@MainActor extension View {
	fileprivate typealias State = PresentationState<BowlersList.Destination.State>
	fileprivate typealias Action = PresentationAction<BowlersList.Destination.Action>

	fileprivate func bowlerEditor(_ store: Store<State, Action>) -> some View {
		sheet(
			store: store,
			state: /BowlersList.Destination.State.editor,
			action: BowlersList.Destination.Action.editor
		) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: Store<State, Action>) -> some View {
		sheet(
			store: store,
			state: /BowlersList.Destination.State.sortOrder,
			action: BowlersList.Destination.Action.sortOrder
		) { (store: StoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func seriesEditor(_ store: Store<State, Action>) -> some View {
		sheet(
			store: store,
			state: /BowlersList.Destination.State.seriesEditor,
			action: BowlersList.Destination.Action.seriesEditor
		) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func leaguesList(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /BowlersList.Destination.State.leagues,
			action: BowlersList.Destination.Action.leagues
		) { (store: StoreOf<LeaguesList>) in
			LeaguesListView(store: store)
		}
	}

	fileprivate func gamesList(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /BowlersList.Destination.State.games,
			action: BowlersList.Destination.Action.games
		) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}

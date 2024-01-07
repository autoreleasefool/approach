import AnnouncementsFeature
import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import ErrorsFeature
import ExtensionsLibrary
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
			self.quickLaunch = state.quickLaunch
			self.isShowingQuickLaunchTip = state.isShowingQuickLaunchTip
		}
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
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
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.announcements(store: store.scope(state: \.announcements, action: \.internal.announcements))
		.bowlerEditor(store.scope(state: \.$destination.editor, action: \.internal.destination.editor))
		.sortOrder(store.scope(state: \.$destination.sortOrder, action: \.internal.destination.sortOrder))
		.seriesEditor(store.scope(state: \.$destination.seriesEditor, action: \.internal.destination.seriesEditor))
		.leaguesList(store.scope(state: \.$destination.leagues, action: \.internal.destination.leagues))
		.gamesList(store.scope(state: \.$destination.games, action: \.internal.destination.games))
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
				StatisticsWidgetLayoutView(store: store.scope(state: \.widgets, action: \.internal.widgets))
			}
			.listRowSeparator(.hidden)
			.listRowInsets(EdgeInsets())
			.listRowBackground(Color.clear)
			.compactList()
		}
	}
}

@MainActor extension View {
	fileprivate func bowlerEditor(_ store: PresentationStoreOf<BowlerEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: PresentationStoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>) -> some View {
		sheet(store: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func seriesEditor(_ store: PresentationStoreOf<SeriesEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func leaguesList(_ store: PresentationStoreOf<LeaguesList>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<LeaguesList>) in
			LeaguesListView(store: store)
		}
	}

	fileprivate func gamesList(_ store: PresentationStoreOf<GamesList>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}

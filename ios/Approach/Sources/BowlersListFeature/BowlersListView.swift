import AnnouncementsFeature
import AssetsLibrary
import BowlerDetailsFeature
import BowlerEditorFeature
import ComposableArchitecture
import ErrorsFeature
import ExtensionsPackageLibrary
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
import SwiftUIExtensionsPackageLibrary
import TipsLibrary
import ViewsLibrary

@ViewAction(for: BowlersList.self)
public struct BowlersListView: View {
	@Bindable public var store: StoreOf<BowlersList>

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		ResourceListView(
			store: store.scope(state: \.list, action: \.internal.list)
		) { bowler in
			Button { send(.didTapBowler(bowler.id)) } label: {
				LabeledContent(bowler.name, value: format(average: bowler.average))
			}
			.buttonStyle(.navigation)
		} header: {
			quickLaunch
			widgets
		}
		.navigationTitle(Strings.Bowler.List.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) { send(.didTapSortOrderButton) }
			}
		}
		.task { await send(.didStartTask).finish() }
		.onAppear { send(.onAppear) }
		.onFirstAppear { send(.didFirstAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.announcements(store: store.scope(state: \.announcements, action: \.internal.announcements))
		.destinations($store)
	}

	@ViewBuilder private var quickLaunch: some View {
		if let quickLaunch = store.quickLaunch {
			Section {
				Button { send(.didTapQuickLaunchButton, animation: .default) } label: {
					HStack(spacing: 0) {
						HStack(spacing: .standardSpacing) {
							Image(systemName: "figure.bowling")
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

						Spacer(minLength: .standardSpacing)

						HStack(spacing: .standardSpacing) {
							VStack(alignment: .trailing) {
								Text(Strings.QuickLaunch.Bowlerslist.title)
									.font(.subheadline)

								Text(Strings.QuickLaunch.Bowlerslist.subtitle)
									.font(.caption)
							}

							Image(systemName: "chevron.right")
								.font(.caption2)
						}
					}
					.contentShape(Rectangle())
				}
				.modifier(PrimaryButton())
			}
			.listRowInsets(EdgeInsets())
			.compactList()

			if store.isShowingQuickLaunchTip {
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

	@ViewBuilder private var widgets: some View {
		if store.isShowingWidgets {
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

extension View {
	fileprivate func destinations(_ store: Bindable<StoreOf<BowlersList>>) -> some View {
		self
			.details(store.scope(state: \.destination?.details, action: \.internal.destination.details))
			.bowlerEditor(store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
			.sortOrder(store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
			.seriesEditor(store.scope(state: \.destination?.seriesEditor, action: \.internal.destination.seriesEditor))
			.leaguesList(store.scope(state: \.destination?.leagues, action: \.internal.destination.leagues))
			.gamesList(store.scope(state: \.destination?.games, action: \.internal.destination.games))
	}

	fileprivate func bowlerEditor(_ store: Binding<StoreOf<BowlerEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}

	fileprivate func details(_ store: Binding<StoreOf<BowlerDetails>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<BowlerDetails>) in
			BowlerDetailsView(store: store)
		}
	}

	fileprivate func sortOrder(_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func seriesEditor(_ store: Binding<StoreOf<SeriesEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func leaguesList(_ store: Binding<StoreOf<LeaguesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<LeaguesList>) in
			LeaguesListView(store: store)
		}
	}

	fileprivate func gamesList(_ store: Binding<StoreOf<GamesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}

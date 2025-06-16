import AnnouncementsFeature
import BowlerDetailsFeature
import BowlerEditorFeature
import ComposableArchitecture
import GamesListFeature
import LeaguesListFeature
import ModelsLibrary
import SeriesEditorFeature
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StringsLibrary
import SwiftUI

@ViewAction(for: Overview.self)
public struct OverviewView: View {
	@Bindable public var store: StoreOf<Overview>

	public init(store: StoreOf<Overview>) {
		self.store = store
	}

	public var body: some View {
		List {
			QuickLaunchView(store: store.scope(state: \.quickLaunch, action: \.internal.quickLaunch))

			if let store = store.scope(state: \.widgets, action: \.internal.widgets) {
				StatisticsWidgetLayoutView(store: store)
					.listRowSeparator(.hidden)
					.listRowInsets(EdgeInsets())
					.listRowBackground(Color.clear)
					.listSectionSpacing(.compact)
			}

			BowlersSectionView(store: store.scope(state: \.bowlers, action: \.internal.bowlers))

			TeamsSectionView(store: store.scope(state: \.teams, action: \.internal.teams))
		}
		.navigationTitle(Strings.Overview.title)
		.task { await send(.didStartTask).finish() }
		.onAppear { send(.onAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.announcements(store: store.scope(state: \.announcements, action: \.internal.announcements))
		.connectingBowlersSection(store.scope(state: \.bowlers, action: \.internal.bowlers))
		.connectingDataSource(store.scope(state: \.teams.list, action: \.internal.teams.internal.list))
		.destinations($store)
	}
}

// MARK: - Destinations

extension View {
	fileprivate func destinations(_ store: Bindable<StoreOf<Overview>>) -> some View {
		self
			.gamesList(store.scope(state: \.destination?.gamesList, action: \.internal.destination.gamesList))
			.seriesEditor(store.scope(state: \.destination?.seriesEditor, action: \.internal.destination.seriesEditor))
			.teamSortOrder(store.scope(state: \.destination?.teamSortOrder, action: \.internal.destination.teamSortOrder))
	}

	fileprivate func gamesList(_ store: Binding<StoreOf<GamesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}

	fileprivate func seriesEditor(_ store: Binding<StoreOf<SeriesEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func teamSortOrder(
		_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Team.List.FetchRequest>>?>
	) -> some View {
		sheet(item: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Team.List.FetchRequest>>) in
			SortOrderView(store: store)
				.presentationDetents([.medium])
		}
	}
}

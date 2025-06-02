import ComposableArchitecture
import GamesListFeature
import SeriesEditorFeature
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
		}
		.navigationTitle(Strings.Overview.title)
		.task { await send(.didStartTask).finish() }
		.onAppear { send(.onAppear) }
		.destinations($store)
	}
}

// MARK: - Destinations

extension View {
	fileprivate func destinations(_ store: Bindable<StoreOf<Overview>>) -> some View {
		self
			.gamesList(store.scope(state: \.destination?.games, action: \.internal.destination.games))
			.seriesEditor(store.scope(state: \.destination?.seriesEditor, action: \.internal.destination.seriesEditor))
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
}

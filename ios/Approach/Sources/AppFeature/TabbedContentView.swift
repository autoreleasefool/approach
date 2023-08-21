import AccessoriesOverviewFeature
import AssetsLibrary
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import SettingsFeature
import StatisticsOverviewFeature
import StringsLibrary
import SwiftUI

public struct TabbedContentView: View {
	let store: StoreOf<TabbedContent>

	@Environment(\.horizontalSizeClass) var horizontalSizeClass

	struct ViewState: Equatable {
		let tabs: [TabbedContent.Tab]
		@BindingViewState var selectedTab: TabbedContent.Tab
	}

	public init(store: StoreOf<TabbedContent>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			// FIXME: create sidebar for ipad size devices
			TabView(selection: viewStore.$selectedTab) {
				ForEach(viewStore.tabs) { tab in
					NavigationStack {
						switch tab {
						case .overview:
							BowlersListView(
								store: store.scope(state: \.bowlersList, action: /TabbedContent.Action.InternalAction.bowlersList)
							)
						case .statistics:
							StatisticsOverviewView(
								store: store.scope(state: \.statistics, action: /TabbedContent.Action.InternalAction.statistics)
							)
						case .accessories:
							AccessoriesOverviewView(
								store: store.scope(state: \.accessories, action: /TabbedContent.Action.InternalAction.accessories)
							)
						case .settings:
							SettingsView(
								store: store.scope(state: \.settings, action: /TabbedContent.Action.InternalAction.settings)
							)
						}
					}
					.tag(tab)
					.tabItem {
						Label(tab.name, systemSymbol: tab.symbol)
					}
				}
			}
			.tint(Asset.Colors.Action.default)
			.task { await viewStore.send(.didAppear).finish() }
		})
	}
}

extension TabbedContentView.ViewState {
	init(store: BindingViewStore<TabbedContent.State>) {
		self._selectedTab = store.$selectedTab
		self.tabs = store.tabs
	}
}

extension TabbedContent.Tab {
	var name: String {
		switch self {
		case .overview:
			return Strings.App.Tabs.overview
		case .settings:
			return Strings.App.Tabs.settings
		case .accessories:
			return Strings.App.Tabs.accessories
		case .statistics:
			return Strings.App.Tabs.statistics
		}
	}

	var symbol: SFSymbol {
		switch self {
		case .accessories:
			return .bag
		case .settings:
			return .gear
		case .overview:
			return .figureBowling
		case .statistics:
			return .chartBar
		}
	}
}

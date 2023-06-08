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
		let selectedTab: TabbedContent.Tab

		init(state: TabbedContent.State) {
			self.tabs = state.tabs
			self.selectedTab = state.selectedTab
		}
	}

	enum ViewAction {
		case didAppear
		case didSelectTab(TabbedContent.Tab)
	}

	public init(store: StoreOf<TabbedContent>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TabbedContent.Action.init) { viewStore in
			if horizontalSizeClass == .compact {
				TabView(
					selection: viewStore.binding(get: \.selectedTab, send: ViewAction.didSelectTab)
				) {
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
							Label(tab.name, systemImage: tab.image)
						}
					}
				}
				.tint(.appAction)
				.task { await viewStore.send(.didAppear).finish() }
			} else {
				// TODO: create sidebar for ipad size devices
				EmptyView()
			}
		}
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

	var image: String {
		switch self {
		case .accessories:
			return "bag"
		case .settings:
			return "gear"
		case .overview:
			return "figure.bowling"
		case .statistics:
			return "chart.bar"
		}
	}
}

extension TabbedContent.Action {
	init(action: TabbedContentView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .didSelectTab(tab):
			self = .view(.didSelectTab(tab))
		}
	}
}

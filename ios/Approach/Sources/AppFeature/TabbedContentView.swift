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
								store: store.scope(state: \.bowlersList, action: \.internal.bowlersList)
							)
						case .statistics:
							StatisticsOverviewView(
								store: store.scope(state: \.statistics, action: \.internal.statistics)
							)
						case .accessories:
							AccessoriesOverviewView(
								store: store.scope(state: \.accessories, action: \.internal.accessories)
							)
						case .settings:
							SettingsView(
								store: store.scope(state: \.settings, action: \.internal.settings)
							)
						}
					}
					.tag(tab)
					.tabItem {
						Label(title: { Text(tab.name) }, icon: { Image(uiImage: tab.icon) })
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

	var icon: UIImage {
		switch self {
		case .accessories:
			return Asset.Media.Icons.alley.image
		case .settings:
			return UIImage(systemSymbol: .gear)
		case .overview:
			return UIImage(systemSymbol: .figureBowling)
		case .statistics:
			return UIImage(systemSymbol: .chartBar)
		}
	}
}

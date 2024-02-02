import AccessoriesOverviewFeature
import AssetsLibrary
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import SettingsFeature
import StatisticsOverviewFeature
import StringsLibrary
import SwiftUI

@ViewAction(for: TabbedContent.self)
public struct TabbedContentView: View {
	@Perception.Bindable public var store: StoreOf<TabbedContent>

	public init(store: StoreOf<TabbedContent>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			// FIXME: create sidebar for ipad size devices
			TabView(selection: $store.selectedTab) {
				NavigationStack {
					BowlersListView(
						store: store.scope(state: \.bowlersList, action: \.internal.bowlersList)
					)
				}
				.tag(TabbedContent.Tab.overview)
				.tabItem { TabbedContent.Tab.overview.label }

				NavigationStack {
					StatisticsOverviewView(
						store: store.scope(state: \.statistics, action: \.internal.statistics)
					)
				}
				.tag(TabbedContent.Tab.statistics)
				.tabItem { TabbedContent.Tab.statistics.label }

				NavigationStack {
					AccessoriesOverviewView(
						store: store.scope(state: \.accessories, action: \.internal.accessories)
					)
				}
				.tag(TabbedContent.Tab.accessories)
				.tabItem { TabbedContent.Tab.accessories.label }

				NavigationStack {
					SettingsView(
						store: store.scope(state: \.settings, action: \.internal.settings)
					)
				}
				.tag(TabbedContent.Tab.settings)
				.tabItem { TabbedContent.Tab.settings.label }
			}
			.tint(Asset.Colors.Action.default)
			.task { await send(.didAppear).finish() }
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

	var label: some View {
		Label(title: { Text(name) }, icon: { Image(uiImage: icon) })
	}
}

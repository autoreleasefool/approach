import AccessoriesOverviewFeature
import AchievementsFeature
import AssetsLibrary
import AutomaticBackupsFeature
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import SettingsFeature
import StatisticsOverviewFeature
import StringsLibrary
import SwiftUI
import ToastUI

@ViewAction(for: TabbedContent.self)
public struct TabbedContentView: View {
	@Bindable public var store: StoreOf<TabbedContent>

	public init(store: StoreOf<TabbedContent>) {
		self.store = store
	}

	public var body: some View {
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
		.task { await send(.didStartTask).finish() }
		.observeAchievements(store: store.scope(state: \.achievements, action: \.internal.achievements))
		.automaticBackups(store: store.scope(state: \.backups, action: \.internal.backups))
		.toast(isPresented: $store.isHudVisible) {
			ToastView("Loading")
				.toastViewStyle(.indeterminate)
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
			return UIImage(systemName: "gear") ?? UIImage()
		case .overview:
			return UIImage(systemName: "figure.bowling") ?? UIImage()
		case .statistics:
			return UIImage(systemName: "chart.bar") ?? UIImage()
		}
	}

	var label: some View {
		Label(title: { Text(name) }, icon: { Image(uiImage: icon) })
	}
}

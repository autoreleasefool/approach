import AccessoriesOverviewFeature
import AchievementsFeature
import AnalyticsServiceInterface
import AssetsLibrary
import AutomaticBackupsFeature
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import HUDServiceInterface
import SettingsFeature
import StatisticsOverviewFeature
import StringsLibrary
import SwiftUI
import ToastUI

@Reducer
public struct TabbedContent: Reducer, Sendable {

	@ObservableState
	public struct State: Equatable {
		public var isHudVisible: Bool = false

		public var selectedTab: Tab = .overview

		public var accessories = AccessoriesOverview.State()
		public var bowlersList = BowlersList.State()
		public var statistics = StatisticsOverview.State()
		public var settings = Settings.State()

		public var achievements = AchievementsObserver.State()
		public var backups = AutomaticBackups.State()

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case didStartTask
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case showHUD(Bool)

			case accessories(AccessoriesOverview.Action)
			case bowlersList(BowlersList.Action)
			case settings(Settings.Action)
			case statistics(StatisticsOverview.Action)
			case achievements(AchievementsObserver.Action)
			case backups(AutomaticBackups.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public enum Tab {
		case overview
		case statistics
		case accessories
		case settings
	}

	public init() {}

	@Dependency(HUDService.self) var hud

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.bowlersList, action: \.internal.bowlersList) {
			BowlersList()
		}

		Scope(state: \.settings, action: \.internal.settings) {
			Settings()
		}

		Scope(state: \.accessories, action: \.internal.accessories) {
			AccessoriesOverview()
		}

		Scope(state: \.statistics, action: \.internal.statistics) {
			StatisticsOverview()
		}

		Scope(state: \.achievements, action: \.internal.achievements) {
			AchievementsObserver()
		}

		Scope(state: \.backups, action: \.internal.backups) {
			AutomaticBackups()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .run { send in
						for await status in hud.hudStatusNotifications() {
							await send(.internal(.showHUD(status.isShowing)))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .showHUD(isShowing):
					state.isHudVisible = isShowing
					return .none

				// swiftlint:disable:next line_length
				case .bowlersList(.internal(.announcements(.internal(.destination(.presented(.tenYearAnniversary(.view(.didTapClaimButton)))))))):
					state.selectedTab = .settings
					return state.settings.showAchievementsList().map { .internal(.settings($0)) }

				case .backups(.internal(.backupFailure(.presented(.view(.didTapOpenSettingsButton))))):
					state.selectedTab = .settings
					return state.settings.showBackupsList().map { .internal(.settings($0)) }

				case .accessories(.view), .accessories(.internal), .accessories(.delegate(.doNothing)),
						.bowlersList(.view), .bowlersList(.internal), .bowlersList(.delegate(.doNothing)),
						.settings(.view), .settings(.internal), .settings(.delegate(.doNothing)), .settings(.binding),
						.statistics(.view), .statistics(.internal), .statistics(.delegate(.doNothing)),
						.achievements(.view), .achievements(.internal), .achievements(.delegate(.doNothing)),
						.backups(.view), .backups(.internal), .backups(.delegate(.doNothing)):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .binding(\.selectedTab):
				return Analytics.App.TabSwitched(tab: String(describing: state.selectedTab))
			default:
				return nil
			}
		}
	}
}

// MARK: - View

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
			ToastView(Strings.loading)
				.toastViewStyle(.indeterminate)
		}
	}
}

extension TabbedContent.Tab {
	var name: String {
		switch self {
		case .overview: Strings.App.Tabs.overview
		case .settings: Strings.App.Tabs.settings
		case .accessories: Strings.App.Tabs.accessories
		case .statistics: Strings.App.Tabs.statistics
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

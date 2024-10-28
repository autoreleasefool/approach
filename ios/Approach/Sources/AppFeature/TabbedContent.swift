import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import AutomaticBackupsFeature
import BadgesFeature
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import HUDServiceInterface
import SettingsFeature
import StatisticsOverviewFeature

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

		public var badges = BadgesObserver.State()
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
			case badges(BadgesObserver.Action)
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

		Scope(state: \.badges, action: \.internal.badges) {
			BadgesObserver()
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
				case .bowlersList(.internal(.announcements(.internal(.destination(.presented(.halloween2024(.view(.didTapOpenIconSettingsButton)))))))):
					state.selectedTab = .settings
					return state.settings.showAppIconList().map { .internal(.settings($0)) }

				case .backups(.internal(.backupFailure(.presented(.view(.didTapOpenSettingsButton))))):
					state.selectedTab = .settings
					return state.settings.showBackupsList().map { .internal(.settings($0)) }

				case .accessories(.view), .accessories(.internal), .accessories(.delegate(.doNothing)),
						.bowlersList(.view), .bowlersList(.internal), .bowlersList(.delegate(.doNothing)),
						.settings(.view), .settings(.internal), .settings(.delegate(.doNothing)), .settings(.binding),
						.statistics(.view), .statistics(.internal), .statistics(.delegate(.doNothing)),
						.badges(.view), .badges(.internal), .badges(.delegate(.doNothing)),
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

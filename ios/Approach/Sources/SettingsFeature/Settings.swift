import AnalyticsServiceInterface
import AppIconServiceInterface
import ArchiveListFeature
import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import DatabaseMockingServiceInterface
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface
import OpponentsListFeature
import StringsLibrary
import ToastLibrary

@Reducer
public struct Settings: Reducer {
	public struct State: Equatable {
		public var isShowingDeveloperOptions: Bool
		public var helpSettings = HelpSettings.State()

		public var isLoadingAppIcon: Bool = true
		public var currentAppIcon: AppIcon?

		public var toast: ToastState<ToastAction>?
		@PresentationState public var destination: Destination.State?

		public init() {
			@Dependency(\.featureFlags) var featureFlags
			self.isShowingDeveloperOptions = featureFlags.isEnabled(.developerOptions)
		}

		public mutating func showAppIconList() -> Effect<Settings.Action> {
			self.destination = .appIcon(.init())
			return .none
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didFirstAppear
			case didTapPopulateDatabase
			case didTapFeatureFlags
			case didTapOpponents
			case didTapStatistics
			case didTapArchive
			case didTapAppIcon
			case didTapVersionNumber
		}
		public enum DelegateAction: Equatable { case doNothing }
		public enum InternalAction: Equatable {
			case didFetchIcon(TaskResult<AppIcon?>)
			case didCopyToClipboard

			case toast(ToastAction)
			case helpSettings(HelpSettings.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case archive(ArchiveList.State)
			case appIcon(AppIconList.State)
			case featureFlags(FeatureFlagsList.State)
			case opponentsList(OpponentsList.State)
			case statistics(StatisticsSettings.State)
		}

		public enum Action: Equatable {
			case archive(ArchiveList.Action)
			case appIcon(AppIconList.Action)
			case featureFlags(FeatureFlagsList.Action)
			case opponentsList(OpponentsList.Action)
			case statistics(StatisticsSettings.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.archive, action: /Action.archive) {
				ArchiveList()
			}
			Scope(state: /State.appIcon, action: /Action.appIcon) {
				AppIconList()
			}
			Scope(state: /State.featureFlags, action: /Action.featureFlags) {
				FeatureFlagsList()
			}
			Scope(state: /State.opponentsList, action: /Action.opponentsList) {
				OpponentsList()
			}
			Scope(state: /State.statistics, action: /Action.statistics) {
				StatisticsSettings()
			}
		}
	}

	public enum ToastAction: ToastableAction, Equatable {
		case didDismiss
		case didFinishDismissing
	}

	@Dependency(\.appIcon) var appIcon
	@Dependency(\.databaseMocking) var databaseMocking
	@Dependency(\.pasteboard) var pasteboard

	public init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.helpSettings, action: /Action.internal..Action.InternalAction.helpSettings) {
			HelpSettings()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .run { send in
						await send(.internal(.didFetchIcon(TaskResult {
							AppIcon(rawValue: await appIcon.getAppIconName() ?? "")
						})))
					}

				case .didTapPopulateDatabase:
					return .run { _ in try await databaseMocking.mockDatabase() }

				case .didTapOpponents:
					state.destination = .opponentsList(.init())
					return .none

				case .didTapFeatureFlags:
					state.destination = .featureFlags(.init())
					return .none

				case .didTapStatistics:
					state.destination = .statistics(.init())
					return .none

				case .didTapAppIcon:
					state.destination = .appIcon(.init())
					return .none

				case .didTapArchive:
					state.destination = .archive(.init())
					return .none

				case .didTapVersionNumber:
					return .run { send in
						pasteboard.copyToClipboard(AppConstants.appVersionReadable)
						await send(.internal(.didCopyToClipboard))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCopyToClipboard:
					state.toast = .init(
						content: .toast(.init(
							message: .init(Strings.copiedToClipboard),
							icon: .checkmarkCircleFill
						)),
						style: .success
					)
					return .none

				case let .didFetchIcon(.success(icon)):
					state.isLoadingAppIcon = false
					state.currentAppIcon = icon
					return .none

				case .didFetchIcon(.failure):
					state.isLoadingAppIcon = false
					return .none

				case .helpSettings(.delegate(.doNothing)):
					return .none

				case let .toast(toastAction):
					switch toastAction {
					case .didDismiss:
						state.toast = nil
						return .none

					case .didFinishDismissing:
						return .none
					}

				case .destination(.presented(.archive(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.featureFlags(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.opponentsList(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.statistics(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.appIcon(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.featureFlags(.internal))), .destination(.presented(.featureFlags(.view))),
						.destination(.presented(.statistics(.internal))), .destination(.presented(.statistics(.view))),
						.destination(.presented(.appIcon(.view))), .destination(.presented(.appIcon(.internal))),
						.destination(.presented(.opponentsList(.internal))), .destination(.presented(.opponentsList(.view))),
						.destination(.presented(.archive(.internal))), .destination(.presented(.archive(.view))),
						.helpSettings(.internal), .helpSettings(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapOpponents):
				return Analytics.Settings.ViewedOpponents()
			case .view(.didTapStatistics):
				return Analytics.Settings.ViewedStatistics()
			case .view(.didTapAppIcon):
				return Analytics.Settings.ViewedAppIcons()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

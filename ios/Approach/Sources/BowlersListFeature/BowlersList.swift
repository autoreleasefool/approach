import AnalyticsServiceInterface
import AnnouncementsFeature
import AnnouncementsLibrary
import AssetsLibrary
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesListFeature
import LeaguesListFeature
import ModelsLibrary
import PreferenceServiceInterface
import QuickLaunchRepositoryInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SeriesEditorFeature
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StringsLibrary
import TipsLibrary
import TipsServiceInterface
import ViewsLibrary

extension Bowler.List: ResourceListItem {}

extension Bowler.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

@Reducer
// swiftlint:disable:next type_body_length
public struct BowlersList: Reducer {
	public static let widgetContext = "bowlersList"

	@ObservableState
	public struct State: Equatable {
		public var list: ResourceList<Bowler.List, Bowler.Ordering>.State
		public var widgets: StatisticsWidgetLayout.State = .init(context: BowlersList.widgetContext, newWidgetSource: nil)
		public var ordering: Bowler.Ordering = .default
		public var quickLaunch: QuickLaunchSource?

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		public var isShowingQuickLaunchTip: Bool
		public var isShowingWidgets: Bool

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: .default,
				listTitle: Strings.Bowler.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.bowlers,
					title: Strings.Bowler.Error.Empty.title,
					message: Strings.Bowler.Error.Empty.message,
					action: Strings.Bowler.List.add
				)
			)

			@Dependency(\.preferences) var preferences
			self.isShowingWidgets = preferences.bool(forKey: .statisticsWidgetHideInBowlerList) != true

			@Dependency(\.tips) var tips
			self.isShowingQuickLaunchTip = tips.shouldShow(tipFor: .quickLaunchTip)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case didFirstAppear
			case didStartTask
			case didTapSortOrderButton
			case didTapBowler(Bowler.ID)
			case didTapQuickLaunchButton
		}

		@CasePathable public enum Delegate { case doNothing }

		@CasePathable public enum Internal {
			case didLoadEditableBowler(Result<Bowler.Edit, Error>)
			case didLoadQuickLaunch(Result<QuickLaunchSource?, Error>)
			case didArchiveBowler(Result<Bowler.List, Error>)
			case didSetIsShowingWidgets(Bool)
			case showAnnouncement(Announcement)

			case list(ResourceList<Bowler.List, Bowler.Ordering>.Action)
			case widgets(StatisticsWidgetLayout.Action)
			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case editor(BowlerEditor)
		case leagues(LeaguesList)
		case sortOrder(SortOrder<Bowler.Ordering>)
		case seriesEditor(SeriesEditor)
		case games(GamesList)
		case announcement(Announcements)
	}

	public enum ErrorID {
		case bowlerNotFound
		case failedToArchiveBowler
	}

	public init() {}

	@Dependency(\.announcements) var announcements
	@Dependency(\.bowlers) var bowlers
	@Dependency(\.calendar) var calendar
	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.preferences) var preferences
	@Dependency(\.quickLaunch) var quickLaunch
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.tips) var tips
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: \.internal.list) {
			ResourceList(fetchResources: bowlers.list)
		}

		Scope(state: \.widgets, action: \.internal.widgets) {
			StatisticsWidgetLayout()
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .run { send in
						guard let announcement = announcements.announcement() else { return }
						await send(.internal(.showAnnouncement(announcement)))
					}

				case .didStartTask:
					return .merge(
						.run { send in
							for await _ in preferences.observe(keys: [.statisticsWidgetHideInBowlerList]) {
								await send(.internal(.didSetIsShowingWidgets(
									preferences.bool(forKey: .statisticsWidgetHideInBowlerList) != true
								)))
							}
						},
						.run { send in
							await send(.internal(.didLoadQuickLaunch(Result {
								try await quickLaunch.defaultSource()
							})))
						}
					)

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.list.query))
					return .none

				case let .didTapBowler(id):
					guard let bowler = state.list.findResource(byId: id) else { return .none }
					state.destination = .leagues(.init(bowler: bowler.summary))
					return .run { _ in
						try await clock.sleep(for: .seconds(1))
						recentlyUsed.didRecentlyUseResource(.bowlers, id)
					}

				case .didTapQuickLaunchButton:
					guard let league = state.quickLaunch?.league else { return .none }
					state.isShowingQuickLaunchTip = false
					state.destination = .seriesEditor(.init(
						value: .create(.default(withId: uuid(), onDate: calendar.startOfDay(for: date()), inLeague: league)),
						inLeague: league
					))
					return .run { _ in await tips.hide(tipFor: .quickLaunchTip) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didSetIsShowingWidgets(isShowing):
					state.isShowingWidgets = isShowing
					return .none

				case let .didLoadEditableBowler(.success(bowler)):
					state.destination = .editor(.init(value: .edit(bowler)))
					return .none

				case let .didLoadQuickLaunch(.success(quickLaunch)):
					state.quickLaunch = quickLaunch
					return .none

				case .didArchiveBowler(.success):
					return .none

				case let .showAnnouncement(announcement):
					state.destination = .announcement(.init(announcement: announcement))
					return .none

				case let .didLoadEditableBowler(.failure(error)):
					return state.errors
						.enqueue(.bowlerNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didArchiveBowler(.failure(error)):
					return state.errors
						.enqueue(.failedToArchiveBowler, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case .didLoadQuickLaunch(.failure):
					// Intentionally drop quick launch errors
					return .none

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						guard let quickLaunch = state.quickLaunch else { return .none }
						state.destination = .games(.init(series: created.asSummary, host: quickLaunch.league))
						return .none

					case .didFinishArchiving, .didFinishUpdating:
						return .none
					}

				case let .destination(.presented(.announcement(.delegate(delegateAction)))):
					switch delegateAction {
					case .openAppIconSettings:
						return .none
					}

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(bowler):
						return .run { send in
							await send(.internal(.didLoadEditableBowler(Result {
								try await bowlers.edit(bowler.id)
							})))
						}

					case let .didArchive(bowler):
						return .run { send in
							await send(.internal(.didArchiveBowler(Result {
								try await bowlers.archive(bowler.id)
								return bowler
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.defaultBowler(withId: uuid()))))
						return .none

					case .didTap, .didDelete, .didMove:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: option)
							.map { .internal(.list($0)) }
					}

				case .destination(.dismiss):
					switch state.destination {
					case let .announcement(announcement):
						return .run { [announcement = announcement.announcement] _ in
							await announcements.hideAnnouncement(announcement)
						}
					case .editor, .leagues, .sortOrder, .seriesEditor, .games, .none:
						return .none
					}

				case .destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.editor(.delegate(.doNothing)))),
						.destination(.presented(.leagues(.internal))),
						.destination(.presented(.leagues(.view))),
						.destination(.presented(.leagues(.delegate(.doNothing)))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.binding))),
						.destination(.presented(.games(.internal))),
						.destination(.presented(.games(.view))),
						.destination(.presented(.games(.delegate(.doNothing)))),
						.destination(.presented(.announcement(.internal))),
						.destination(.presented(.announcement(.view))),
						.list(.internal), .list(.view),
						.widgets(.internal), .widgets(.view), .widgets(.delegate(.doNothing)),
						.errors(.view), .errors(.internal), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapBowler):
				return Analytics.Bowler.Viewed(kind: Bowler.Kind.playable.rawValue)
			case .internal(.list(.delegate(.didArchive))):
				return Analytics.Bowler.Archived(kind: Bowler.Kind.playable.rawValue)
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

extension Tip {
	static let quickLaunchTip = Tip(
		id: "Bowlers.List.QuickLaunch",
		title: Strings.QuickLaunch.BowlersList.Tip.title,
		message: Strings.QuickLaunch.BowlersList.Tip.message
	)
}

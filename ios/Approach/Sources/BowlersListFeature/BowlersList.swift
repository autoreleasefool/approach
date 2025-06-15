import AnalyticsServiceInterface
import AnnouncementsFeature
import AssetsLibrary
import BowlerDetailsFeature
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorReportingClientPackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import GamesListFeature
import GamesRepositoryInterface
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
		case .byRecentlyUsed: Strings.Ordering.mostRecentlyUsed
		case .byName: Strings.Ordering.alphabetical
		}
	}
}

@Reducer
public struct BowlersList: Reducer, Sendable {
	public static let widgetContext = "bowlersList"

	@ObservableState
	public struct State: Equatable {
		public var list: ResourceList<Bowler.List, Bowler.List.FetchRequest>.State
		public var widgets: StatisticsWidgetLayout.State = .init(context: BowlersList.widgetContext, newWidgetSource: nil)
		public var quickLaunch: QuickLaunchSource?

		@Shared(.fetchRequest) public var fetchRequest: Bowler.List.FetchRequest

		public var errors: Errors<ErrorID>.State = .init()

		public var announcements = Announcements.State()
		@Presents public var destination: Destination.State?

		public var isShowingQuickLaunchTip: Bool
		public var isShowingWidgets: Bool

		public init() {
			let fetchRequest = Shared(.fetchRequest)
			self._fetchRequest = fetchRequest

			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: SharedReader(fetchRequest),
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

			@Dependency(TipsService.self) var tips
			self.isShowingQuickLaunchTip = tips.shouldShow(tipFor: .quickLaunchTip)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didFirstAppear
			case didStartTask
			case didTapSortOrderButton
			case didTapBowler(Bowler.ID)
			case didTapQuickLaunchButton
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case didLoadEditableBowler(Result<Bowler.Edit, Error>)
			case didLoadQuickLaunch(Result<QuickLaunchSource?, Error>)
			case didArchiveBowler(Result<Bowler.List, Error>)
			case didSetIsShowingWidgets(Bool)

			case announcements(Announcements.Action)
			case list(ResourceList<Bowler.List, Bowler.List.FetchRequest>.Action)
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
		case details(BowlerDetails)
		case leagues(LeaguesList)
		case sortOrder(SortOrder<Bowler.Ordering>)
		case seriesEditor(SeriesEditor)
		case games(GamesList)
	}

	public enum ErrorID: Hashable, Sendable {
		case bowlerNotFound
		case failedToArchiveBowler
	}

	public init() {}

	@Dependency(\.analytics) var analytics
	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(\.calendar) var calendar
	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.errors) var errors
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(GamesRepository.self) var games
	@Dependency(\.preferences) var preferences
	@Dependency(QuickLaunchRepository.self) var quickLaunch
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(TipsService.self) var tips
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

		Scope(state: \.announcements, action: \.internal.announcements) {
			Announcements()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .run { _ in
						do {
							try await games.lockStaleGames()
						} catch {
							errors.captureError(error)
						}
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
					state.destination = .sortOrder(.init(initialValue: state.$fetchRequest))
					return .none

				case let .didTapBowler(id):
					guard let bowler = state.list.findResource(byId: id) else { return .none }
					state.destination = if featureFlags.isFlagEnabled(.bowlerDetails) {
						.details(BowlerDetails.State(bowler: bowler.summary))
					} else {
						.leagues(LeaguesList.State(bowler: bowler.summary))
					}
					return recentlyUsed.didRecentlyUse(.bowlers, id: id, in: self)

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

				case .destination(.presented(.details(.delegate(.doNothing)))):
					return .none

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						guard let quickLaunch = state.quickLaunch else { return .none }
						state.destination = .games(.init(series: created.asGameHost, host: quickLaunch.league))
						return .none

					case .didFinishArchiving, .didFinishUpdating:
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

				case .destination(.presented(.sortOrder(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss):
					return .none

				case .destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.editor(.delegate(.doNothing)))),
						.destination(.presented(.details(.internal))),
						.destination(.presented(.details(.view))),
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
						.announcements(.internal), .announcements(.view), .announcements(.delegate(.doNothing)),
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

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadEditableBowler(.failure(error))),
				let .internal(.didArchiveBowler(.failure(error))),
				let .internal(.didLoadQuickLaunch(.failure(error))):
				return error
			default:
				return nil
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

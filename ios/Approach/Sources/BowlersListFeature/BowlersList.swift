import AnalyticsServiceInterface
import AssetsLibrary
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import LeaguesListFeature
import ModelsLibrary
import PreferenceServiceInterface
import RecentlyUsedServiceInterface
import QuickLaunchRepositoryInterface
import ResourceListLibrary
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StringsLibrary
import ToastLibrary
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

public struct BowlersList: Reducer {
	public static let widgetContext = "bowlersList"

	public struct State: Equatable {
		public var list: ResourceList<Bowler.List, Bowler.Ordering>.State
		public var widgets: StatisticsWidgetLayout.State = .init(context: BowlersList.widgetContext, newWidgetSource: nil)
		public var ordering: Bowler.Ordering = .byRecentlyUsed
		public var quickLaunch: QuickLaunchSource?

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public var isShowingWidgets: Bool
		public let isQuickLaunchEnabled: Bool

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: ordering,
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

			@Dependency(\.featureFlags) var featureFlags
			self.isQuickLaunchEnabled = featureFlags.isEnabled(.seriesQuickCreate)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didStartTask
			case didTapSortOrderButton
			case didTapBowler(Bowler.ID)
			case didTapQuickLaunchButton
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableBowler(TaskResult<Bowler.Edit>)
			case didLoadQuickLaunch(TaskResult<QuickLaunchSource?>)
			case didArchiveBowler(TaskResult<Bowler.List>)
			case didSetIsShowingWidgets(Bool)

			case list(ResourceList<Bowler.List, Bowler.Ordering>.Action)
			case widgets(StatisticsWidgetLayout.Action)
			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(BowlerEditor.State)
			case leagues(LeaguesList.State)
			case sortOrder(SortOrder<Bowler.Ordering>.State)
		}

		public enum Action: Equatable {
			case editor(BowlerEditor.Action)
			case leagues(LeaguesList.Action)
			case sortOrder(SortOrder<Bowler.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				BowlerEditor()
			}
			Scope(state: /State.leagues, action: /Action.leagues) {
				LeaguesList()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public enum ErrorID {
		case bowlerNotFound
		case failedToArchiveBowler
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.preferences) var preferences
	@Dependency(\.quickLaunch) var quickLaunch
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: bowlers.list)
		}

		Scope(state: \.widgets, action: /Action.internal..Action.InternalAction.widgets) {
			StatisticsWidgetLayout()
		}

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .merge(
						.run { send in
							for await _ in preferences.observe(keys: [.statisticsWidgetHideInBowlerList]) {
								await send(.internal(.didSetIsShowingWidgets(
									preferences.bool(forKey: .statisticsWidgetHideInBowlerList) != true
								)))
							}
						},
						.run { [isQuickLaunchEnabled = state.isQuickLaunchEnabled] send in
							guard isQuickLaunchEnabled else { return }
							for try await source in quickLaunch.defaultSource() {
								await send(.internal(.didLoadQuickLaunch(.success(source))))
							}
						} catch: { error, send in
							await send(.internal(.didLoadQuickLaunch(.failure(error))))
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
					// TODO: Open quick launch menu
					return .none
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

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(bowler):
						return .run { send in
							await send(.internal(.didLoadEditableBowler(TaskResult {
								try await bowlers.edit(bowler.id)
							})))
						}

					case let .didArchive(bowler):
						return .run { send in
							await send(.internal(.didArchiveBowler(TaskResult {
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

				case let .widgets(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: option)
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.leagues(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .widgets(.internal), .widgets(.view):
					return .none

				case .errors(.view), .errors(.internal):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.leagues(.internal))),
						.destination(.presented(.leagues(.view))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))):
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
			case .view(.didTapBowler):
				return Analytics.Bowler.Viewed(kind: Bowler.Kind.playable.rawValue)
			case .internal(.list(.delegate(.didArchive))):
				return Analytics.Bowler.Archived(kind: Bowler.Kind.playable.rawValue)
			default:
				return nil
			}
		}
	}
}

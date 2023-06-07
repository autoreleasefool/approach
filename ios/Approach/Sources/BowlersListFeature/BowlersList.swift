import AnalyticsServiceInterface
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import LeaguesListFeature
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
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
	public struct State: Equatable {
		public var list: ResourceList<Bowler.List, Bowler.Ordering>.State
		public var ordering: Bowler.Ordering = .byRecentlyUsed

		@PresentationState public var destination: Destination.State?

		public let hasAvatarsEnabled: Bool

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(
						onDelete: .init {
							@Dependency(\.bowlers) var bowlers: BowlersRepository
							try await bowlers.delete($0.id)
						}
					),
				],
				query: ordering,
				listTitle: Strings.Bowler.List.title,
				emptyContent: .init(
					image: .emptyBowlers,
					title: Strings.Bowler.Error.Empty.title,
					message: Strings.Bowler.Error.Empty.message,
					action: Strings.Bowler.List.add
				)
			)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAvatarsEnabled = featureFlags.isEnabled(.avatars)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapConfigureStatisticsButton
			case didTapSortOrderButton
			case didTapBowler(Bowler.ID)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableBowler(Bowler.Edit)
			case list(ResourceList<Bowler.List, Bowler.Ordering>.Action)
			case destination(PresentationAction<Destination.Action>)
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

	public init() {}

	@Dependency(\.analytics) var analytics
	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(\.recentlyUsed) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: bowlers.list)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapConfigureStatisticsButton:
					// TODO: handle configure statistics button press
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.list.query))
					return .none

				case let .didTapBowler(id):
					guard let bowler = state.list.resources?[id: id] else { return .none }
					state.destination = .leagues(.init(bowler: bowler.summary))
					return .merge(
						.run { _ in
							try await clock.sleep(for: .seconds(1))
							recentlyUsed.didRecentlyUseResource(.bowlers, id)
						},
						.run { _ in await analytics.trackEvent(Analytics.Bowler.Viewed(id: id.uuidString)) }
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableBowler(bowler):
					state.destination = .editor(.init(value: .edit(bowler)))
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(bowler):
						return .run { send in
							guard let editable = try await bowlers.edit(bowler.id) else {
								// TODO: report bowler not found
								return
							}

							await send(.internal(.didLoadEditableBowler(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.defaultBowler(withId: uuid()))))
						return .none

					case .didDelete, .didTap:
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
	}
}

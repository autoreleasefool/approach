import AnalyticsServiceInterface
import AssetsLibrary
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import ModelsLibrary
import OpponentDetailsFeature
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Bowler.Summary: ResourceListItem {}

extension Bowler.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

public struct OpponentsList: Reducer {
	public struct State: Equatable {
		public var list: ResourceList<Bowler.Summary, Bowler.Ordering>.State
		public var ordering: Bowler.Ordering = .byRecentlyUsed
		public let isOpponentDetailsEnabled: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete,
				],
				query: ordering,
				listTitle: Strings.Opponent.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.opponents,
					title: Strings.Opponent.Error.Empty.title,
					message: Strings.Opponent.Error.Empty.message,
					action: Strings.Opponent.List.add
				)
			)

			@Dependency(\.featureFlags) var features
			self.isOpponentDetailsEnabled = features.isEnabled(.opponentDetails)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapSortOrderButton
			case didTapOpponent(Bowler.ID)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadEditableOpponent(TaskResult<Bowler.Edit>)
			case didDeleteOpponent(TaskResult<Bowler.Summary>)

			case list(ResourceList<Bowler.Summary, Bowler.Ordering>.Action)
			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case details(OpponentDetails.State)
			case editor(BowlerEditor.State)
			case sortOrder(SortOrder<Bowler.Ordering>.State)
		}

		public enum Action: Equatable {
			case details(OpponentDetails.Action)
			case editor(BowlerEditor.Action)
			case sortOrder(SortOrder<Bowler.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.details, action: /Action.details) {
				OpponentDetails()
			}
			Scope(state: /State.editor, action: /Action.editor) {
				BowlerEditor()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public enum ErrorID: Hashable {
		case opponentNotFound
		case failedToDeleteOpponent
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(\.recentlyUsed) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: bowlers.opponents(ordered:))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapOpponent(id):
					guard state.isOpponentDetailsEnabled, let opponent = state.list.resources?[id: id] else { return .none }
					state.destination = .details(.init(opponent: opponent))
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableOpponent(.success(bowler)):
					state.destination = .editor(.init(value: .edit(bowler)))
					return .none

				case .didDeleteOpponent(.success):
					return .none

				case let .didLoadEditableOpponent(.failure(error)):
					return state.errors
						.enqueue(.opponentNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteOpponent(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteOpponent, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(opponent):
						return .run { send in
							await send(.internal(.didLoadEditableOpponent(TaskResult {
								try await bowlers.edit(opponent.id)
							})))
						}

					case let .didDelete(bowler):
						return .run { send in
							await send(.internal(.didDeleteOpponent(TaskResult {
								try await bowlers.delete(bowler.id)
								return bowler
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.defaultOpponent(withId: uuid()))))
						return .none

					case .didTap:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: state.ordering)
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.details(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .errors(.internal), .errors(.view):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.details(.internal))),
						.destination(.presented(.details(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
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
			case .view(.didTapOpponent):
				return Analytics.Bowler.Viewed(kind: Bowler.Kind.opponent.rawValue)
			case .internal(.list(.delegate(.didDelete))):
				return Analytics.Bowler.Deleted(kind: Bowler.Kind.opponent.rawValue)
			default:
				return nil
			}
		}
	}
}

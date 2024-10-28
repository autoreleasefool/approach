import AnalyticsServiceInterface
import AssetsLibrary
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsLibrary
import ModelsLibrary
import OpponentDetailsFeature
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Bowler.Opponent: ResourceListItem {}

extension Bowler.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

@Reducer
public struct OpponentsList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var list: ResourceList<Bowler.Opponent, Bowler.Ordering>.State
		public var ordering: Bowler.Ordering = .byRecentlyUsed
		public let isOpponentDetailsEnabled: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: .byRecentlyUsed,
				listTitle: Strings.Opponent.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.opponents,
					title: Strings.Opponent.Error.Empty.title,
					message: Strings.Opponent.Error.Empty.message,
					action: Strings.Opponent.List.add
				)
			)

			@Dependency(\.featureFlags) var featureFlags
			self.isOpponentDetailsEnabled = featureFlags.isFlagEnabled(.opponentDetails)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapSortOrderButton
			case didTapOpponent(Bowler.ID)
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didLoadEditableOpponent(Result<Bowler.Edit, Error>)
			case didArchiveOpponent(Result<Bowler.Opponent, Error>)

			case list(ResourceList<Bowler.Opponent, Bowler.Ordering>.Action)
			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case details(OpponentDetails)
		case editor(BowlerEditor)
		case sortOrder(SortOrder<Bowler.Ordering>)
	}

	public enum ErrorID: Hashable {
		case opponentNotFound
		case failedToArchiveOpponent
	}

	public init() {}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(RecentlyUsedService.self) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.list, action: \.internal.list) {
			ResourceList { @Sendable in
				bowlers.opponents(ordering: $0)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case let .didTapOpponent(id):
					guard state.isOpponentDetailsEnabled, let opponent = state.list.findResource(byId: id) else { return .none }
					state.destination = .details(.init(opponent: opponent.summary))
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

				case .didArchiveOpponent(.success):
					return .none

				case let .didLoadEditableOpponent(.failure(error)):
					return state.errors
						.enqueue(.opponentNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didArchiveOpponent(.failure(error)):
					return state.errors
						.enqueue(.failedToArchiveOpponent, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(opponent):
						return .run { send in
							await send(.internal(.didLoadEditableOpponent(Result {
								try await bowlers.edit(opponent.id)
							})))
						}

					case let .didArchive(bowler):
						return .run { send in
							await send(.internal(.didArchiveOpponent(Result {
								try await bowlers.archive(bowler.id)
								return bowler
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.defaultOpponent(withId: uuid()))))
						return .none

					case .didTap, .didDelete, .didMove:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: state.ordering)
							.map { .internal(.list($0)) }
					}

				case .destination(.presented(.editor(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.details(.delegate(.doNothing)))):
					return .none

				case .errors(.delegate(.doNothing)):
					return .none

				case .list(.internal), .list(.view):
					return .none

				case .errors(.internal), .errors(.view):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.details(.internal))),
						.destination(.presented(.details(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapOpponent):
				return Analytics.Bowler.Viewed(kind: Bowler.Kind.opponent.rawValue)
			case .internal(.list(.delegate(.didArchive))):
				return Analytics.Bowler.Archived(kind: Bowler.Kind.opponent.rawValue)
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
			case let .internal(.didLoadEditableOpponent(.failure(error))),
				let .internal(.didArchiveOpponent(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

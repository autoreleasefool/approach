import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ScoreSheetFeature
import StringsLibrary

public struct Sharing: Reducer {
	public struct State: Equatable {
		public let dataSource: DataSource
		public var games: IdentifiedArrayOf<Game.Shareable> = []

		public var errors: Errors<ErrorID>.State = .init()

		public init(dataSource: DataSource) {
			self.dataSource = dataSource
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didFirstAppear
			case didTapShareButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadGames(TaskResult<[Game.Shareable]>)

			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum DataSource: Equatable {
		case games([Game.ID])
		case series(Series.ID)
	}

	public enum ErrorID: Hashable {
		case gamesNotFound
	}

	public init() {}

	@Dependency(\.games) var games

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .run { [dataSource = state.dataSource] send in
						await send(.internal(.didLoadGames(TaskResult {
							switch dataSource {
							case let .games(ids):
								return try await games.shareGames(ids)
							case let .series(id):
								return try await games.shareSeries(id)
							}
						})))
					}

				case .didTapShareButton:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadGames(.success(games)):
					state.games = .init(uniqueElements: games)
					return .none

				case let .didLoadGames(.failure(error)):
					return state.errors
						.enqueue(.gamesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

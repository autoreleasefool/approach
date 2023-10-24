import BowlersRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import FeatureActionLibrary
import GamesRepositoryInterface
import LeaguesRepositoryInterface
import ModelsLibrary
import SeriesRepositoryInterface
import StringsLibrary

public struct ArchiveList: Reducer {
	public struct State: Equatable {
		public var archivedBowlers: IdentifiedArrayOf<Bowler.Archived> = []
		public var archivedLeagues: IdentifiedArrayOf<League.Archived> = []
		public var archivedSeries: IdentifiedArrayOf<Series.Archived> = []
		public var archivedGames: IdentifiedArrayOf<Game.Archived> = []

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case observeData
			case didSwipeBowler(Bowler.Archived)
			case didSwipeLeague(League.Archived)
			case didSwipeSeries(Series.Archived)
			case didSwipeGame(Game.Archived)
			case alert(PresentationAction<AlertAction>)
		}

		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case bowlersResponse(TaskResult<[Bowler.Archived]>)
			case leaguesResponse(TaskResult<[League.Archived]>)
			case seriesResponse(TaskResult<[Series.Archived]>)
			case gamesResponse(TaskResult<[Game.Archived]>)

			case unarchivedBowler(TaskResult<Bowler.Archived>)
			case unarchivedLeague(TaskResult<League.Archived>)
			case unarchivedSeries(TaskResult<Series.Archived>)
			case unarchivedGame(TaskResult<Game.Archived>)

			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum AlertAction: Equatable {
		case didTapConfirm
	}

	public enum ErrorID: Hashable {
		case failedToLoadBowlers
		case failedToLoadLeagues
		case failedToLoadSeries
		case failedToLoadGames
		case failedToUnarchiveBowler
		case failedToUnarchiveLeague
		case failedToUnarchiveSeries
		case failedToUnarchiveGame
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.leagues) var leagues
	@Dependency(\.series) var series
	@Dependency(\.games) var games

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .observeData:
					return .merge(
						observeBowlers(),
						observeLeagues(),
						observeSeries(),
						observeGames()
					)

				case let .didSwipeBowler(bowler):
					return .run { send in
						await send(.internal(.unarchivedBowler(TaskResult {
							try await self.bowlers.unarchive(bowler.id)
							return bowler
						})))
					}

				case let .didSwipeLeague(league):
					return .run { send in
						await send(.internal(.unarchivedLeague(TaskResult {
							try await self.leagues.unarchive(league.id)
							return league
						})))
					}

				case let .didSwipeSeries(series):
					return .run { send in
						await send(.internal(.unarchivedSeries(TaskResult {
							try await self.series.unarchive(series.id)
							return series
						})))
					}

				case let .didSwipeGame(game):
					return .run { send in
						await send(.internal(.unarchivedGame(TaskResult {
							try await self.games.unarchive(game.id)
							return game
						})))
					}

				case let .alert(.presented(alertAction)):
					switch alertAction {
					case .didTapConfirm:
						state.alert = nil
						return .none
					}

				case .alert(.dismiss):
					state.alert = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .bowlersResponse(.success(bowlers)):
					state.archivedBowlers = .init(uniqueElements: bowlers)
					return .none

				case let .unarchivedBowler(.success(bowler)):
					state.alert = AlertState {
						TextState(Strings.Archive.Alert.unarchived(bowler.name))
					} message: {
						TextState(Strings.Archive.Alert.restoredBowler)
					}
					return .none

				case let .leaguesResponse(.success(leagues)):
					state.archivedLeagues = .init(uniqueElements: leagues)
					return .none

				case let .unarchivedLeague(.success(league)):
					state.alert = AlertState {
						TextState(Strings.Archive.Alert.unarchived(league.name))
					} message: {
						TextState(Strings.Archive.Alert.restoredLeague)
					}
					return .none

				case let .seriesResponse(.success(series)):
					state.archivedSeries = .init(uniqueElements: series)
					return .none

				case let .unarchivedSeries(.success(series)):
					state.alert = AlertState {
						TextState(Strings.Archive.Alert.unarchived(series.date.longFormat))
					} message: {
						TextState(Strings.Archive.Alert.restoredSeries)
					}
					return .none

				case let .gamesResponse(.success(games)):
					state.archivedGames = .init(uniqueElements: games)
					return .none

				case let .unarchivedGame(.success(game)):
					state.alert = AlertState {
						TextState(Strings.Archive.Alert.unarchivedGame(game.seriesDate.longFormat))
					} message: {
						TextState(Strings.Archive.Alert.restoredGame)
					}
					return .none

				case let .bowlersResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadBowlers, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .unarchivedBowler(.failure(error)):
					return state.errors
						.enqueue(.failedToUnarchiveBowler, thrownError: error, toastMessage: Strings.Error.Toast.failedToRestore)
						.map { .internal(.errors($0)) }

				case let .leaguesResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadLeagues, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .unarchivedLeague(.failure(error)):
					return state.errors
						.enqueue(.failedToUnarchiveLeague, thrownError: error, toastMessage: Strings.Error.Toast.failedToRestore)
						.map { .internal(.errors($0)) }

				case let .seriesResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadSeries, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .unarchivedSeries(.failure(error)):
					return state.errors
						.enqueue(.failedToUnarchiveSeries, thrownError: error, toastMessage: Strings.Error.Toast.failedToRestore)
						.map { .internal(.errors($0)) }

				case let .gamesResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadGames, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .unarchivedGame(.failure(error)):
					return state.errors
						.enqueue(.failedToUnarchiveGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToRestore)
						.map { .internal(.errors($0)) }

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .errors(.view), .errors(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func observeBowlers() -> Effect<Action> {
		.run { send in
			for try await bowlers in self.bowlers.archived() {
				await send(.internal(.bowlersResponse(.success(bowlers))))
			}
		} catch: { error, send in
			await send(.internal(.bowlersResponse(.failure(error))))
		}
	}

	private func observeLeagues() -> Effect<Action> {
		.run { send in
			for try await leagues in self.leagues.archived() {
				await send(.internal(.leaguesResponse(.success(leagues))))
			}
		} catch: { error, send in
			await send(.internal(.leaguesResponse(.failure(error))))
		}
	}

	private func observeSeries() -> Effect<Action> {
		.run { send in
			for try await series in self.series.archived() {
				await send(.internal(.seriesResponse(.success(series))))
			}
		} catch: { error, send in
			await send(.internal(.seriesResponse(.failure(error))))
		}
	}

	private func observeGames() -> Effect<Action> {
		.run { send in
			for try await games in self.games.archived() {
				await send(.internal(.gamesResponse(.success(games))))
			}
		} catch: { error, send in
			await send(.internal(.gamesResponse(.failure(error))))
		}
	}
}

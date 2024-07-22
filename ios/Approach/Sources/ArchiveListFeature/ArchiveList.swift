import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import GamesRepositoryInterface
import LeaguesRepositoryInterface
import ModelsLibrary
import SeriesRepositoryInterface
import StringsLibrary

public struct ArchiveItem: Identifiable, Equatable {
	public let id: ArchiveItemID
	public let title: String
	public let subtitle: String
	public let archivedOn: Date?
}

public enum ArchiveItemID: Hashable {
	case bowler(Bowler.ID)
	case league(League.ID)
	case series(Series.ID)
	case game(Game.ID)
}

@Reducer
public struct ArchiveList: Reducer, Sendable {

	@ObservableState
	public struct State: Equatable {
		public var archivedBowlers: [Bowler.Archived] = []
		public var archivedLeagues: [League.Archived] = []
		public var archivedSeries: [Series.Archived] = []
		public var archivedGames: [Game.Archived] = []

		public var archived: IdentifiedArrayOf<ArchiveItem> = []

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case observeData
			case didSwipe(ArchiveItem)
			case alert(PresentationAction<AlertAction>)
		}

		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case bowlersResponse(Result<[Bowler.Archived], Error>)
			case leaguesResponse(Result<[League.Archived], Error>)
			case seriesResponse(Result<[Series.Archived], Error>)
			case gamesResponse(Result<[Game.Archived], Error>)

			case unarchived(Result<ArchiveItem, Error>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum AlertAction: Equatable {
		case didTapConfirm
	}

	public enum ErrorID: Hashable {
		case failedToLoadBowlers
		case failedToLoadLeagues
		case failedToLoadSeries
		case failedToLoadGames
		case failedToUnarchive
	}

	public init() {}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(LeaguesRepository.self) var leagues
	@Dependency(SeriesRepository.self) var series
	@Dependency(GamesRepository.self) var games

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .observeData:
					return .merge(
						observeBowlers(),
						observeLeagues(),
						observeSeries(),
						observeGames()
					)

				case let .didSwipe(item):
					return .run { send in
						await send(.internal(.unarchived(Result {
							switch item.id {
							case let .bowler(bowlerId):
								try await self.bowlers.unarchive(bowlerId)
							case let .league(leagueId):
								try await self.leagues.unarchive(leagueId)
							case let .series(seriesId):
								try await self.series.unarchive(seriesId)
							case let .game(gameId):
								try await self.games.unarchive(gameId)
							}
							return item
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
				case let .unarchived(.success(item)):
					state.alert = AlertState {
						TextState(Strings.Archive.Alert.unarchived(item.title))
					} message: {
						switch item.id {
						case .bowler: return TextState(Strings.Archive.Alert.restoredBowler)
						case .league: return TextState(Strings.Archive.Alert.restoredLeague)
						case .series: return TextState(Strings.Archive.Alert.restoredSeries)
						case .game: return TextState(Strings.Archive.Alert.restoredGame)
						}
					}
					return .none

				case let .bowlersResponse(.success(bowlers)):
					state.archivedBowlers = bowlers
					state.updateItems()
					return .none

				case let .leaguesResponse(.success(leagues)):
					state.archivedLeagues = leagues
					state.updateItems()
					return .none

				case let .seriesResponse(.success(series)):
					state.archivedSeries = series
					state.updateItems()
					return .none

				case let .gamesResponse(.success(games)):
					state.archivedGames = games
					state.updateItems()
					return .none

				case let .unarchived(.failure(error)):
					return state.errors
						.enqueue(.failedToUnarchive, thrownError: error, toastMessage: Strings.Error.Toast.failedToRestore)
						.map { .internal(.errors($0)) }

				case let .bowlersResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadBowlers, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .leaguesResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadLeagues, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .seriesResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadSeries, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .gamesResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadGames, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)):
					return .none

				case .errors(.view), .errors(.internal):
					return .none
				}

			case .delegate:
				return .none
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
			case let .internal(.unarchived(.failure(error))),
				let .internal(.bowlersResponse(.failure(error))),
				let .internal(.leaguesResponse(.failure(error))),
				let .internal(.seriesResponse(.failure(error))),
				let .internal(.gamesResponse(.failure(error))):
				return error
			default:
				return nil
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

extension Game.ScoringMethod: CustomStringConvertible {
	public var description: String {
		switch self {
		case .manual: return Strings.Game.Editor.Fields.ScoringMethod.manual
		case .byFrame: return Strings.Game.Editor.Fields.ScoringMethod.byFrame
		}
	}
}

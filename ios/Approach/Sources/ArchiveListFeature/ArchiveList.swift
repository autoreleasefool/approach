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
public struct ArchiveList: Reducer {
	public struct State: Equatable {
		public var archivedBowlers: [Bowler.Archived] = []
		public var archivedLeagues: [League.Archived] = []
		public var archivedSeries: [Series.Archived] = []
		public var archivedGames: [Game.Archived] = []

		public var archived: IdentifiedArrayOf<ArchiveItem> = []

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case observeData
			case didSwipe(ArchiveItem)
			case alert(PresentationAction<AlertAction>)
		}

		public enum DelegateAction: Equatable { case doNothing }
		public enum InternalAction: Equatable {
			case bowlersResponse(TaskResult<[Bowler.Archived]>)
			case leaguesResponse(TaskResult<[League.Archived]>)
			case seriesResponse(TaskResult<[Series.Archived]>)
			case gamesResponse(TaskResult<[Game.Archived]>)

			case unarchived(TaskResult<ArchiveItem>)

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
		case failedToUnarchive
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
						await send(.internal(.unarchived(TaskResult {
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

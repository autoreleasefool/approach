import ComposableArchitecture
import GamesDataProviderInterface
import GameEditorFeature
import SharedModelsLibrary

public struct SeriesSidebar: ReducerProtocol {
	public struct State: Equatable {
		public var series: Series
		public var games: IdentifiedArrayOf<Game> = []
		public var selection: Identified<Game.ID, GameEditor.State>?

		public init(series: Series) {
			self.series = series
		}
	}

	public enum Action: Equatable {
		case subscribeToGames
		case gamesResponse(TaskResult<[Game]>)
		case setNavigation(selection: Game.ID?)
		case gameEditor(GameEditor.Action)
	}

	public init() {}

	@Dependency(\.gamesDataProvider) var gamesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToGames:
				return .run { [series = state.series.id] send in
					for try await games in gamesDataProvider.fetchGames(.init(series: series, ordering: .byOrdinal)) {
						await send(.gamesResponse(.success(games)))
					}
				} catch: { error, send in
					await send(.gamesResponse(.failure(error)))
				}

			case let .gamesResponse(.success(games)):
				state.games = .init(uniqueElements: games)
				return .none

			case .gamesResponse(.failure):
				// TODO: show games error
				return .none

			case let .setNavigation(selection: .some(id)):
				if let selection = state.games[id: id] {
					state.selection = Identified(.init(game: selection), id: id)
				}
				return .none

			case .setNavigation(selection: .none):
				state.selection = nil
				return .none

			case .gameEditor:
				return .none
			}
		}
		.ifLet(\.selection, action: /SeriesSidebar.Action.gameEditor) {
			Scope(state: \Identified<Game.ID, GameEditor.State>.value, action: /.self) {
				GameEditor()
			}
		}
	}
}

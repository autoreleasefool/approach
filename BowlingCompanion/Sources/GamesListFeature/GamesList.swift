import ComposableArchitecture
import GamesDataProviderInterface
import SharedModelsLibrary

public struct GamesList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Equatable {
		public var series: Series
		public var games: IdentifiedArrayOf<Game> = []

		public init(series: Series) {
			self.series = series
		}
	}

	public enum Action: Equatable {
		case onAppear
		case onDisappear
		case gamesResponse(TaskResult<[Game]>)
	}

	public init() {}

	@Dependency(\.gamesDataProvider) var gamesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .onAppear:
				return .run { [series = state.series] send in
					for await games in gamesDataProvider.fetchAll(series) {
						await send(.gamesResponse(.success(games)))
					}
				}
				.cancellable(id: ListObservable.self)

			case .onDisappear:
				// TODO: list observation doesn't cancel and leaks because store becomes nil before `onDisappear`
				return .cancel(id: ListObservable.self)

			case let .gamesResponse(.success(games)):
				state.games = .init(uniqueElements: games)
				return .none

			case .gamesResponse(.failure):
				// TODO: show error when games fail to load
				return .none
			}
		}
	}
}

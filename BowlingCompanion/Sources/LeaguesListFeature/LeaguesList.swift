import ComposableArchitecture
import LeaguesDataProviderInterface
import SharedModelsLibrary

public struct LeaguesList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Sendable, Equatable {
		public var bowler: Bowler
		public var leagues: IdentifiedArrayOf<League> = []

		public init(bowler: Bowler) {
			self.bowler = bowler
		}
	}

	public enum Action: Sendable, Equatable {
		case onAppear
		case onDisappear
		case leaguesResponse(TaskResult<[League]>)
		case setFormSheet(isPresented: Bool)
	}

	public init() {}

	@Dependency(\.leaguesDataProvider) var leaguesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .onAppear:
				return .run { [bowler = state.bowler] send in
					for await leagues in leaguesDataProvider.fetchAll(bowler) {
						await send(.leaguesResponse(.success(leagues)))
					}
				}
				.cancellable(id: ListObservable.self)

			case .onDisappear:
				return .cancel(id: ListObservable.self)

			case let .leaguesResponse(.success(leagues)):
				state.leagues = .init(uniqueElements: leagues)
				return .none

			case .leaguesResponse(.failure):
				return .none

			case .setFormSheet(isPresented: true):
				// TODO: show create league form
				return .none

			case .setFormSheet(isPresented: false):
				// TODO: hide create league form
				return .none
			}
		}
	}
}

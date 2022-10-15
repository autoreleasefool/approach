import ComposableArchitecture
import LeagueFormFeature
import LeaguesDataProviderInterface
import SharedModelsLibrary

public struct LeaguesList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Sendable, Equatable {
		public var bowler: Bowler
		public var leagues: IdentifiedArrayOf<League> = []
		public var leagueForm: LeagueForm.State?

		public init(bowler: Bowler) {
			self.bowler = bowler
		}
	}

	public enum Action: Sendable, Equatable {
		case onAppear
		case onDisappear
		case leaguesResponse(TaskResult<[League]>)
		case setFormSheet(isPresented: Bool)
		case leagueForm(LeagueForm.Action)
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
				// TODO: list observation doesn't cancel and leaks because store becomes nil before `onDisappear`
				return .cancel(id: ListObservable.self)

			case let .leaguesResponse(.success(leagues)):
				state.leagues = .init(uniqueElements: leagues)
				return .none

			case .leaguesResponse(.failure):
				return .none

			case .setFormSheet(isPresented: true):
				state.leagueForm = .init(bowler: state.bowler, mode: .create)
				return .none

			case .setFormSheet(isPresented: false):
				state.leagueForm = nil
				return .none

			case .leagueForm(.saveLeagueResult(.success)):
				state.leagueForm = nil
				return .none

			case .leagueForm:
				return .none
			}
		}
		.ifLet(\.leagueForm, action: /LeaguesList.Action.leagueForm) {
			LeagueForm()
		}
	}
}

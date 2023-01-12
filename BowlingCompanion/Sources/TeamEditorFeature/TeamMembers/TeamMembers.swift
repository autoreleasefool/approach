import BowlersDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary
import TeamsDataProviderInterface

public struct TeamMembers: ReducerProtocol {
	public struct State: Equatable {
		public let team: Team?
		public var isLoadingInitialData = true
		public var bowlers: IdentifiedArrayOf<Bowler> = []

		public init(team: Team?) {
			self.team = team
		}
	}

	public enum Action: Equatable {
		case refreshData
		case bowlersResponse(TaskResult<[Bowler]>)
	}

	public init() {}

	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.teamsDataProvider) var teamsDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .refreshData:
				return .task { [team = state.team] in
					if let team {
						return await .bowlersResponse(TaskResult {
							try await bowlersDataProvider.fetchBowlers(.init(filter: .team(team), ordering: .byName))
						})
					} else {
						return .bowlersResponse(.success([]))
					}
				}

			case let .bowlersResponse(.success(bowlers)):
				state.bowlers = .init(uniqueElements: bowlers)
				state.isLoadingInitialData = false
				return .none

			case .bowlersResponse(.failure):
				// TODO: handle failure loading bowlers
				state.isLoadingInitialData = false
				return .none
			}
		}
	}
}

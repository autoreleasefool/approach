import ComposableArchitecture
import SharedModelsLibrary
import TeamsDataProviderInterface

public struct TeamMembers: ReducerProtocol {
	public struct State: Equatable {
		public let team: Team.ID?
		public var isLoadingInitialData = true
		public var bowlers: IdentifiedArrayOf<Bowler> = []

		public init(team: Team.ID?) {
			self.team = team
		}
	}

	public enum Action: Equatable {
		case refreshData
		case teamMembershipResponse(TaskResult<TeamMembership>)
	}

	public init() {}

	@Dependency(\.teamsDataProvider) var teamsDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .refreshData:
				if let team = state.team {
					return .task {
						await .teamMembershipResponse(TaskResult {
							try await teamsDataProvider.fetchTeamMembers(.init(filter: .id(team), ordering: .byName))
						})
					}
				}

				state.isLoadingInitialData = false
				return .none

			case let .teamMembershipResponse(.success(membership)):
				state.bowlers = .init(uniqueElements: membership.members)
				state.isLoadingInitialData = false
				return .none

			case .teamMembershipResponse(.failure):
				// TODO: handle failure loading bowlers
				state.isLoadingInitialData = false
				return .none
			}
		}
	}
}

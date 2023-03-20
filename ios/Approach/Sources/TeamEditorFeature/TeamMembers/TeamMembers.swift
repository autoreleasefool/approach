import BowlersDataProviderInterface
import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary
import TeamsDataProviderInterface

public struct TeamMembers: Reducer {
	public struct State: Equatable {
		public let team: Team?
		public var bowlers: IdentifiedArrayOf<Bowler>?

		public init(team: Team?) {
			self.team = team
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadBowlers(TaskResult<[Bowler]>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.teamsDataProvider) var teamsDataProvider

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .task { [team = state.team] in
						if let team {
							return await .internal(.didLoadBowlers(TaskResult {
								try await bowlersDataProvider.fetchBowlers(.init(filter: .team(team), ordering: .byName))
							}))
						} else {
							return .internal(.didLoadBowlers(.success([])))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadBowlers(.success(bowlers)):
					state.bowlers = .init(uniqueElements: bowlers)
					return .none

				case .didLoadBowlers(.failure):
					// TODO: handle failure loading bowlers
					state.bowlers = []
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

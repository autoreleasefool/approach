import BowlersListFeature
import ComposableArchitecture
import StringsLibrary
import TeamsListFeature

public struct TeamsAndBowlersList: ReducerProtocol {
	public struct State: Equatable {
		public var selectedTab: Tab = .bowlers
		public var bowlersList: BowlersList.State = .init()
		public var teamsList: TeamsList.State = .init()

		var bowlersListSelected: BowlersList.State? {
			selectedTab == .bowlers ? bowlersList : nil
		}

		var teamsListSelected: TeamsList.State? {
			selectedTab == .teams ? teamsList : nil
		}

		public init() {}
	}

	public enum Action: Equatable {
		case tabPicked(tab: Tab)
		case bowlersList(BowlersList.Action)
		case teamsList(TeamsList.Action)
	}

	public enum Tab: CaseIterable, Hashable, CustomStringConvertible {
		case bowlers
		case teams

		public var description: String {
			switch self {
			case .teams: return Strings.Team.List.title
			case .bowlers: return Strings.Bowler.List.title
			}
		}
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.bowlersList, action: /Action.bowlersList) {
			BowlersList()
		}

		Scope(state: \.teamsList, action: /Action.teamsList) {
			TeamsList()
		}

		Reduce { state, action in
			switch action {
			case let .tabPicked(tab):
				state.selectedTab = tab
				return .none

				// TODO: use proper delegate destructuring
			case .teamsList, .bowlersList:
				return .none
			}
		}
	}
}

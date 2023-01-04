import ComposableArchitecture
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SortOrderLibrary
import TeamsDataProviderInterface
import TeamEditorFeature
import ViewsLibrary

public struct TeamsList: ReducerProtocol {
	public struct State: Equatable {
		public var teams: IdentifiedArrayOf<Team>?
		public var sortOrder: SortOrder<Team.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed)
		public var error: ListErrorContent?
		public var teamEditor: TeamEditor.State?

		public init() {}
	}

	public enum Action: Equatable {
		case observeTeams
		case errorButtonTapped
		case teamsResponse(TaskResult<[Team]>)
		case editTeamLoadResponse(TaskResult<EditTeamLoadResult>)
		case swipeAction(Team, SwipeAction)
		case sortOrder(SortOrder<Team.FetchRequest.Ordering>.Action)
		case setNavigation(selection: Team.ID?)
		case setEditorFormSheet(isPresented: Bool)
		case teamEditor(TeamEditor.Action)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public struct EditTeamLoadResult: Equatable {
		let team: Team
		let membership: TeamMembership
	}

	struct ObservationCancellable {}
	struct EditTeamCancellable {}

	public init() {}

	@Dependency(\.teamsDataProvider) var teamsDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.sortOrder, action: /TeamsList.Action.sortOrder) {
			SortOrder()
		}

		Reduce { state, action in
			switch action {
			case .observeTeams:
				state.error = nil
				return .run { [ordering = state.sortOrder.ordering] send in
					for try await teams in teamsDataProvider.observeTeams(.init(filter: nil, ordering: ordering)) {
						await send(.teamsResponse(.success(teams)))
					}
				} catch: { error, send in
					await send(.teamsResponse(.failure(error)))
				}
				.cancellable(id: ObservationCancellable.self, cancelInFlight: true)

			case .errorButtonTapped:
				// TODO: handle error button tapped
				return .none

			case let .teamsResponse(.success(teams)):
				state.teams = .init(uniqueElements: teams)
				return .none

			case .teamsResponse(.failure):
				state.error = .loadError
				return .none

			case .setNavigation:
				// TODO: navigate to team
				return .none

			case .setEditorFormSheet(isPresented: true):
				state.teamEditor = .init(mode: .create, membership: nil)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.teamEditor(.form(.didFinishSaving)),
					.teamEditor(.form(.didFinishDeleting)),
					.teamEditor(.form(.alert(.discardButtonTapped))):
				state.teamEditor = nil
				return .none

			case let .swipeAction(team, .edit):
				return .task { [team = team] in
					await .editTeamLoadResponse(TaskResult {
						try await .init(
							team: team,
							membership: teamsDataProvider.fetchTeamMembers(.init(filter: .id(team.id), ordering: .byName))
						)
					})
				}
				.cancellable(id: EditTeamCancellable.self, cancelInFlight: true)

			case let .editTeamLoadResponse(.success(editTeam)):
				state.teamEditor = .init(mode: .edit(editTeam.team), membership: editTeam.membership)
				return .none

			case .editTeamLoadResponse(.failure):
				// TODO: handle failed to load team members error
				return .none

			case .swipeAction(_, .delete):
				// TODO: open team delete prompt
				return .none

			case .sortOrder(.optionTapped):
				return .task { .observeTeams }

			case .sortOrder, .teamEditor:
				return .none
			}
		}
		.ifLet(\.teamEditor, action: /TeamsList.Action.teamEditor) {
			TeamEditor()
		}
	}
}

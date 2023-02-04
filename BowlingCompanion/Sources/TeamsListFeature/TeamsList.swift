import BowlersDataProviderInterface
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
		public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: Equatable {
		case observeTeams
		case errorButtonTapped
		case teamsResponse(TaskResult<[Team]>)
		case editTeamLoadResponse(TaskResult<EditTeamLoadResult>)
		case deleteTeamResponse(TaskResult<Bool>)
		case swipeAction(Team, SwipeAction)
		case sortOrder(SortOrder<Team.FetchRequest.Ordering>.Action)
		case setNavigation(selection: Team.ID?)
		case setEditorFormSheet(isPresented: Bool)
		case teamEditor(TeamEditor.Action)
		case alert(AlertAction)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public struct EditTeamLoadResult: Equatable {
		let team: Team
		let bowlers: [Bowler]
	}

	struct ObservationCancellable {}
	struct EditTeamCancellable {}

	public init() {}

	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.teamsDataProvider) var teamsDataProvider
	@Dependency(\.persistenceService) var persistenceService

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
				state.teamEditor = .init(mode: .create, bowlers: [])
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
							bowlers: bowlersDataProvider.fetchBowlers(.init(filter: .team(team), ordering: .byName))
						)
					})
				}
				.cancellable(id: EditTeamCancellable.self, cancelInFlight: true)

			case let .editTeamLoadResponse(.success(editTeam)):
				state.teamEditor = .init(mode: .edit(editTeam.team), bowlers: editTeam.bowlers)
				return .none

			case .editTeamLoadResponse(.failure):
				// TODO: handle failed to load team members error
				return .none

			case let .swipeAction(team, .delete):
				state.alert = TeamsList.alert(toDelete: team)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case let .alert(.deleteButtonTapped(team)):
				return .task {
					return await .deleteTeamResponse(TaskResult {
						try await persistenceService.deleteTeam(team)
						return true
					})
				}

			case .deleteTeamResponse(.success):
				return .none

			case .deleteTeamResponse(.failure):
				state.error = .deleteError
				return .none

			case let .sortOrder(.delegate(delegateAction)):
				switch delegateAction {
				case .didTapOption:
					return .task { .observeTeams }
				}

			case .sortOrder(.internal), .sortOrder(.view), .teamEditor:
				return .none
			}
		}
		.ifLet(\.teamEditor, action: /TeamsList.Action.teamEditor) {
			TeamEditor()
		}
	}
}

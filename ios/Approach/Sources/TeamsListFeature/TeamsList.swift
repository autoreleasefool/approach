import BowlersDataProviderInterface
import ComposableArchitecture
import FeatureActionLibrary
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SharedModelsLibrary
import SortOrderLibrary
import StringsLibrary
import TeamsDataProviderInterface
import TeamEditorFeature
import ViewsLibrary

extension Team: ResourceListItem {}

public struct TeamsList: ReducerProtocol {
	public struct State: Equatable {
		public var list: ResourceList<Team, Team.FetchRequest>.State
		public var sortOrder: SortOrder<Team.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed)
		public var editor: TeamEditor.State?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.persistenceService) var persistenceService: PersistenceService
						try await persistenceService.deleteTeam($0)
					}),
				],
				query: .init(filter: nil, ordering: sortOrder.ordering),
				listTitle: Strings.Team.List.title,
				emptyContent: .init(
					image: .emptyTeams,
					title: Strings.Team.Error.Empty.title,
					message: Strings.Team.Error.Empty.message,
					action: Strings.Team.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setNavigation(selection: Team.ID?)
			case setEditorSheet(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadTeamToEdit(TaskResult<EditTeamLoadResult>)
			case list(ResourceList<Team, Team.FetchRequest>.Action)
			case sortOrder(SortOrder<Team.FetchRequest.Ordering>.Action)
			case editor(TeamEditor.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct EditTeamLoadResult: Equatable {
		let team: Team
		let bowlers: [Bowler]
	}

	struct EditTeamCancellable {}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.teamsDataProvider) var teamsDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.sortOrder, action: /Action.internal..Action.InternalAction.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: teamsDataProvider.observeTeams)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, state: &state)

				case .setEditorSheet(isPresented: true):
					state.editor = .init(mode: .create, bowlers: [])
					return .none

				case .setEditorSheet(isPresented: false):
					state.editor = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadTeamToEdit(.success(teamToEdit)):
					state.editor = .init(mode: .edit(teamToEdit.team), bowlers: teamToEdit.bowlers)
					return .none

				case .didLoadTeamToEdit(.failure):
					// TODO: handle failed to load team members
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(team):
						return .task { [team = team] in
							await .internal(.didLoadTeamToEdit(TaskResult {
								try await .init(
									team: team,
									bowlers: bowlersDataProvider.fetchBowlers(.init(filter: .team(team), ordering: .byName))
								)
							}))
						}
						.cancellable(id: EditTeamCancellable.self, cancelInFlight: true)

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(mode: .create, bowlers: [])
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .editor(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.editor = nil
						return .none
					}

				case let .sortOrder(.delegate(delegateAction)):
					switch delegateAction {
					case .didTapOption:
						state.updateQuery()
						return .task { .internal(.list(.callback(.shouldRefreshData))) }
					}

				case .list(.internal), .list(.view), .list(.callback):
					return .none

				case .sortOrder(.internal), .sortOrder(.view):
					return .none

				case .editor(.internal), .editor(.binding), .editor(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			TeamEditor()
		}
	}

	private func navigate(to id: Team.ID?, state: inout State) -> EffectTask<Action> {
		// TODO: navigate to team details
		if let id {
			return .fireAndForget {
				try await clock.sleep(for: .seconds(1))
				recentlyUsedService.didRecentlyUseResource(.teams, id)
			}
		}

		return .none
	}
}

extension TeamsList.State {
	mutating func updateQuery() {
		list.query = .init(filter: nil, ordering: sortOrder.ordering)
	}
}

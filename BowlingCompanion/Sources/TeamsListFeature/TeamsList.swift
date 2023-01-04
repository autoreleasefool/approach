import ComposableArchitecture
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SortOrderLibrary
import TeamsDataProviderInterface
import ViewsLibrary

public struct TeamsList: ReducerProtocol {
	public struct State: Equatable {
		public var teams: IdentifiedArrayOf<Team>?
		public var sortOrder: SortOrder<Team.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed)
		public var error: ListErrorContent?

		public init() {}
	}

	public enum Action: Equatable {
		case observeTeams
		case errorButtonTapped
		case teamsResponse(TaskResult<[Team]>)
		case swipeAction(Team, SwipeAction)
		case sortOrder(SortOrder<Team.FetchRequest.Ordering>.Action)
		case setNavigation(selection: Team.ID?)
		case setEditorFormSheet(isPresented: Bool)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	struct ObservationCancellable {}

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
				// TODO: show editor
				return .none

			case .setEditorFormSheet(isPresented: false):
				// TODO: hide editor
				return .none

			case .swipeAction(_, .edit):
				// TODO: open team editor
				return .none

			case .swipeAction(_, .delete):
				// TODO: open team delete prompt
				return .none

			case .sortOrder(.optionTapped):
				return .task { .observeTeams }

			case .sortOrder:
				return .none
			}
		}
	}
}

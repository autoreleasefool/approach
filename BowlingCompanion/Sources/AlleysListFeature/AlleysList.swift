import ComposableArchitecture
import PersistenceServiceInterface
import SharedModelsLibrary

public struct AlleysList: ReducerProtocol {
	public struct State: Equatable {
		public var alleys: IdentifiedArrayOf<Alley> = []
		public init() {}
	}

	public enum Action: Equatable {
		case subscribeToAlleys
		case alleysResponse(TaskResult<[Alley]>)
		case swipeAction(Alley, SwipeAction)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToAlleys:
				return .run { send in
					for try await alleys in persistenceService.fetchAlleys(.init(ordering: .byLastModified)) {
						await send(.alleysResponse(.success(alleys)))
					}
				} catch: { error, send in
					await send(.alleysResponse(.failure(error)))
				}

			case let .alleysResponse(.success(alleys)):
				state.alleys = .init(uniqueElements: alleys)
				return .none

			case .alleysResponse(.failure):
				// TODO: handle failed alley response
				return .none

			case .swipeAction(_, .edit):
				// TODO: present alley editor
				return .none

			case .swipeAction(_, .delete):
				// TODO: present alley delete form
				return .none
			}
		}
	}
}

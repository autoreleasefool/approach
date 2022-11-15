import GearDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary

public struct GearList: ReducerProtocol {
	public struct State: Equatable {
		public var gear: IdentifiedArrayOf<Gear> = []
		public init() {}
	}

	public enum Action: Equatable {
		case subscribeToGear
		case gearResponse(TaskResult<[Gear]>)
		case swipeAction(Gear, SwipeAction)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public init() {}

	@Dependency(\.gearDataProvider) var gearDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToGear:
				return .run { send in
					for try await gear in gearDataProvider.fetchGear(.init(ordering: .byRecentlyUsed)) {
						await send(.gearResponse(.success(gear)))
					}
				} catch: { error, send in
					await send(.gearResponse(.failure(error)))
				}

			case let .gearResponse(.success(gear)):
				state.gear = .init(uniqueElements: gear)
				return .none

			case .gearResponse(.failure):
				// TODO: handle failed gear response
				return .none

			case .swipeAction(_, .edit):
				// TODO: present gear editor
				return .none

			case .swipeAction(_, .delete):
				// TODO: present gear delete form
				return .none
			}
		}
	}
}

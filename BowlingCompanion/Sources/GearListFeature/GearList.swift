import GearDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary
import ViewsLibrary

public struct GearList: ReducerProtocol {
	public struct State: Equatable {
		public var gear: IdentifiedArrayOf<Gear>?
		public var error: ListErrorContent?

		public init() {}
	}

	public enum Action: Equatable {
		case refreshList
		case errorButtonTapped
		case gearResponse(TaskResult<[Gear]>)
		case swipeAction(Gear, SwipeAction)
		case setEditorFormSheet(isPresented: Bool)
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
			case .refreshList:
				state.error = nil
				return .task {
					await .gearResponse(TaskResult {
						try await gearDataProvider.fetchGear(.init(ordering: .byRecentlyUsed))
					})
				}

			case .setEditorFormSheet(isPresented: true):
				// TODO: show editor
				return .none

			case .setEditorFormSheet(isPresented: false):
				// TODO: hide editor
				return .none

			case .errorButtonTapped:
				return .task { .refreshList }

			case let .gearResponse(.success(gear)):
				state.gear = .init(uniqueElements: gear)
				return .none

			case .gearResponse(.failure):
				state.error = .loadError
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

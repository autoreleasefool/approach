import GearDataProviderInterface
import GearEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import ViewsLibrary

public struct GearList: ReducerProtocol {
	public struct State: Equatable {
		public var gear: IdentifiedArrayOf<Gear>?
		public var error: ListErrorContent?
		public var gearEditor: GearEditor.State?

		public init() {}
	}

	public enum Action: Equatable {
		case refreshList
		case errorButtonTapped
		case gearResponse(TaskResult<[Gear]>)
		case swipeAction(Gear, SwipeAction)
		case setEditorFormSheet(isPresented: Bool)
		case gearEditor(GearEditor.Action)
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
				state.gearEditor = .init(mode: .create)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.gearEditor(.form(.saveResult(.success))),
					.gearEditor(.form(.deleteResult(.success))),
					.gearEditor(.form(.alert(.discardButtonTapped))):
				state.gearEditor = nil
				return .task { .refreshList }

			case .errorButtonTapped:
				return .task { .refreshList }

			case let .gearResponse(.success(gear)):
				state.gear = .init(uniqueElements: gear)
				return .none

			case .gearResponse(.failure):
				state.error = .loadError
				return .none

			case let .swipeAction(gear, .edit):
				state.gearEditor = .init(mode: .edit(gear))
				return .none

			case .swipeAction(_, .delete):
				// TODO: present gear delete form
				return .none

			case .gearEditor:
				return .none
			}
		}
		.ifLet(\.gearEditor, action: /GearList.Action.gearEditor) {
			GearEditor()
		}
	}
}

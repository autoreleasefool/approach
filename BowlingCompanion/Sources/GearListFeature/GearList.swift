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
		case observeGear
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

	struct ObservationCancellable {}

	public init() {}

	@Dependency(\.gearDataProvider) var gearDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .observeGear:
				state.error = nil
				return .run { send in
					for try await gear in gearDataProvider.observeGear(.init(ordering: .byRecentlyUsed)) {
						await send(.gearResponse(.success(gear)))
					}
				} catch: { error, send in
					await send(.gearResponse(.failure(error)))
				}
				.cancellable(id: ObservationCancellable.self, cancelInFlight: true)

			case .setEditorFormSheet(isPresented: true):
				state.gearEditor = .init(mode: .create)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.gearEditor(.form(.didFinishSaving)),
					.gearEditor(.form(.didFinishDeleting)),
					.gearEditor(.form(.alert(.discardButtonTapped))):
				state.gearEditor = nil
				return .none

			case .errorButtonTapped:
				// TODO: handle error button tapped
				return .none

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

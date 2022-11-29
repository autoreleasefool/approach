import AlleysDataProviderInterface
import AlleyEditorFeature
import ComposableArchitecture
import PersistenceServiceInterface
import SharedModelsLibrary
import ViewsLibrary

public struct AlleysList: ReducerProtocol {
	public struct State: Equatable {
		public var alleys: IdentifiedArrayOf<Alley>?
		public var error: ListErrorContent?
		public var alleyEditor: AlleyEditor.State?
		public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: Equatable {
		case refreshList
		case errorButtonTapped
		case swipeAction(Alley, SwipeAction)
		case alleysResponse(TaskResult<[Alley]>)
		case deleteAlleyResponse(TaskResult<Bool>)
		case alert(AlertAction)
		case setEditorFormSheet(isPresented: Bool)
		case alleyEditor(AlleyEditor.Action)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .refreshList:
				state.error = nil
				return .task {
					await .alleysResponse(TaskResult { try await alleysDataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed)) })
				}

			case .errorButtonTapped:
				return .task { .refreshList }

			case let .alleysResponse(.success(alleys)):
				state.alleys = .init(uniqueElements: alleys)
				return .none

			case .alleysResponse(.failure):
				state.error = .loadError
				return .none

			case let .swipeAction(alley, .edit):
				state.alleyEditor = .init(mode: .edit(alley))
				return .none

			case let .swipeAction(alley, .delete):
				state.alert = AlleysList.alert(toDelete: alley)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case let .alert(.deleteButtonTapped(alley)):
				return .task {
					return await .deleteAlleyResponse(TaskResult {
						try await persistenceService.deleteAlley(alley)
						return true
					})
				}

			case .deleteAlleyResponse(.failure):
				state.error = .deleteError
				return .none

			case .setEditorFormSheet(isPresented: true):
				state.alleyEditor = .init(mode: .create)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.alleyEditor(.form(.saveResult(.success))),
					.alleyEditor(.form(.deleteResult(.success))):
				state.alleyEditor = nil
				return .none

			case .alleyEditor, .deleteAlleyResponse(.success):
				return .none
			}
		}
		.ifLet(\.alleyEditor, action: /AlleysList.Action.alleyEditor) {
			AlleyEditor()
		}
	}
}

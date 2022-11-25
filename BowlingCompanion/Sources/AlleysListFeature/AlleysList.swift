import AlleysDataProviderInterface
import AlleyEditorFeature
import ComposableArchitecture
import PersistenceServiceInterface
import SharedModelsLibrary

public struct AlleysList: ReducerProtocol {
	public struct State: Equatable {
		public var alleys: IdentifiedArrayOf<Alley>?
		public var error: ErrorContent?
		public var alleyEditor: AlleyEditor.State?
		public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: Equatable {
		case subscribeToAlleys
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

	public struct ErrorContent: Equatable {
		let title: String
		let message: String?
		let action: String

		static let loadError = Self(
			title: "Something went wrong!",
			message: "We couldn't load your data",
			action: "Try again"
		)

		static let deleteError = Self(
			title: "Something went wrong!",
			message: nil,
			action: "Reload"
		)
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToAlleys:
				state.error = nil

				return .run { send in
					for try await alleys in alleysDataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed)) {
						await send(.alleysResponse(.success(alleys)))
					}
				} catch: { error, send in
					await send(.alleysResponse(.failure(error)))
				}

			case .errorButtonTapped:
				// TODO: handle error button tapped
				return .none

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

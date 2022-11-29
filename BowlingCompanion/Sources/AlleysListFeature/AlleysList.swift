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
		public var isAlleyFiltersPresented = false
		public var alleyFilters: AlleysFilter.State = .init()

		public init() {}
	}

	public enum Action: Equatable {
		case refreshList
		case errorButtonTapped
		case swipeAction(Alley, SwipeAction)
		case alleysResponse(TaskResult<[Alley]>)
		case deleteAlleyResponse(TaskResult<Bool>)
		case alert(AlertAction)
		case setFilterSheet(isPresented: Bool)
		case setEditorFormSheet(isPresented: Bool)
		case alleyEditor(AlleyEditor.Action)
		case alleysFilter(AlleysFilter.Action)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.alleyFilters, action: /AlleysList.Action.alleysFilter) {
			AlleysFilter()
		}

		Reduce { state, action in
			switch action {
			case .refreshList:
				state.error = nil
				return .task { [filters = state.alleyFilters.filters] in
					await .alleysResponse(TaskResult {
						try await alleysDataProvider.fetchAlleys(.init(filter: filters, ordering: .byRecentlyUsed))
					})
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

			case .deleteAlleyResponse(.success):
				return .task { .refreshList }

			case .deleteAlleyResponse(.failure):
				state.error = .deleteError
				return .none

			case .setFilterSheet(isPresented: true):
				state.isAlleyFiltersPresented = true
				return .none

			case .setFilterSheet(isPresented: false), .alleysFilter(.applyButtonTapped):
				state.isAlleyFiltersPresented = false
				return .task { .refreshList }

			case .setEditorFormSheet(isPresented: true):
				state.alleyEditor = .init(mode: .create)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.alleyEditor(.form(.saveResult(.success))),
					.alleyEditor(.form(.deleteResult(.success))),
					.alleyEditor(.form(.alert(.discardButtonTapped))):
				state.alleyEditor = nil
				return .task { .refreshList }

			case .alleysFilter(.binding):
				return .task { .refreshList }

			case .alleyEditor, .alleysFilter:
				return .none
			}
		}
		.ifLet(\.alleyEditor, action: /AlleysList.Action.alleyEditor) {
			AlleyEditor()
		}
	}
}

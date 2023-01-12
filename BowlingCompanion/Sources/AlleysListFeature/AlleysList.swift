import AlleysDataProviderInterface
import AlleyEditorFeature
import ComposableArchitecture
import FeatureFlagsServiceInterface
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
		case observeAlleys
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

	struct ObservationCancellable {}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.alleyFilters, action: /AlleysList.Action.alleysFilter) {
			AlleysFilter()
		}

		Reduce { state, action in
			switch action {
			case .observeAlleys:
				state.error = nil
				return .run { [filter = state.alleyFilters.filter] send in
					for try await alleys in alleysDataProvider.observeAlleys(.init(filter: filter, ordering: .byRecentlyUsed)) {
						await send(.alleysResponse(.success(alleys)))
					}
				} catch: { error, send in
					await send(.alleysResponse(.failure(error)))
				}
				.cancellable(id: ObservationCancellable.self, cancelInFlight: true)

			case let .alleysResponse(.success(alleys)):
				state.alleys = .init(uniqueElements: alleys)
				return .none

			case .alleysResponse(.failure):
				state.error = .loadError
				return .none

			case let .swipeAction(alley, .edit):
				state.alleyEditor = .init(
					mode: .edit(alley),
					hasLanesEnabled: featureFlags.isEnabled(.lanes)
				)
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

			case .setFilterSheet(isPresented: true):
				state.isAlleyFiltersPresented = true
				return .none

			case .setFilterSheet(isPresented: false), .alleysFilter(.applyButtonTapped):
				state.isAlleyFiltersPresented = false
				return .task { .observeAlleys }

			case .alleysFilter(.binding):
				return .task { .observeAlleys }

			case .setEditorFormSheet(isPresented: true):
				state.alleyEditor = .init(
					mode: .create,
					hasLanesEnabled: featureFlags.isEnabled(.lanes)
				)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.alleyEditor(.form(.didFinishSaving)),
					.alleyEditor(.form(.didFinishDeleting)),
					.alleyEditor(.form(.alert(.discardButtonTapped))):
				state.alleyEditor = nil
				return .none

			case .alleyEditor, .alleysFilter, .errorButtonTapped, .deleteAlleyResponse(.success):
				return .none
			}
		}
		.ifLet(\.alleyEditor, action: /AlleysList.Action.alleyEditor) {
			AlleyEditor()
		}
	}
}

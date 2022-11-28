import ComposableArchitecture
import ViewsLibrary

public protocol PickableResource: Equatable, Identifiable {
	static var pickableModelName: String { get }
	var pickableTitle: String { get }
	var pickableSubtitle: String? { get }
}

public struct ResourcePicker<Resource: PickableResource>: ReducerProtocol {
	public struct State: Equatable {
		public var resources: IdentifiedArrayOf<Resource>?
		public var selected: Set<Resource.ID>
		public var initialSelection: Set<Resource.ID>
		public var limit: Int
		public var error: ListErrorContent?

		public init(selected: Set<Resource.ID>, limit: Int = 0) {
			self.selected = selected
			self.initialSelection = selected
			self.limit = limit
		}
	}

	public enum Action: Equatable {
		case subscribeToResources
		case cancelButtonTapped
		case saveButtonTapped
		case resourceTapped(Resource)
		case resources(TaskResult<[Resource]>)
	}

	public init(fetchResources: @escaping () -> AsyncThrowingStream<[Resource], Error>) {
		self.fetchResources = fetchResources
	}

	let fetchResources: () -> AsyncThrowingStream<[Resource], Error>

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToResources:
				state.initialSelection = state.selected
				state.error = nil
				return .run { send in
					for try await resources in fetchResources() {
						await send(.resources(.success(resources)))
					}
				} catch: { error, send in
					await send(.resources(.failure(error)))
				}

			case let .resources(.success(resources)):
				state.resources = .init(uniqueElements: resources)
				return .none

			case .resources(.failure):
				state.error = .loadError
				return .none

			case let .resourceTapped(resource):
				if state.selected.contains(resource.id) {
					state.selected.remove(resource.id)
				} else if state.limit == 1 {
					state.selected.removeAll()
					state.selected.insert(resource.id)
					return .task { .saveButtonTapped }
				} else if state.selected.count < state.limit || state.limit <= 0 {
					state.selected.insert(resource.id)
				}
				return .none

			case .cancelButtonTapped:
				state.selected = state.initialSelection
				return .none

			case .saveButtonTapped:
				return .none
			}
		}
	}
}

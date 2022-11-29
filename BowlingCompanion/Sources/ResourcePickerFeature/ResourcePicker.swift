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

		public mutating func updateInitialSelection() {
			self.initialSelection = selected
		}
	}

	public enum Action: Equatable {
		case refreshData
		case cancelButtonTapped
		case saveButtonTapped
		case resourceTapped(Resource)
		case resources(TaskResult<[Resource]>)
	}

	public init(fetchResources: @escaping () async throws -> [Resource]) {
		self.fetchResources = fetchResources
	}

	let fetchResources: () async throws -> [Resource]

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .refreshData:
				state.error = nil
				return .task {
					await .resources(TaskResult { try await fetchResources() })
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
				state.updateInitialSelection()
				return .none
			}
		}
	}
}

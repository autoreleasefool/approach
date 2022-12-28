import ComposableArchitecture
import LanesDataProviderInterface
import SharedModelsLibrary

public struct AlleyLanes: ReducerProtocol {
	public struct State: Equatable {
		public let alley: Alley.ID?
		public var isLoadingInitialData = true
		public var lanes: IdentifiedArrayOf<Lane> = []

		public init(alley: Alley.ID?) {
			self.alley = alley
		}
	}

	public enum Action: Equatable {
		case refreshData
		case lanesResponse(TaskResult<[Lane]>)
	}

	public init() {}

	@Dependency(\.lanesDataProvider) var lanesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .refreshData:
				if let alley = state.alley {
					return .task {
						await .lanesResponse(TaskResult {
							return try await lanesDataProvider.fetchLanes(.init(filter: [.alley(alley)], ordering: .byLabel))
						})
					}
				}
				state.isLoadingInitialData = false
				return .none

			case let .lanesResponse(.success(lanes)):
				state.lanes = .init(uniqueElements: lanes)
				state.isLoadingInitialData = false
				return .none

			case .lanesResponse(.failure):
				// TODO: handle failure loading lanes
				state.isLoadingInitialData = false
				return .none
			}
		}
	}
}

import ComposableArchitecture
import LanesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

public struct AlleyLanesEditor: ReducerProtocol {
	public struct State: Equatable {
		public var alley: Alley.ID
		public var isLoadingInitialData = true
		public var lanes: IdentifiedArrayOf<LaneEditor.State> = []

		public init(alley: Alley.ID) {
			self.alley = alley
		}
	}

	public enum Action: Equatable {
		case loadInitialData
		case lanesResponse(TaskResult<[Lane]>)
		case addLaneButtonTapped
		case laneEditor(id: LaneEditor.State.ID, action: LaneEditor.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.lanesDataProvider) var lanesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .loadInitialData:
				state.isLoadingInitialData = true
				return .task { [alley = state.alley] in
					await .lanesResponse(TaskResult {
						try await lanesDataProvider.fetchLanes(.init(filter: [.alley(alley)], ordering: .byLabel))
					})
				}

			case let .lanesResponse(.success(lanes)):
				if lanes.count == 0 {
					state.lanes = .init(uniqueElements: [.init(
						id: uuid(),
						label: "1",
						isAgainstWall: true,
						isShowingAgainstWallNotice: true
					)])
				} else {
					state.lanes = .init(uniqueElements: lanes.enumerated().map { .init(
						id: $1.id,
						label: $1.label,
						isAgainstWall: $1.isAgainstWall,
						isShowingAgainstWallNotice: $0 == 0
					)})
				}
				state.isLoadingInitialData = false
				return .none

			case .lanesResponse(.failure):
				// TODO: handle lanes load error
				state.isLoadingInitialData = false
				return .none

			case .addLaneButtonTapped:
				// FIXME: is it possible to focus on this lane's input when it appears
				if let previousLane = state.lanes.last?.label, let previousLaneNumber = Int(previousLane) {
					state.lanes.append(.init(
						id: uuid(),
						label: String(previousLaneNumber + 1),
						isAgainstWall: false,
						isShowingAgainstWallNotice: false
					))
				} else {
					state.lanes.append(.init(
						id: uuid(),
						label: "",
						isAgainstWall: false,
						isShowingAgainstWallNotice: false
					))
				}
				return .none

			case .laneEditor:
				return .none
			}
		}
		.forEach(\.lanes, action: /Action.laneEditor(id:action:)) {
			LaneEditor()
		}
	}
}

import ComposableArchitecture
import LanesDataProviderInterface
import LaneEditorFeature
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesEditor: ReducerProtocol {
	public struct State: Equatable {
		public var alley: Alley.ID?
		public var isLoadingInitialData = true
		public var existingLanes: [Lane] = []
		public var lanes: IdentifiedArrayOf<LaneEditor.State> = []
		public var alert: AlertState<AlertAction>?
		public var addLaneForm: AddLaneForm.State?

		public init(alley: Alley.ID?) {
			self.alley = alley
		}
	}

	public enum Action: Equatable {
		case loadInitialData
		case addLaneButtonTapped
		case addMultipleLanesButtonTapped
		case lanesResponse(TaskResult<[Lane]>)
		case laneDeleteResponse(TaskResult<Lane.ID>)
		case alert(AlertAction)
		case laneEditor(id: LaneEditor.State.ID, action: LaneEditor.Action)
		case addLaneForm(AddLaneForm.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.lanesDataProvider) var lanesDataProvider
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .loadInitialData:
				guard state.isLoadingInitialData else { return .none }

				if let alley = state.alley {
					return .task {
						await .lanesResponse(TaskResult {
							try await lanesDataProvider.fetchLanes(.init(filter: [.alley(alley)], ordering: .byLabel))
						})
					}
				}
				return .task { .lanesResponse(.success([])) }

			case let .lanesResponse(.success(lanes)):
				state.existingLanes = lanes
				if lanes.count == 0 {
					state.lanes = .init(uniqueElements: [
						.init(
							id: uuid(),
							label: "1",
							isAgainstWall: true
						),
					])
				} else {
					state.lanes = .init(uniqueElements: lanes.enumerated().map { .init(
						id: $1.id,
						label: $1.label,
						isAgainstWall: $1.isAgainstWall
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
						isAgainstWall: false
					))
				} else {
					state.lanes.append(.init(
						id: uuid(),
						label: "",
						isAgainstWall: false
					))
				}
				return .none

			case .addMultipleLanesButtonTapped:
				state.addLaneForm = .init()
				return .none

			case .addLaneForm(.cancelButtonTapped):
				state.addLaneForm = nil
				return .none

			case .addLaneForm(.saveButtonTapped):
				if let lanesToAdd = state.addLaneForm?.lanesToAdd {
					if let previousLane = state.lanes.last?.label,
						 let previousLaneNumber = Int(previousLane) {
						(1...lanesToAdd).forEach {
							state.lanes.append(.init(id: uuid(), label: String(previousLaneNumber + $0), isAgainstWall: false))
						}
					} else {
						(1...lanesToAdd).forEach { _ in
							state.lanes.append(.init(id: uuid(), label: "", isAgainstWall: false))
						}
					}
				}
				state.addLaneForm = nil
				return .none

			case let .laneEditor(id, .swipeAction(.delete)):
				if let deleted = state.existingLanes.first(where: { $0.id == id }) {
					// FIXME: AlleyLanesEditorView does not re-render when alert is dismissed and deleted lane does not re-appear
					state.alert = AlleyLanesEditor.alert(toDelete: deleted)
				} else {
					state.lanes.removeAll { $0.id == id }
				}
				return .none

			case let .alert(.deleteButtonTapped(lane)):
				return .task { [lane = lane] in
					await .laneDeleteResponse(TaskResult {
						try await persistenceService.deleteLanes([lane])
						return lane.id
					})
				}

			case let .laneDeleteResponse(.success(id)):
				state.lanes.removeAll { $0.id == id }
				state.existingLanes.removeAll { $0.id == id }
				return .none

			case .laneDeleteResponse(.failure):
				// TODO: handle fail to delete lane
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case .laneEditor, .addLaneForm:
				return .none
			}
		}
		.forEach(\.lanes, action: /Action.laneEditor(id:action:)) {
			LaneEditor()
		}
		.ifLet(\.addLaneForm, action: /Action.addLaneForm) {
			AddLaneForm()
		}
	}
}

import ComposableArchitecture
import FeatureActionLibrary
import LaneEditorFeature
import LanesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesEditor: Reducer {
	public struct State: Equatable {
		public var alley: Alley?

		public var isLoadingData = true
		public var lanes: IdentifiedArrayOf<LaneEditor.State> = []
		public var existingLanes: [Lane] = []

		public var alert: AlertState<AlertAction>?
		public var addLaneForm: AddLaneForm.State?

		public init(alley: Alley?) {
			self.alley = alley
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didTapAddLaneButton
			case didTapAddMultipleLanesButton
			case setAddLaneForm(isPresented: Bool)
			case alert(AlertAction)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadLanes(TaskResult<[Lane]>)
			case didDeleteLane(TaskResult<Lane.ID>)

			case laneEditor(id: LaneEditor.State.ID, action: LaneEditor.Action)
			case addLaneForm(AddLaneForm.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.lanesDataProvider) var lanesDataProvider
	@Dependency(\.persistenceService) var persistenceService

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					state.isLoadingData = true
					return .task { [alley = state.alley] in
						if let alley {
							return await .internal(.didLoadLanes(TaskResult {
								try await lanesDataProvider.fetchLanes(.init(filter: .alley(alley), ordering: .byLabel))
							}))
						} else {
							return .internal(.didLoadLanes(.success([])))
						}
					}

				case .didTapAddLaneButton:
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

				case .didTapAddMultipleLanesButton:
					return presentAddLanesForm(&state)

				case .setAddLaneForm(isPresented: true):
					return presentAddLanesForm(&state)

				case .setAddLaneForm(isPresented: false):
					return didFinishAddingLanes(&state, count: nil)

				case let .alert(alertAction):
					switch alertAction {
					case let .didTapDeleteButton(lane):
						return .task { [lane = lane] in
							await .internal(.didDeleteLane(TaskResult {
								try await persistenceService.deleteLanes([lane])
								return lane.id
							}))
						}

					case .didTapDismissButton:
						state.alert = nil
						return .none
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadLanes(.success(lanes)):
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
					state.isLoadingData = false
					return .none

				case .didLoadLanes(.failure):
					// TODO: handle lanes load error
					state.existingLanes = []
					state.lanes = []
					state.isLoadingData = false
					return .none

				case let .didDeleteLane(.success(id)):
					state.lanes.removeAll { $0.id == id }
					state.existingLanes.removeAll { $0.id == id }
					return .none

				case .didDeleteLane(.failure):
					// TODO: handle fail to delete lane
					return .none

				case let .laneEditor(id, .delegate(delegateAction)):
					switch delegateAction {
					case .didSwipe(.delete):
						if let deleted = state.existingLanes.first(where: { $0.id == id }) {
							// FIXME: AlleyLanesEditorView does not re-render when alert is dismissed and deleted lane does not re-appear
							state.alert = AlleyLanesEditor.alert(toDelete: deleted)
						} else {
							state.lanes.removeAll { $0.id == id }
						}
						return .none
					}

				case let .addLaneForm(.delegate(delegateAction)):
					switch delegateAction {
					case let .didFinishAddingLanes(numberOfLanes):
						return didFinishAddingLanes(&state, count: numberOfLanes)
					}

				case .laneEditor(_, .binding), .laneEditor(_, .view), .laneEditor(_, .internal):
					return .none

				case .addLaneForm(.internal), .addLaneForm(.view), .addLaneForm(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.forEach(\.lanes, action: /Action.internal..Action.InternalAction.laneEditor(id:action:)) {
			LaneEditor()
		}
		.ifLet(\.addLaneForm, action: /Action.internal..Action.InternalAction.addLaneForm) {
			AddLaneForm()
		}
	}

	private func presentAddLanesForm(_ state: inout State) -> Effect<Action> {
		state.addLaneForm = .init()
		return .none
	}

	private func didFinishAddingLanes(_ state: inout State, count numberOfLanes: Int?) -> Effect<Action> {
		if let numberOfLanes {
			if let previousLane = state.lanes.last?.label,
				 let previousLaneNumber = Int(previousLane) {
				for index in 1...numberOfLanes {
					state.lanes.append(.init(id: uuid(), label: String(previousLaneNumber + index), isAgainstWall: false))
				}
			} else {
				for _ in 1...numberOfLanes {
					state.lanes.append(.init(id: uuid(), label: "", isAgainstWall: false))
				}
			}
		}

		state.addLaneForm = nil
		return .none
	}
}

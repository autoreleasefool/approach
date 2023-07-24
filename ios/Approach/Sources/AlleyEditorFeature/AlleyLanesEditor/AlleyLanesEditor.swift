import ComposableArchitecture
import FeatureActionLibrary
import LaneEditorFeature
import LanesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesEditor: Reducer {
	public struct State: Equatable {
		public var alley: Alley.ID
		public var existingLanes: IdentifiedArrayOf<Lane.Edit>
		public var newLanes: IdentifiedArrayOf<Lane.Create>

		@PresentationState public var alert: AlertState<AlertAction>?
		@PresentationState public var addLaneForm: AddLaneForm.State?

		public init(
			alley: Alley.ID,
			existingLanes: IdentifiedArrayOf<Lane.Edit>,
			newLanes: IdentifiedArrayOf<Lane.Create>
		) {
			self.alley = alley
			self.newLanes = newLanes
			self.existingLanes = existingLanes
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapAddLaneButton
			case didTapAddMultipleLanesButton
			case alert(PresentationAction<AlertAction>)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didDeleteLane(TaskResult<Lane.ID>)
			case laneEditor(id: LaneEditor.State.ID, action: LaneEditor.Action)
			case addLaneForm(PresentationAction<AddLaneForm.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum AlertAction: Equatable {
		case didTapDeleteButton(Lane.ID)
		case didTapDismissButton
	}

	public init() {}

	@Dependency(\.lanes) var lanes
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapAddLaneButton:
					// FIXME: is it possible to focus on this lane's input when it appears
					return didFinishAddingLanes(&state, count: 1)

				case .didTapAddMultipleLanesButton:
					state.addLaneForm = .init()
					return .none

				case let .alert(.presented(alertAction)):
					switch alertAction {
					case let .didTapDeleteButton(lane):
						return .run { send in
							await send(.internal(.didDeleteLane(TaskResult {
								try await lanes.delete([lane])
								return lane
							})))
						}

					case .didTapDismissButton:
						state.alert = nil
						return .none
					}

				case .alert(.dismiss):
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didDeleteLane(.success(id)):
					state.existingLanes.removeAll { $0.id == id }
					state.newLanes.removeAll { $0.id == id }
					return .none

				case .didDeleteLane(.failure):
					// TODO: handle fail to delete lane
					return .none

				case let .laneEditor(id, .delegate(delegateAction)):
					switch delegateAction {
					case .didDeleteLane:
						if let deleted = state.existingLanes.first(where: { $0.id == id }) {
							state.alert = AlertState {
								TextState(Strings.Form.Prompt.delete(deleted.label))
							} actions: {
								ButtonState(role: .destructive, action: .didTapDeleteButton(deleted.id)) { TextState(Strings.Action.delete) }
								ButtonState(role: .cancel, action: .didTapDismissButton) { TextState(Strings.Action.cancel) }
							}
						} else {
							state.existingLanes.removeAll { $0.id == id }
							state.newLanes.removeAll { $0.id == id }
						}
						return .none
					}

				case let .addLaneForm(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didFinishAddingLanes(numberOfLanes):
						return didFinishAddingLanes(&state, count: numberOfLanes)
					}

				case .laneEditor(_, .view), .laneEditor(_, .internal):
					return .none

				case .addLaneForm(.presented(.internal)),
						.addLaneForm(.presented(.view)),
						.addLaneForm(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.forEach(\.existingLaneEditors, action: /Action.internal..Action.InternalAction.laneEditor(id:action:)) {
			LaneEditor()
		}
		.forEach(\.newLaneEditors, action: /Action.internal..Action.InternalAction.laneEditor(id:action:)) {
			LaneEditor()
		}
		.ifLet(\.$addLaneForm, action: /Action.internal..Action.InternalAction.addLaneForm) {
			AddLaneForm()
		}
	}

	private func didFinishAddingLanes(_ state: inout State, count numberOfLanes: Int?) -> Effect<Action> {
		if let numberOfLanes {
			if let previousLane = state.newLanes.last?.label ?? state.existingLanes.last?.label,
				 let previousLaneNumber = Int(previousLane) {
				for index in 1...numberOfLanes {
					state.newLanes.append(.init(
						alleyId: state.alley,
						id: uuid(),
						label: String(previousLaneNumber + index),
						position: .noWall
					))
				}
			} else {
				for index in 1...numberOfLanes {
					state.newLanes.append(.init(alleyId: state.alley, id: uuid(), label: String(index), position: .noWall))
				}
			}
		}

		state.addLaneForm = nil
		return .none
	}
}

extension AlleyLanesEditor.State {
	public var existingLaneEditors: IdentifiedArrayOf<LaneEditor.State> {
		get {
			.init(
				uniqueElements: existingLanes.map { .init(id: $0.id, label: $0.label, position: $0.position) }
			)
		}
		set {
			self.existingLanes = .init(
				uniqueElements: newValue.map { .init(id: $0.id, label: $0.label, position: $0.position) }
			)
		}
	}

	public var newLaneEditors: IdentifiedArrayOf<LaneEditor.State> {
		get {
			.init(
				uniqueElements: newLanes.map { .init(id: $0.id, label: $0.label, position: $0.position) }
			)
		}
		set {
			self.newLanes = .init(
				uniqueElements: newValue.map { .init(alleyId: alley, id: $0.id, label: $0.label, position: $0.position) }
			)
		}
	}
}

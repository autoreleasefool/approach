import ComposableArchitecture
import LaneEditorFeature

extension AlleyLanesEditor {
	func didFinishAddingLanes(_ state: inout State, count numberOfLanes: Int?) -> Effect<Action> {
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

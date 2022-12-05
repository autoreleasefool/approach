import ComposableArchitecture
import Foundation

public struct LaneEditor: ReducerProtocol {
	public struct State: Identifiable, Equatable {
		public let id: UUID
		public let isShowingAgainstWallNotice: Bool
		@BindableState public var label: String
		@BindableState public var isAgainstWall: Bool

		public init(id: UUID, label: String = "", isAgainstWall: Bool = false, isShowingAgainstWallNotice: Bool) {
			self.id = id
			self.label = label
			self.isAgainstWall = isAgainstWall
			self.isShowingAgainstWallNotice = isShowingAgainstWallNotice
		}

		public var laneLabel: Int? {
			Int(label)
		}

		public var isValid: Bool {
			label.isEmpty || laneLabel != nil
		}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
	}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { _, action in
			switch action {
			case .binding:
				return .none
			}
		}
	}
}

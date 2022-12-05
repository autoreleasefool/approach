import ComposableArchitecture
import Foundation

public struct LaneEditor: ReducerProtocol {
	public struct State: Equatable {
		public let id: UUID
		public let isShowingAgainstWallNotice: Bool
		@BindableState public var label = ""
		@BindableState public var isAgainstWall = false

		public init(id: UUID, isShowingAgainstWallNotice: Bool) {
			self.id = id
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

import ComposableArchitecture
import Foundation

public struct Lane: ReducerProtocol {
	public struct State: Equatable {
		public let id: UUID
		@BindableState public var label = ""

		public init(id: UUID) {
			self.id = id
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

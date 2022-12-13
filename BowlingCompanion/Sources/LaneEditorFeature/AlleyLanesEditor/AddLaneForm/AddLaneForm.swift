import ComposableArchitecture

public struct AddLaneForm: ReducerProtocol {
	public struct State: Equatable {
		@BindableState var lanesToAdd = 1
	}

	public enum Action: BindableAction, Equatable {
		case saveButtonTapped
		case cancelButtonTapped
		case binding(BindingAction<State>)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { _, action in
			switch action {
			case .cancelButtonTapped, .saveButtonTapped, .binding:
				return .none
			}
		}
	}
}

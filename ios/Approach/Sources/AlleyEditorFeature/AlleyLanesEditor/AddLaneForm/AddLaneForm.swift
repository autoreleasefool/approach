import ComposableArchitecture
import FeatureActionLibrary

public struct AddLaneForm: ReducerProtocol {
	public struct State: Equatable {
		@BindableState var lanesToAdd = 1
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapSaveButton
			case didTapCancelButton
		}

		public enum DelegateAction: Equatable {
			case didFinishAddingLanes(Int?)
		}

		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSaveButton:
					return didFinishAddingLanes(count: state.lanesToAdd)
				case .didTapCancelButton:
					return didFinishAddingLanes(count: nil)
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
	}

	private func didFinishAddingLanes(count: Int?) -> EffectTask<Action> {
		.task { .delegate(.didFinishAddingLanes(count)) }
	}
}

import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct AddLaneForm: Reducer {
	public struct State: Equatable {
		@BindingState var lanesToAdd = 1
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

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSaveButton:
					return .send(.delegate(.didFinishAddingLanes(state.lanesToAdd)))

				case .didTapCancelButton:
					return .send(.delegate(.didFinishAddingLanes(nil)))
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
}

// MARK: - View

public struct AddLaneFormView: View {
	let store: StoreOf<AddLaneForm>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			VStack(spacing: .standardSpacing) {
				HStack {
					Button(Strings.Action.cancel) {
						viewStore.send(.view(.didTapCancelButton))
					}
					Spacer()
					Button(Strings.Action.add) {
						viewStore.send(.view(.didTapSaveButton))
					}
				}

				Stepper(
					Strings.Lane.Editor.Fields.addLanes(viewStore.lanesToAdd),
					value: viewStore.binding(\.$lanesToAdd),
					in: Alley.NUMBER_OF_LANES_RANGE
				)
			}
		})
	}
}

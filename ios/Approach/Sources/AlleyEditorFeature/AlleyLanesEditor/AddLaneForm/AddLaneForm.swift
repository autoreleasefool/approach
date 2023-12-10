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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapSaveButton
			case didTapCancelButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didFinishAddingLanes(Int?)
		}
		public enum InternalAction: Equatable { case doNothing }

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)

	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSaveButton:
					return .send(.delegate(.didFinishAddingLanes(state.lanesToAdd)))

				case .didTapCancelButton:
					return .send(.delegate(.didFinishAddingLanes(nil)))

				case .binding:
					return .none
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct AddLaneFormView: View {
	let store: StoreOf<AddLaneForm>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack(spacing: .standardSpacing) {
				HStack {
					Button(Strings.Action.cancel) {
						viewStore.send(.didTapCancelButton)
					}
					Spacer()
					Button(Strings.Action.add) {
						viewStore.send(.didTapSaveButton)
					}
				}

				Stepper(
					Strings.Lane.Editor.Fields.addLanes(viewStore.lanesToAdd),
					value: viewStore.$lanesToAdd,
					in: Alley.NUMBER_OF_LANES_RANGE
				)
			}
		})
	}
}

import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct AddLaneForm: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var lanesToAdd = 1
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didTapSaveButton
			case didTapCancelButton

		}
		@CasePathable public enum Delegate {
			case didFinishAddingLanes(Int?)
		}
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)

	}

	public init() {}

	public var body: some ReducerOf<Self> {
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

			case .internal(.doNothing):
				return .none

			case .delegate, .binding:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: AddLaneForm.self)
public struct AddLaneFormView: View {
	@Bindable public var store: StoreOf<AddLaneForm>

	public var body: some View {
		VStack(spacing: .standardSpacing) {
			HStack {
				Button(Strings.Action.cancel) {
					send(.didTapCancelButton)
				}
				Spacer()
				Button(Strings.Action.add) {
					send(.didTapSaveButton)
				}
			}

			Stepper(
				Strings.Lane.Editor.Fields.addLanes(store.lanesToAdd),
				value: $store.lanesToAdd,
				in: Alley.NUMBER_OF_LANES_RANGE
			)
		}
	}
}

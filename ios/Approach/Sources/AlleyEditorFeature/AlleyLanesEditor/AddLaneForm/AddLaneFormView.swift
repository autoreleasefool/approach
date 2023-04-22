import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI

struct AddLaneFormView: View {
	let store: StoreOf<AddLaneForm>

	struct ViewState: Equatable {
		@BindingState var lanesToAdd: Int

		init(state: AddLaneForm.State) {
			self.lanesToAdd = state.lanesToAdd
		}
	}

	enum ViewAction: BindableAction {
		case didTapSaveButton
		case didTapCancelButton
		case binding(BindingAction<ViewState>)
	}

	var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AddLaneForm.Action.init) { viewStore in
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
					value: viewStore.binding(\.$lanesToAdd),
					in: Alley.NUMBER_OF_LANES_RANGE
				)
			}
		}
	}
}

extension AddLaneForm.State {
	var view: AddLaneFormView.ViewState {
		get { .init(state: self) }
		set {
			self.lanesToAdd = newValue.lanesToAdd
		}
	}
}

extension AddLaneForm.Action {
	init(action: AddLaneFormView.ViewAction) {
		switch action {
		case .didTapSaveButton:
			self = .view(.didTapSaveButton)
		case .didTapCancelButton:
			self = .view(.didTapCancelButton)
		case let .binding(action):
			self = .binding(action.pullback(\AddLaneForm.State.view))
		}
	}
}

import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI
import ThemesLibrary

struct AddLaneFormView: View {
	let store: StoreOf<AddLaneForm>

	struct ViewState: Equatable {
		@BindableState var lanesToAdd: Int

		init(state: AddLaneForm.State) {
			self.lanesToAdd = state.lanesToAdd
		}
	}

	enum ViewAction: BindableAction {
		case saveButtonTapped
		case cancelButtonTapped
		case binding(BindingAction<ViewState>)
	}

	var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AddLaneForm.Action.init) { viewStore in
			VStack(spacing: .standardSpacing) {
				HStack {
					Button(Strings.Action.cancel) {
						viewStore.send(.cancelButtonTapped)
					}
					Spacer()
					Button(Strings.Action.add) {
						viewStore.send(.saveButtonTapped)
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
		case .saveButtonTapped:
			self = .saveButtonTapped
		case .cancelButtonTapped:
			self = .cancelButtonTapped
		case let .binding(action):
			self = .binding(action.pullback(\AddLaneForm.State.view))
		}
	}
}

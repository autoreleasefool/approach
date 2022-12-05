import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct BallDetailsView: View {
	let store: StoreOf<BallDetails>

	struct ViewState: Equatable {
		let frame: Int
		let ball: Int
		@BindableState var fouled: Bool
		@BindableState var ballRolled: Gear?

		init(state: BallDetails.State) {
			self.frame = state.frame
			self.ball = state.ball
			self.fouled = state.fouled
			self.ballRolled = state.ballRolled
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
		case nextButtonTapped
	}

	init(store: StoreOf<BallDetails>) {
		self.store = store
	}

	var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BallDetails.Action.init) { viewStore in
			VStack(alignment: .leading, spacing: .unitSpacing) {
				HStack(alignment: .firstTextBaseline, spacing: .smallSpacing) {
					Text(Strings.Frame.title(viewStore.frame))
					Text(Strings.Ball.title(viewStore.ball))
						.font(.caption)
				}
				.frame(maxWidth: .infinity, alignment: .leading)
				.padding(.leading, .standardSpacing)

				HStack {
					BallPickerView(ballRolled: viewStore.binding(\.$ballRolled))
					Spacer()
					FoulPickerView(fouled: viewStore.binding(\.$fouled))
				}
				.padding(.standardSpacing)
				.background(Color.appBackgroundSecondary)
				.cornerRadius(.standardRadius)
			}
		}
	}
}

extension BallDetails.State {
	var view: BallDetailsView.ViewState {
		get { .init(state: self) }
		set {
			self.fouled = newValue.fouled
			self.ballRolled = newValue.ballRolled
		}
	}
}

extension BallDetails.Action {
	init(action: BallDetailsView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\BallDetails.State.view))
		case .nextButtonTapped:
			self = .nextButtonTapped
		}
	}
}

#if DEBUG
struct BallDetailsViewPreviews: PreviewProvider {
	static var previews: some View {
		ScrollView {
			BallDetailsView(
				store: .init(
					initialState: .init(
						frame: 1,
						ball: 1
					),
					reducer: BallDetails()
				)
			)
		}
	}
}
#endif

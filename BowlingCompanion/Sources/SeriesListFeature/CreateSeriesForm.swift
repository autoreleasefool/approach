import ComposableArchitecture
import SeriesDataProviderInterface
import SharedModelsLibrary
import SwiftUI

public struct CreateSeriesForm: ReducerProtocol {
	public struct State: Equatable {
		@BindableState var numberOfGames = League.DEFAULT_NUMBER_OF_GAMES
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case createButtonTapped
		case cancelButtonTapped
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { _, action in
			switch action {
			case .createButtonTapped:
				return .none

			case .cancelButtonTapped:
				return .none

			case .binding:
				return .none
			}
		}
	}
}

public struct CreateSeriesFormView: View {
	let store: StoreOf<CreateSeriesForm>

	struct ViewState: Equatable {
		@BindableState var numberOfGames: Int

		init(state: CreateSeriesForm.State) {
			self.numberOfGames = state.numberOfGames
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
		case createButtonTapped
		case cancelButtonTapped
	}

	public init(store: StoreOf<CreateSeriesForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: CreateSeriesForm.Action.init) { viewStore in
			Form {
				Section("Number of Games") {
					Stepper(
						"\(viewStore.numberOfGames)",
						value: viewStore.binding(\.$numberOfGames),
						in: League.NUMBER_OF_GAMES_RANGE
					)
				}
			}
			.navigationTitle("New Series")
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button("Cancel") {
						viewStore.send(.cancelButtonTapped)
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button("Start") {
						viewStore.send(.createButtonTapped)
					}
				}
			}
		}
	}
}

extension CreateSeriesForm.State {
	var view: CreateSeriesFormView.ViewState {
		get { .init(state: self) }
		set {
			self.numberOfGames = newValue.numberOfGames
		}
	}
}

extension CreateSeriesForm.Action {
	init(action: CreateSeriesFormView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\CreateSeriesForm.State.view))
		case .createButtonTapped:
			self = .createButtonTapped
		case .cancelButtonTapped:
			self = .cancelButtonTapped
		}
	}
}

import AlleyPickerFeature
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct CreateSeriesForm: ReducerProtocol {
	public struct State: Equatable {
		@BindableState public var numberOfGames = League.DEFAULT_NUMBER_OF_GAMES
		public var alleyPicker: AlleyPicker.State

		public init(league: League) {
			self.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
			self.alleyPicker = .init(selected: Set([league.alley].compactMap { $0 }), limit: 1)
		}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case createButtonTapped
		case cancelButtonTapped
		case alleyPicker(AlleyPicker.Action)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.alleyPicker, action: /CreateSeriesForm.Action.alleyPicker) {
			AlleyPicker()
		}

		Reduce { _, action in
			switch action {
			case .createButtonTapped:
				return .none

			case .cancelButtonTapped:
				return .none

			case .alleyPicker:
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
		let selectedAlley: Alley?

		init(state: CreateSeriesForm.State) {
			self.numberOfGames = state.numberOfGames
			if let id = state.alleyPicker.selected.first {
				self.selectedAlley = state.alleyPicker.alleys?[id: id]
			} else {
				self.selectedAlley = nil
			}
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
				Section(Strings.Series.Editor.Fields.NumberOfGames.title) {
					Stepper(
						"\(viewStore.numberOfGames)",
						value: viewStore.binding(\.$numberOfGames),
						in: League.NUMBER_OF_GAMES_RANGE
					)
				}

				Section(Strings.Series.Editor.Fields.Alley.title) {
					NavigationLink(
						destination: AlleyPickerView(
							store: store.scope(
								state: \.alleyPicker,
								action: CreateSeriesForm.Action.alleyPicker
							)
						)
					) {
						LabeledContent(
							Strings.Series.Editor.Fields.Alley.BowlingAlley.title,
							value: viewStore.selectedAlley?.name ?? Strings.Series.Editor.Fields.Alley.BowlingAlley.none
						)
					}
				}
			}
			.navigationTitle(Strings.Series.Editor.new)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Series.Editor.cancel) {
						viewStore.send(.cancelButtonTapped)
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Series.Editor.start) {
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

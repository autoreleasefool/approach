import ComposableArchitecture
import FeatureActionLibrary
import LeaguesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct LeaguesFilter: Reducer {
	public struct State: Equatable {
		@BindingState public var recurrence: League.Recurrence?
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapClearButton
			case didTapApplyButton
		}
		public enum DelegateAction: Equatable {
			case didChangeFilters
			case didApplyFilters
		}
		public enum InternalAction: Equatable {}

		case binding(BindingAction<State>)
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapClearButton:
					state = .init()
					return .send(.delegate(.didApplyFilters))

				case .didTapApplyButton:
					return .send(.delegate(.didApplyFilters))
				}

			case .binding:
				return .send(.delegate(.didChangeFilters))

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct LeaguesFilterView: View {
	let store: StoreOf<LeaguesFilter>

	init(store: StoreOf<LeaguesFilter>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			List {
				Section {
					Picker(
						Strings.League.Properties.recurrence,
						selection: viewStore.binding(\.$recurrence)
					) {
						Text("").tag(nil as League.Recurrence?)
						ForEach(League.Recurrence.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				}

				Section {
					Button(Strings.Action.reset) { viewStore.send(.view(.didTapClearButton)) }
						.tint(.appDestructive)
				}
			}
			.navigationTitle(Strings.Action.filter)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.view(.didTapApplyButton)) }
				}
			}
		})
	}
}

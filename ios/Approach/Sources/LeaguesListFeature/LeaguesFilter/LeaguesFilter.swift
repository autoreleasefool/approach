import AssetsLibrary
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapClearButton
			case didTapApplyButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didChangeFilters(League.Recurrence?)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapClearButton:
					return .concatenate(
						.send(.delegate(.didChangeFilters(nil))),
						.run { _ in await dismiss() }
					)

				case .didTapApplyButton:
					return .run { _ in await dismiss() }

				case .binding:
					return .send(.delegate(.didChangeFilters(state.recurrence)))
				}

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

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					Picker(
						Strings.League.Properties.recurrence,
						selection: viewStore.$recurrence
					) {
						Text("").tag(nil as League.Recurrence?)
						ForEach(League.Recurrence.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				}

				Section {
					Button(Strings.Action.reset) { viewStore.send(.didTapClearButton) }
						.tint(Asset.Colors.Destructive.default)
				}
			}
			.navigationTitle(Strings.League.Filters.title)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.didTapApplyButton) }
				}
			}
		})
	}
}

import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import LeaguesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct LeaguesFilter: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		// TODO: migrate to Shared
		public var recurrence: League.Recurrence?
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case didTapClearButton
			case didTapApplyButton
		}
		@CasePathable
		public enum Delegate {
			case didChangeFilters(League.Recurrence?)
		}
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		BindingReducer()

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
				}

			case .internal(.doNothing):
				return .none

			case .binding:
				return .send(.delegate(.didChangeFilters(state.recurrence)))

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: LeaguesFilter.self)
public struct LeaguesFilterView: View {
	@Bindable public var store: StoreOf<LeaguesFilter>

	public var body: some View {
		List {
			Section {
				Picker(
					Strings.League.Properties.recurrence,
					selection: $store.recurrence
				) {
					Text("").tag(nil as League.Recurrence?)
					ForEach(League.Recurrence.allCases) {
						Text(String(describing: $0)).tag(Optional($0))
					}
				}
			}

			Section {
				Button(Strings.Action.reset) { send(.didTapClearButton) }
					.tint(Asset.Colors.Destructive.default)
			}
		}
		.navigationTitle(Strings.League.Filters.title)
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.apply) { send(.didTapApplyButton) }
			}
		}
	}
}

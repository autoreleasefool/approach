import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GearRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GearFilter: Reducer {
	public struct State: Equatable {
		@BindingState public var kind: Gear.Kind?
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapClearButton
			case didTapApplyButton
		}
		public enum DelegateAction: Equatable {
			case didChangeFilters(Gear.Kind?)
		}
		public enum InternalAction: Equatable {}

		case binding(BindingAction<State>)
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
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

			case .binding:
				return .send(.delegate(.didChangeFilters(state.kind)))

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

public struct GearFilterView: View {
	let store: StoreOf<GearFilter>

	init(store: StoreOf<GearFilter>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			List {
				Section {
					Picker(
						Strings.Gear.Properties.kind,
						selection: viewStore.binding(\.$kind)
					) {
						Text("").tag(nil as Gear.Kind?)
						ForEach(Gear.Kind.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				}

				Section {
					Button(Strings.Action.reset) { viewStore.send(.view(.didTapClearButton)) }
						.tint(.appDestructive)
				}
			}
			.navigationTitle(Strings.Gear.Filters.title)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.view(.didTapApplyButton)) }
				}
			}
		})
	}
}

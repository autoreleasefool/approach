import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GearRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct GearFilter: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared public var kind: Gear.Kind?
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case didTapClearButton
			case didTapApplyButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
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
					state.$kind.withLock { $0 = nil }
					return .run { _ in await dismiss() }

				case .didTapApplyButton:
					return .run { _ in await dismiss() }
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

@ViewAction(for: GearFilter.self)
public struct GearFilterView: View {
	@Bindable public var store: StoreOf<GearFilter>

	public var body: some View {
		List {
			Section {
				Picker(
					Strings.Gear.Properties.kind,
					selection: $store.kind
				) {
					Text("").tag(nil as Gear.Kind?)
					ForEach(Gear.Kind.allCases) {
						Text(String(describing: $0)).tag(Optional($0))
					}
				}
			}

			Section {
				Button(Strings.Action.reset) { send(.didTapClearButton) }
					.tint(Asset.Colors.Destructive.default)
			}
		}
		.navigationTitle(Strings.Gear.Filters.title)
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.apply) { send(.didTapApplyButton) }
			}
		}
	}
}

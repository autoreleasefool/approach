import AlleysRepositoryInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct AlleysFilter: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var filter: Alley.List.FetchRequest.Filter
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didTapClearButton
			case didTapApplyButton
		}
		@CasePathable public enum Delegate {
			case didChangeFilters(Alley.List.FetchRequest.Filter)
		}
		@CasePathable public enum Internal { case doNothing }

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
						.send(.delegate(.didChangeFilters(.init()))),
						.run { _ in await dismiss() }
					)

				case .didTapApplyButton:
					return .run { _ in await dismiss() }
				}

			case .internal(.doNothing):
				return .none

			case .binding:
				return .send(.delegate(.didChangeFilters(state.filter)))

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: AlleysFilter.self)
public struct AlleysFilterView: View {
	@Bindable public var store: StoreOf<AlleysFilter>

	public var body: some View {
		List {
			Section(Strings.Alley.Properties.title) {
				Picker(
					Strings.Alley.Properties.material,
					selection: $store.filter.material
				) {
					Text("").tag(nil as Alley.Material?)
					ForEach(Alley.Material.allCases) {
						Text(String(describing: $0)).tag(Optional($0))
					}
				}

				Picker(
					Strings.Alley.Properties.mechanism,
					selection: $store.filter.mechanism
				) {
					Text("").tag(nil as Alley.Mechanism?)
					ForEach(Alley.Mechanism.allCases) {
						Text(String(describing: $0)).tag(Optional($0))
					}
				}

				Picker(
					Strings.Alley.Properties.pinFall,
					selection: $store.filter.pinFall
				) {
					Text("").tag(nil as Alley.PinFall?)
					ForEach(Alley.PinFall.allCases) {
						Text(String(describing: $0)).tag(Optional($0))
					}
				}

				Picker(
					Strings.Alley.Properties.pinBase,
					selection: $store.filter.pinBase
				) {
					Text("").tag(nil as Alley.PinBase?)
					ForEach(Alley.PinBase.allCases) {
						Text(String(describing: $0)).tag(Optional($0))
					}
				}
			}

			Section {
				Button(Strings.Action.reset) { send(.didTapClearButton) }
					.tint(Asset.Colors.Destructive.default)
			}
		}
		.navigationTitle(Strings.Alley.Filters.title)
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.apply) { send(.didTapApplyButton) }
			}
		}
	}
}

extension Alley.Material: CustomStringConvertible {
	public var description: String {
		switch self {
		case .synthetic: return Strings.Alley.Properties.Material.synthetic
		case .wood: return Strings.Alley.Properties.Material.wood
		}
	}
}

extension Alley.PinFall: CustomStringConvertible {
	public var description: String {
		switch self {
		case .freefall: return Strings.Alley.Properties.PinFall.freefall
		case .strings: return Strings.Alley.Properties.PinFall.strings
		}
	}
}

extension Alley.Mechanism: CustomStringConvertible {
	public var description: String {
		switch self {
		case .dedicated: return Strings.Alley.Properties.Mechanism.dedicated
		case .interchangeable: return Strings.Alley.Properties.Mechanism.interchangeable
		}
	}
}

extension Alley.PinBase: CustomStringConvertible {
	public var description: String {
		switch self {
		case .black: return Strings.Alley.Properties.PinBase.black
		case .white: return Strings.Alley.Properties.PinBase.white
		case .other: return Strings.other
		}
	}
}

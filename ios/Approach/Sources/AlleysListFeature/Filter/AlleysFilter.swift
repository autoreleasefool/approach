import AlleysRepositoryInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleysFilter: Reducer {
	public struct State: Equatable {
		@BindingState public var filter: Alley.Summary.FetchRequest.Filter

		init(filter: Alley.Summary.FetchRequest.Filter) {
			self.filter = filter
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapClearButton
			case didTapApplyButton
		}
		public enum DelegateAction: Equatable {
			case didChangeFilters(Alley.Summary.FetchRequest.Filter)
		}
		public enum InternalAction: Equatable {}

		case binding(BindingAction<State>)
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some Reducer<State, Action> {
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

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .binding:
				return .send(.delegate(.didChangeFilters(state.filter)))

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct AlleysFilterView: View {
	let store: StoreOf<AlleysFilter>

	init(store: StoreOf<AlleysFilter>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			List {
				Section(Strings.Alley.Properties.title) {
					Picker(
						Strings.Alley.Properties.material,
						selection: viewStore.binding(\.$filter.material)
					) {
						Text("").tag(nil as Alley.Material?)
						ForEach(Alley.Material.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}

					Picker(
						Strings.Alley.Properties.mechanism,
						selection: viewStore.binding(\.$filter.mechanism)
					) {
						Text("").tag(nil as Alley.Mechanism?)
						ForEach(Alley.Mechanism.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}

					Picker(
						Strings.Alley.Properties.pinFall,
						selection: viewStore.binding(\.$filter.pinFall)
					) {
						Text("").tag(nil as Alley.PinFall?)
						ForEach(Alley.PinFall.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}

					Picker(
						Strings.Alley.Properties.pinBase,
						selection: viewStore.binding(\.$filter.pinBase)
					) {
						Text("").tag(nil as Alley.PinBase?)
						ForEach(Alley.PinBase.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				}

				Section {
					Button(Strings.Action.reset) { viewStore.send(.view(.didTapClearButton)) }
						.tint(.appDestructive)
				}
			}
			.navigationTitle(Strings.Alley.Filters.title)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.view(.didTapApplyButton)) }
				}
			}
		})
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

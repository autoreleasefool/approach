import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleysFilterView: View {
	let store: StoreOf<AlleysFilter>

	struct ViewState: Hashable, Equatable {
		@BindingState var material: Alley.Material?
		@BindingState var mechanism: Alley.Mechanism?
		@BindingState var pinBase: Alley.PinBase?
		@BindingState var pinFall: Alley.PinFall?

		init(state: AlleysFilter.State) {
			self.material = state.material
			self.mechanism = state.mechanism
			self.pinBase = state.pinBase
			self.pinFall = state.pinFall
		}
	}

	enum ViewAction: BindableAction {
		case didTapClearButton
		case didTapApplyButton
		case binding(BindingAction<ViewState>)
	}

	init(store: StoreOf<AlleysFilter>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleysFilter.Action.init) { viewStore in
			List {
				Section {
					Picker(
						Strings.Alley.Properties.material,
						selection: viewStore.binding(\.$material)
					) {
						Text("").tag(nil as Alley.Material?)
						ForEach(Alley.Material.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}

					Picker(
						Strings.Alley.Properties.mechanism,
						selection: viewStore.binding(\.$mechanism)
					) {
						Text("").tag(nil as Alley.Mechanism?)
						ForEach(Alley.Mechanism.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}

					Picker(
						Strings.Alley.Properties.pinFall,
						selection: viewStore.binding(\.$pinFall)
					) {
						Text("").tag(nil as Alley.PinFall?)
						ForEach(Alley.PinFall.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}

					Picker(
						Strings.Alley.Properties.pinBase,
						selection: viewStore.binding(\.$pinBase)
					) {
						Text("").tag(nil as Alley.PinBase?)
						ForEach(Alley.PinBase.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				}

				Section {
					Button(Strings.Action.reset) { viewStore.send(.didTapClearButton) }
						.tint(.appDestructive)
				}
			}
			.navigationTitle(Strings.Action.filter)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.didTapApplyButton) }
				}
			}
		}
	}
}

extension AlleysFilter.State {
	var view: AlleysFilterView.ViewState {
		get { .init(state: self) }
		set {
			self.material = newValue.material
			self.pinFall = newValue.pinFall
			self.mechanism = newValue.mechanism
			self.pinBase = newValue.pinBase
		}
	}
}

extension AlleysFilter.Action {
	init(action: AlleysFilterView.ViewAction) {
		switch action {
		case .didTapClearButton:
			self = .view(.didTapClearButton)
		case .didTapApplyButton:
			self = .view(.didTapApplyButton)
		case let .binding(action):
			self = .binding(action.pullback(\AlleysFilter.State.view))
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

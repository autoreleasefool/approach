import AlleysDataProviderInterface
import AssetsLibrary
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

struct AlleysFilterView: View {
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
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				Section {
					Button(Strings.Action.reset) { viewStore.send(.didTapClearButton) }
						.tint(.appDestructive)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.scrollContentBackground(.hidden)
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

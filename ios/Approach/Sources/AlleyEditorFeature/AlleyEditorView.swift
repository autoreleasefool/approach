import AlleysRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyEditorView: View {
	let store: StoreOf<AlleyEditor>

	struct ViewState: Equatable {
		@BindingState public var name: String
		@BindingState public var address: String?
		@BindingState public var material: Alley.Material?
		@BindingState public var pinFall: Alley.PinFall?
		@BindingState public var mechanism: Alley.Mechanism?
		@BindingState public var pinBase: Alley.PinBase?

		var isLaneEditorPresented: Bool
		let hasLanesEnabled: Bool
		let newLanes: IdentifiedArrayOf<Lane.Create>
		let existingLanes: IdentifiedArrayOf<Lane.Edit>

		init(state: AlleyEditor.State) {
			self.name = state.name
			self.address = state.address
			self.material = state.material
			self.pinFall = state.pinFall
			self.mechanism = state.mechanism
			self.pinBase = state.pinBase
			self.isLaneEditorPresented = state.isLaneEditorPresented
			self.hasLanesEnabled = state.hasLanesEnabled
			self.newLanes = state.newLanes
			self.existingLanes = state.existingLanes
		}
	}

	enum ViewAction: BindableAction {
		case setLaneEditor(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<AlleyEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyEditor.Action.init) { viewStore in
			FormView(store: store.scope(state: \.form, action: /AlleyEditor.Action.InternalAction.form)) {
				detailsSection(viewStore)
				materialSection(viewStore)
				mechanismSection(viewStore)
				pinFallSection(viewStore)
				pinBaseSection(viewStore)
				if viewStore.hasLanesEnabled {
					lanesSection(viewStore)
				}
				Section {
					Text(Strings.Alley.Editor.Help.askAStaffMember)
						.font(.caption)
				}
			}
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: viewStore.binding(\.$name)
			)
			TextField(
				Strings.Editor.Fields.Details.address,
				text: viewStore.binding(
					get: { $0.address ?? "" },
					send: { ViewAction.set(\.$address, $0) }
				)
			)
			.textContentType(.fullStreetAddress)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func materialSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
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
		} footer: {
			Text(Strings.Alley.Editor.Fields.Material.help)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func pinFallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.pinFall,
				selection: viewStore.binding(\.$pinFall)
			) {
				Text("").tag(nil as Alley.PinFall?)
				ForEach(Alley.PinFall.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.PinFall.help)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func mechanismSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.mechanism,
				selection: viewStore.binding(\.$mechanism)
			) {
				Text("").tag(nil as Alley.Mechanism?)
				ForEach(Alley.Mechanism.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.Mechanism.help)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func pinBaseSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.pinBase,
				selection: viewStore.binding(\.$pinBase)
			) {
				Text("").tag(nil as Alley.PinBase?)
				ForEach(Alley.PinBase.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.PinBase.help)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func lanesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Group {
				if viewStore.newLanes.isEmpty && viewStore.existingLanes.isEmpty {
					Text(Strings.Alley.Properties.Lanes.none)
				} else {
					ForEach(viewStore.existingLanes) { lane in
						Lane.View(label: lane.label, position: lane.position)
					}
					ForEach(viewStore.newLanes) { lane in
						Lane.View(label: lane.label, position: lane.position)
					}
				}
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		} header: {
			HStack(alignment: .bottom) {
				Text(Strings.Lane.List.title)
				Spacer()
				NavigationLink(
					destination: AlleyLanesEditorView(store: store.scope(
						state: \.alleyLanes,
						action: /AlleyEditor.Action.InternalAction.alleyLanes
					)),
					isActive: viewStore.binding(
						get: \.isLaneEditorPresented,
						send: AlleyEditorView.ViewAction.setLaneEditor(isPresented:)
					)
				) {
					Text(Strings.Action.manage)
						.font(.caption)
				}
			}
		}
	}
}

extension AlleyEditor.State {
	var view: AlleyEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.name = newValue.name
			self.address = newValue.address
			self.material = newValue.material
			self.pinFall = newValue.pinFall
			self.mechanism = newValue.mechanism
			self.pinBase = newValue.pinBase
		}
	}
}

extension AlleyEditor.Action {
	init(action: AlleyEditorView.ViewAction) {
		switch action {
		case let .setLaneEditor(isPresented):
			self = .view(.setLaneEditor(isPresented: isPresented))
		case let .binding(action):
			self = .binding(action.pullback(\AlleyEditor.State.view))
		}
	}
}

import BaseFormFeature
import ComposableArchitecture
import LaneEditorFeature
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyEditorView: View {
	let store: StoreOf<AlleyEditor>

	struct ViewState: Equatable {
		@BindableState var name: String
		@BindableState var address: String
		@BindableState var material: Alley.Material
		@BindableState var pinFall: Alley.PinFall
		@BindableState var mechanism: Alley.Mechanism
		@BindableState var pinBase: Alley.PinBase
		var isLaneEditorPresented: Bool
		let hasLanesEnabled: Bool

		init(state: AlleyEditor.State) {
			self.name = state.base.form.name
			self.address = state.base.form.address
			self.material = state.base.form.material
			self.pinFall = state.base.form.pinFall
			self.mechanism = state.base.form.mechanism
			self.pinBase = state.base.form.pinBase
			self.isLaneEditorPresented = state.isLaneEditorPresented
			self.hasLanesEnabled = state.hasLanesEnabled
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
			BaseFormView(store: store.scope(state: \.base, action: AlleyEditor.Action.form)) {
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
				text: viewStore.binding(\.$address)
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
				ForEach(Alley.Material.allCases) {
					Text(String(describing: $0)).tag($0)
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
				ForEach(Alley.PinFall.allCases) {
					Text(String(describing: $0)).tag($0)
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
				ForEach(Alley.Mechanism.allCases) {
					Text(String(describing: $0)).tag($0)
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
				ForEach(Alley.PinBase.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.PinBase.help)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func lanesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			AlleyLanesView(store: store.scope(state: \.alleyLanes, action: AlleyEditor.Action.alleyLanes))
		} header: {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Lane.List.title)
				Spacer()
				NavigationLink(
					destination: AlleyLanesEditorView(store: store.scope(
						state: \.base.form.laneEditor,
						action: AlleyEditor.Action.laneEditor
					)),
					isActive: viewStore.binding(
						get: \.isLaneEditorPresented,
						send: AlleyEditorView.ViewAction.setLaneEditor(isPresented:)
					)
				) {
					Text(Strings.Alley.Properties.Lanes.manage)
				}
			}
		}
	}
}

extension AlleyEditor.State {
	var view: AlleyEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
			self.base.form.address = newValue.address
			self.base.form.material = newValue.material
			self.base.form.pinFall = newValue.pinFall
			self.base.form.mechanism = newValue.mechanism
			self.base.form.pinBase = newValue.pinBase
		}
	}
}

extension AlleyEditor.Action {
	init(action: AlleyEditorView.ViewAction) {
		switch action {
		case let .setLaneEditor(isPresented):
			self = .setLaneEditor(isPresented: isPresented)
		case let .binding(action):
			self = .binding(action.pullback(\AlleyEditor.State.view))
		}
	}
}

#if DEBUG
struct AlleyEditorViewPreview: PreviewProvider {
	static var previews: some View {
		NavigationView {
			AlleyEditorView(
				store: .init(
					initialState: .init(mode: .create, hasLanesEnabled: true),
					reducer: AlleyEditor()
				)
			)
		}
	}
}
#endif

import BaseFormFeature
import ComposableArchitecture
import SharedModelsLibrary
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

		init(state: AlleyEditor.State) {
			self.name = state.base.form.name
			self.address = state.base.form.address
			self.material = state.base.form.material
			self.pinFall = state.base.form.pinFall
			self.mechanism = state.base.form.mechanism
			self.pinBase = state.base.form.pinBase
		}
	}

	enum ViewAction: BindableAction {
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
				Section {
					Text("Not sure about any of the settings? Ask a staff member! They'll probably be happy to help")
						.font(.caption)
				}
			}
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section("Details") {
			TextField("Name", text: viewStore.binding(\.$name))
			TextField("Address", text: viewStore.binding(\.$address))
				.textContentType(.fullStreetAddress)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func materialSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Material",
				selection: viewStore.binding(\.$material)
			) {
				ForEach(Alley.Material.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
		} footer: {
			Text("To help tell the difference, wooden lanes tend to show some wear, while synthetic lanes are usually harder and smoother.")
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func pinFallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Pin Fall",
				selection: viewStore.binding(\.$pinFall)
			) {
				ForEach(Alley.PinFall.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
		} footer: {
			Text("Look at how the pins are set up. Do you notice the pins are pushed off the lane after each ball, or are they attached to strings and pulled up?")
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func mechanismSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Mechanism",
				selection: viewStore.binding(\.$mechanism)
			) {
				ForEach(Alley.Mechanism.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
		} footer: {
			Text("Are the lanes interchangeable between multiple types of bowling (5-Pin and 10-Pin), or do they only support one kind?")
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func pinBaseSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Pin Base",
				selection: viewStore.binding(\.$pinBase)
			) {
				ForEach(Alley.PinBase.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
		} footer: {
			Text("What kind of base do the pins have?")
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
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
					initialState: .init(mode: .create),
					reducer: AlleyEditor()
				)
			)
		}
	}
}
#endif

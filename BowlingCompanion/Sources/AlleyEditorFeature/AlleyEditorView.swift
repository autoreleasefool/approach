import BaseFormFeature
import ComposableArchitecture
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
					Text(Strings.Alleys.Editor.Help.askAStaffMember)
						.font(.caption)
				}
			}
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section(Strings.Alleys.Editor.Fields.Details.title) {
			TextField(
				Strings.Alleys.Editor.Fields.Details.name,
				text: viewStore.binding(\.$name)
			)
			TextField(
				Strings.Alleys.Editor.Fields.Details.address,
				text: viewStore.binding(\.$address)
			)
			.textContentType(.fullStreetAddress)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func materialSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alleys.Editor.Fields.Material.title,
				selection: viewStore.binding(\.$material)
			) {
				ForEach(Alley.Material.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.Alleys.Editor.Fields.Material.footer)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func pinFallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alleys.Editor.Fields.PinFall.title,
				selection: viewStore.binding(\.$pinFall)
			) {
				ForEach(Alley.PinFall.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.Alleys.Editor.Fields.PinFall.footer)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func mechanismSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alleys.Editor.Fields.Mechanism.title,
				selection: viewStore.binding(\.$mechanism)
			) {
				ForEach(Alley.Mechanism.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.Alleys.Editor.Fields.Mechanism.footer)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func pinBaseSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alleys.Editor.Fields.PinBase.title,
				selection: viewStore.binding(\.$pinBase)
			) {
				ForEach(Alley.PinBase.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.Alleys.Editor.Fields.PinBase.footer)
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

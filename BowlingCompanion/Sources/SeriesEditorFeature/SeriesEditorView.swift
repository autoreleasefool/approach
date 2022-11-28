import BaseFormFeature
import ComposableArchitecture
import SwiftUI

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>

	struct ViewState: Equatable {
		@BindableState var date: Date
		let hasAlleysEnabled: Bool

		init(state: SeriesEditor.State) {
			self.date = state.base.form.date
			self.hasAlleysEnabled = state.hasAlleysEnabled
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: SeriesEditor.Action.form)) {
				Section("Details") {
					DatePicker(
						"Date",
						selection: viewStore.binding(\.$date),
						displayedComponents: [.date]
					)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				if viewStore.hasAlleysEnabled {
					Section("Alley") {
						NavigationLink(destination: EmptyView()) {
							LabeledContent("Bowling Alley", value: "None")
						}
						NavigationLink(destination: EmptyView()) {
							LabeledContent("Lanes", value: "1, 2")
						}
						NavigationLink(destination: EmptyView()) {
							LabeledContent("Starting Lane", value: "1")
						}
					}
					.listRowBackground(Color(uiColor: .secondarySystemBackground))
				}
			}
		}
	}
}

extension SeriesEditor.State {
	var view: SeriesEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.date = newValue.date
		}
	}
}

extension SeriesEditor.Action {
	init(action: SeriesEditorView.ViewAction) {
		switch action {
		case .binding(let action):
			self = .binding(action.pullback(\SeriesEditor.State.view))
		}
	}
}

#if DEBUG
struct SeriesEditorViewPreview: PreviewProvider {
	static var previews: some View {
		NavigationView {
			SeriesEditorView(store:
				.init(
					initialState: .init(
						league: .init(
							bowlerId: UUID(),
							id: UUID(),
							name: "Majors, 2022",
							recurrence: .repeating,
							numberOfGames: 4,
							additionalPinfall: nil,
							additionalGames: nil,
							alley: nil
						),
						mode: .create,
						hasAlleysEnabled: true
					),
					reducer: SeriesEditor()
				)
			)
		}
	}
}
#endif

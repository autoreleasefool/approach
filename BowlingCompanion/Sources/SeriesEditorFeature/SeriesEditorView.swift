import BaseFormFeature
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>

	struct ViewState: Equatable {
		@BindableState var date: Date
		@BindableState var numberOfGames: Int
		let selectedAlley: Alley?
		let hasAlleysEnabled: Bool

		init(state: SeriesEditor.State) {
			self.date = state.base.form.date
			self.numberOfGames = state.base.form.numberOfGames
			self.hasAlleysEnabled = state.hasAlleysEnabled
			if let id = state.base.form.alleyPicker.selected.first {
				if let alley = state.base.form.alleyPicker.resources?[id: id] {
					self.selectedAlley = alley
				} else if let alley = state.initialAlley, alley.id == id {
					self.selectedAlley = alley
				} else {
					self.selectedAlley = nil
				}
			} else {
				self.selectedAlley = nil
			}
		}
	}

	enum ViewAction: BindableAction {
		case loadInitialData
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: SeriesEditor.Action.form)) {
				Section(Strings.Series.Editor.Fields.Details.title) {
					DatePicker(
						Strings.Series.Editor.Fields.Details.date,
						selection: viewStore.binding(\.$date),
						displayedComponents: [.date]
					)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				if viewStore.hasAlleysEnabled {
					Section(Strings.Series.Editor.Fields.Alley.title) {
						NavigationLink(destination: EmptyView()) {
							LabeledContent(
								Strings.Series.Editor.Fields.Alley.BowlingAlley.title,
								value: Strings.Series.Editor.Fields.Alley.BowlingAlley.none
							)
						}
						NavigationLink(destination: EmptyView()) {
							LabeledContent(Strings.Series.Editor.Fields.Alley.lanes, value: "1, 2")
						}
						NavigationLink(destination: EmptyView()) {
							LabeledContent(Strings.Series.Editor.Fields.Alley.startingLane, value: "1")
						}
					}
					.listRowBackground(Color(uiColor: .secondarySystemBackground))
				}
			}
			.task { await viewStore.send(.loadInitialData).finish() }
		}
	}
}

extension SeriesEditor.State {
	var view: SeriesEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.date = newValue.date
			self.base.form.numberOfGames = newValue.numberOfGames
		}
	}
}

extension SeriesEditor.Action {
	init(action: SeriesEditorView.ViewAction) {
		switch action {
		case .loadInitialData:
			self = .loadInitialData
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
							bowler: UUID(),
							id: UUID(),
							name: "Majors, 2022",
							recurrence: .repeating,
							numberOfGames: 4,
							additionalPinfall: nil,
							additionalGames: nil,
							alley: nil
						),
						mode: .create,
						date: Date(),
						hasAlleysEnabled: true
					),
					reducer: SeriesEditor()
				)
			)
		}
	}
}
#endif

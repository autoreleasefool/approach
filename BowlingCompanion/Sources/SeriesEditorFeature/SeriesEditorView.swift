import BaseFormFeature
import ComposableArchitecture
import ResourcePickerFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>

	struct ViewState: Equatable {
		@BindableState var date: Date
		@BindableState var numberOfGames: Int
		let isAlleyPickerPresented: Bool
		let selectedAlley: Alley?
		let hasAlleysEnabled: Bool
		let hasLanesEnabled: Bool

		init(state: SeriesEditor.State) {
			self.date = state.base.form.date
			self.numberOfGames = state.base.form.numberOfGames
			self.hasAlleysEnabled = state.hasAlleysEnabled
			self.hasLanesEnabled = state.hasLanesEnabled
			self.isAlleyPickerPresented = state.isAlleyPickerPresented
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
		case setAlleyPicker(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: SeriesEditor.Action.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					DatePicker(
						Strings.Series.Properties.date,
						selection: viewStore.binding(\.$date),
						displayedComponents: [.date]
					)
				}

				if viewStore.hasAlleysEnabled {
					Section(Strings.Series.Properties.alley) {
						NavigationLink(
							destination: ResourcePickerView(
								store: store.scope(
									state: \.base.form.alleyPicker,
									action: SeriesEditor.Action.alleyPicker
								)
							) { alley in
								AlleyRow(alley: alley)
							},
							isActive: viewStore.binding(
								get: \.isAlleyPickerPresented,
								send: ViewAction.setAlleyPicker(isPresented:)
							)
						) {
							LabeledContent(
								Strings.Series.Properties.alley,
								value: viewStore.selectedAlley?.name ?? Strings.none
							)
						}

						// TODO: if lanes are enabled
//						NavigationLink(destination: EmptyView()) {
//							LabeledContent(Strings.Series.Editor.Fields.Alley.lanes, value: "1, 2")
//						}
//						NavigationLink(destination: EmptyView()) {
//							LabeledContent(Strings.Series.Editor.Fields.Alley.startingLane, value: "1")
//						}
					}
					.listRowBackground(Color(uiColor: .secondarySystemBackground))
				}
			}
			.onAppear { viewStore.send(.loadInitialData) }
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
		case let .setAlleyPicker(isPresented):
			self = .setAlleyPicker(isPresented: isPresented)
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
						hasAlleysEnabled: true,
						hasLanesEnabled: true
					),
					reducer: SeriesEditor()
				)
			)
		}
	}
}
#endif

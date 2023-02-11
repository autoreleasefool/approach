import AssetsLibrary
import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ResourcePickerLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>

	struct ViewState: Equatable {
		@BindableState var date: Date
		@BindableState var numberOfGames: Int
		@BindableState public var preBowl: Series.PreBowl
		@BindableState public var excludeFromStatistics: Series.ExcludeFromStatistics
		let isAlleyPickerPresented: Bool
		let isLanePickerPresented: Bool
		let selectedAlley: Alley?
		let selectedLanes: IdentifiedArrayOf<Lane>?
		let hasAlleysEnabled: Bool
		let hasLanesEnabled: Bool

		var laneLabels: String {
			if let selectedLanes, !selectedLanes.isEmpty {
				return selectedLanes.map(\.label).joined(separator: ", ")
			} else {
				return Strings.none
			}
		}

		init(state: SeriesEditor.State) {
			self.date = state.base.form.date
			self.numberOfGames = state.base.form.numberOfGames
			self.preBowl = state.base.form.preBowl
			self.excludeFromStatistics = state.base.form.excludeFromStatistics
			self.hasAlleysEnabled = state.hasAlleysEnabled
			self.hasLanesEnabled = state.hasLanesEnabled
			self.isAlleyPickerPresented = state.isAlleyPickerPresented
			self.isLanePickerPresented = state.isLanePickerPresented
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
			if let lanes = state.base.form.lanePicker.selectedResources?.sorted(by: { $0.label < $1.label }) {
				self.selectedLanes = .init(uniqueElements: lanes)
			} else if let lanes = state.initialLanes {
				self.selectedLanes = lanes
			} else {
				self.selectedLanes = nil
			}
		}
	}

	enum ViewAction: BindableAction {
		case didAppear
		case setAlleyPicker(isPresented: Bool)
		case setLanePicker(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: /SeriesEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					DatePicker(
						Strings.Series.Properties.date,
						selection: viewStore.binding(\.$date),
						displayedComponents: [.date]
					)
					.datePickerStyle(.graphical)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				if viewStore.hasAlleysEnabled {
					Section(Strings.Series.Properties.alley) {
						NavigationLink(
							destination: ResourcePickerView(
								store: store.scope(
									state: \.base.form.alleyPicker,
									action: /SeriesEditor.Action.InternalAction.alleyPicker
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

						if viewStore.hasLanesEnabled {
							NavigationLink(
								destination: ResourcePickerView(
									store: store.scope(
										state: \.base.form.lanePicker,
										action: /SeriesEditor.Action.InternalAction.lanePicker
									)
								) { lane in
									LaneRow(lane: lane)
								},
								isActive: viewStore.binding(
									get: \.isLanePickerPresented,
									send: ViewAction.setLanePicker(isPresented:)
								)
							) {
								LabeledContent(
									Strings.Series.Editor.Fields.Alley.lanes,
									value: viewStore.laneLabels
								)
							}
							.disabled(viewStore.selectedAlley == nil)
						}
					}
					.listRowBackground(Color(uiColor: .secondarySystemBackground))
				}

				Section {
					Toggle(
						Strings.Series.Editor.Fields.PreBowl.label,
						isOn: viewStore.binding(
							get: { $0.preBowl == .preBowl },
							send: { ViewAction.set(\.$preBowl, $0 ? .preBowl : .regularPlay) }
						)
					)
				} header: {
					Text(Strings.Series.Editor.Fields.PreBowl.title)
				} footer: {
					Text(Strings.Series.Editor.Fields.PreBowl.help)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				Section {
					Toggle(
						Strings.Series.Editor.Fields.ExcludeFromStatistics.label,
						isOn: viewStore.binding(
							get: { $0.excludeFromStatistics == .exclude || $0.preBowl == .preBowl },
							send: { ViewAction.set(\.$excludeFromStatistics, $0 ? .exclude : .include) }
						)
					).disabled(viewStore.preBowl == .preBowl)
				} header: {
					Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.title)
				} footer: {
					if viewStore.preBowl == .preBowl {
						Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenPreBowl)
							.foregroundColor(.appWarning)
					} else {
						Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.help)
					}
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.onAppear { viewStore.send(.didAppear) }
		}
	}
}

extension SeriesEditor.State {
	var view: SeriesEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.date = newValue.date
			self.base.form.numberOfGames = newValue.numberOfGames
			self.base.form.preBowl = newValue.preBowl
			self.base.form.excludeFromStatistics = newValue.excludeFromStatistics
		}
	}
}

extension SeriesEditor.Action {
	init(action: SeriesEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .setAlleyPicker(isPresented):
			self = .view(.setAlleyPicker(isPresented: isPresented))
		case let .setLanePicker(isPresented):
			self = .view(.setLanePicker(isPresented: isPresented))
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

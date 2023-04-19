import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>

	struct ViewState: Equatable {
		@BindingState var date: Date
		@BindingState var numberOfGames: Int
		@BindingState var preBowl: Series.PreBowl
		@BindingState var excludeFromStatistics: Series.ExcludeFromStatistics
		let location: Alley.Summary?
		let lanes: IdentifiedArrayOf<Lane.Summary>

		let hasSetNumberOfGames: Bool
		let excludeLeagueFromStatistics: League.ExcludeFromStatistics

		let isAlleyPickerPresented: Bool
		let isLanePickerPresented: Bool

		let hasAlleysEnabled: Bool
		let hasLanesEnabled: Bool

		let isEditing: Bool

		var laneLabels: String {
			lanes.isEmpty ? Strings.none : lanes.map(\.label).joined(separator: ", ")
		}

		init(state: SeriesEditor.State) {
			self.date = state.date
			self.numberOfGames = state.numberOfGames
			self.preBowl = state.preBowl
			self.excludeFromStatistics = state.excludeFromStatistics
			self.location = state.location
			self.lanes = state.lanes

			self.hasSetNumberOfGames = state.league.numberOfGames != nil
			self.excludeLeagueFromStatistics = state.league.excludeFromStatistics

			self.isAlleyPickerPresented = state.isAlleyPickerPresented
			self.isLanePickerPresented = state.isLanePickerPresented
			self.hasAlleysEnabled = state.hasAlleysEnabled
			self.hasLanesEnabled = state.hasLanesEnabled

			switch state._form.value {
			case .create: self.isEditing = false
			case .edit: self.isEditing = true
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
			FormView(store: store.scope(state: \.form, action: /SeriesEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					Stepper(
						Strings.Series.Editor.Fields.numberOfGames(viewStore.numberOfGames),
						value: viewStore.binding(\.$numberOfGames),
						in: League.NUMBER_OF_GAMES_RANGE
					)
					.disabled(viewStore.hasSetNumberOfGames || viewStore.isEditing)

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
									state: \.alleyPicker,
									action: /SeriesEditor.Action.InternalAction.alleyPicker
								)
							) { alley in
								Alley.View(alley: alley)
							},
							isActive: viewStore.binding(
								get: \.isAlleyPickerPresented,
								send: ViewAction.setAlleyPicker(isPresented:)
							)
						) {
							LabeledContent(
								Strings.Series.Properties.alley,
								value: viewStore.location?.name ?? Strings.none
							)
						}

						if viewStore.hasLanesEnabled {
							NavigationLink(
								destination: ResourcePickerView(
									store: store.scope(
										state: \.lanePicker,
										action: /SeriesEditor.Action.InternalAction.lanePicker
									)
								) { lane in
									Lane.View(lane: lane)
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
							.disabled(viewStore.location == nil)
						}
					}
					.listRowBackground(Color(uiColor: .secondarySystemBackground))
				}

				Section {
					Toggle(
						Strings.Series.Editor.Fields.PreBowl.label,
						isOn: viewStore.binding(
							get: { $0.preBowl == .preBowl },
							send: { ViewAction.set(\.$preBowl, $0 ? .preBowl : .regular) }
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
							get: {
								$0.excludeLeagueFromStatistics == .exclude ||
								$0.preBowl == .preBowl ||
								$0.excludeFromStatistics == .exclude
							},
							send: { ViewAction.set(\.$excludeFromStatistics, $0 ? .exclude : .include) }
						)
					).disabled(viewStore.preBowl == .preBowl || viewStore.excludeLeagueFromStatistics == .exclude)
				} header: {
					Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.title)
				} footer: {
					excludeFromStatisticsHelp(viewStore)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.onAppear { viewStore.send(.didAppear) }
		}
	}

	@ViewBuilder private func excludeFromStatisticsHelp(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		switch viewStore.excludeLeagueFromStatistics {
		case .exclude:
			Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenLeagueExcluded)
				.foregroundColor(.appWarning)
		case .include:
			switch viewStore.preBowl {
			case .preBowl:
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenPreBowl)
					.foregroundColor(.appWarning)
			case .regular:
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.help)
			}
		}
	}
}

extension SeriesEditor.State {
	var view: SeriesEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.date = newValue.date
			self.numberOfGames = newValue.numberOfGames
			self.preBowl = newValue.preBowl
			self.excludeFromStatistics = newValue.excludeFromStatistics
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

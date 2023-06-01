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

		let hasSetNumberOfGames: Bool
		let excludeLeagueFromStatistics: League.ExcludeFromStatistics

		let isAlleyPickerPresented: Bool

		let hasAlleysEnabled: Bool

		let isEditing: Bool

		init(state: SeriesEditor.State) {
			self.date = state.date
			self.numberOfGames = state.numberOfGames
			self.preBowl = state.preBowl
			self.excludeFromStatistics = state.excludeFromStatistics
			self.location = state.location

			self.hasSetNumberOfGames = state.league.numberOfGames != nil
			self.excludeLeagueFromStatistics = state.league.excludeFromStatistics

			self.isAlleyPickerPresented = state.isAlleyPickerPresented
			self.hasAlleysEnabled = state.hasAlleysEnabled

			switch state._form.value {
			case .create: self.isEditing = false
			case .edit: self.isEditing = true
			}
		}
	}

	enum ViewAction: BindableAction {
		case setAlleyPicker(isPresented: Bool)
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
					}
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
			}
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
		case let .setAlleyPicker(isPresented):
			self = .view(.setAlleyPicker(isPresented: isPresented))
		case .binding(let action):
			self = .binding(action.pullback(\SeriesEditor.State.view))
		}
	}
}

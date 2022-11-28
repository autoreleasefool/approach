import AlleyPickerFeature
import BaseFormFeature
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct LeagueEditorView: View {
	let store: StoreOf<LeagueEditor>

	struct ViewState: Equatable {
		@BindableState var name: String
		@BindableState var recurrence: League.Recurrence
		@BindableState var gamesPerSeries: LeagueEditor.GamesPerSeries
		@BindableState var numberOfGames: Int
		@BindableState var additionalPinfall: String
		@BindableState var additionalGames: String
		@BindableState var hasAdditionalPinfall: Bool
		let selectedAlley: Alley?
		let hasAlleysEnabled: Bool

		init(state: LeagueEditor.State) {
			self.name = state.base.form.name
			self.recurrence = state.base.form.recurrence
			self.gamesPerSeries = state.base.form.gamesPerSeries
			self.numberOfGames = state.base.form.numberOfGames
			self.additionalGames = state.base.form.additionalGames
			self.additionalPinfall = state.base.form.additionalPinfall
			self.hasAdditionalPinfall = state.base.form.hasAdditionalPinfall
			self.hasAlleysEnabled = state.hasAlleysEnabled
			if let id = state.base.form.alleyPicker.selected.first {
				self.selectedAlley = state.base.form.alleyPicker.alleys?[id: id]
			} else {
				self.selectedAlley = nil
			}
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<LeagueEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeagueEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: LeagueEditor.Action.form)) {
				detailsSection(viewStore)
				recurrenceSection(viewStore)
				gamesSection(viewStore)
				additionalPinfallSection(viewStore)
			}
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			TextField(Strings.Leagues.Editor.Fields.Details.name, text: viewStore.binding(\.$name))
				.textContentType(.name)
			if viewStore.hasAlleysEnabled {
				NavigationLink(
					destination: AlleyPickerView(
						store: store.scope(
							state: \.base.form.alleyPicker,
							action: LeagueEditor.Action.alleyPicker
						)
					)
				) {
					LabeledContent(
						Strings.Leagues.Editor.Fields.Details.BowlingAlley.title,
						value: viewStore.selectedAlley?.name ?? Strings.Leagues.Editor.Fields.Details.BowlingAlley.none
					)
				}
			}
		} header: {
			Text(Strings.Leagues.Editor.Fields.Details.title)
		} footer: {
			if viewStore.hasAlleysEnabled {
				Text(Strings.Leagues.Editor.Fields.Details.BowlingAlley.help)
			}
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func recurrenceSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Leagues.Editor.Fields.Recurrence.title,
				selection: viewStore.binding(\.$recurrence)
			) {
				ForEach(League.Recurrence.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
		} footer: {
			Text(Strings.Leagues.Editor.Fields.Recurrence.help(League.Recurrence.repeating, League.Recurrence.oneTimeEvent))
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func gamesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Leagues.Editor.Fields.NumberOfGames.title,
				selection: viewStore.binding(\.$gamesPerSeries)
			) {
				ForEach(LeagueEditor.GamesPerSeries.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
			.disabled(viewStore.recurrence == .oneTimeEvent)

			if viewStore.gamesPerSeries == .static {
				Stepper(
					"\(viewStore.numberOfGames)",
					value: viewStore.binding(\.$numberOfGames),
					in: League.NUMBER_OF_GAMES_RANGE
				)
			}
		} footer: {
			Text(
				Strings.Leagues.Editor.Fields.NumberOfGames.help(
					LeagueEditor.GamesPerSeries.static,
					LeagueEditor.GamesPerSeries.dynamic
				)
			)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func additionalPinfallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Toggle(
				Strings.Leagues.Editor.Fields.AdditionalPinfall.title,
				isOn: viewStore.binding(\.$hasAdditionalPinfall)
			)
			.toggleStyle(SwitchToggleStyle())

			if viewStore.hasAdditionalPinfall {
				TextField(
					Strings.Leagues.Editor.Fields.AdditionalPinfall.pinfall,
					text: viewStore.binding(\.$additionalPinfall)
				)
				.keyboardType(.numberPad)
				TextField(
					Strings.Leagues.Editor.Fields.AdditionalPinfall.games,
					text: viewStore.binding(\.$additionalGames)
				)
				.keyboardType(.numberPad)
			}
		} header: {
			Text(Strings.Leagues.Editor.Fields.AdditionalPinfall.games)
		} footer: {
			Text(Strings.Leagues.Editor.Fields.AdditionalPinfall.help)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}
}

extension LeagueEditor.State {
	var view: LeagueEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
			self.base.form.recurrence = newValue.recurrence
			self.base.form.gamesPerSeries = newValue.gamesPerSeries
			self.base.form.numberOfGames = newValue.numberOfGames
			self.base.form.hasAdditionalPinfall = newValue.hasAdditionalPinfall
			self.base.form.additionalGames = newValue.additionalGames
			self.base.form.additionalPinfall = newValue.additionalPinfall
		}
	}
}

extension LeagueEditor.Action {
	init(action: LeagueEditorView.ViewAction) {
		switch action {
		case let.binding(action):
			self = .binding(action.pullback(\LeagueEditor.State.view))
		}
	}
}

#if DEBUG
struct LeagueEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationView {
			LeagueEditorView(
				store: .init(
					initialState: .init(
						bowler: .init(id: UUID(), name: "Joseph"),
						mode: .create,
						hasAlleysEnabled: true
					),
					reducer: LeagueEditor()
				)
			)
		}
	}
}
#endif

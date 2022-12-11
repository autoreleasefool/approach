import BaseFormFeature
import ComposableArchitecture
import ResourcePickerFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

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
		let isAlleyPickerPresented: Bool

		init(state: LeagueEditor.State) {
			self.name = state.base.form.name
			self.recurrence = state.base.form.recurrence
			self.gamesPerSeries = state.base.form.gamesPerSeries
			self.numberOfGames = state.base.form.numberOfGames
			self.additionalGames = state.base.form.additionalGames
			self.additionalPinfall = state.base.form.additionalPinfall
			self.hasAdditionalPinfall = state.base.form.hasAdditionalPinfall
			self.hasAlleysEnabled = state.hasAlleysEnabled
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
			.interactiveDismissDisabled(viewStore.isAlleyPickerPresented)
			.onAppear { viewStore.send(.loadInitialData) }
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			TextField(Strings.Editor.Fields.Details.name, text: viewStore.binding(\.$name))
				.textContentType(.name)
			if viewStore.hasAlleysEnabled {
				NavigationLink(
					destination: ResourcePickerView(
						store: store.scope(
							state: \.base.form.alleyPicker,
							action: LeagueEditor.Action.alleyPicker
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
						Strings.League.Properties.alley,
						value: viewStore.selectedAlley?.name ?? Strings.none
					)
				}
			}
		} header: {
			Text(Strings.Editor.Fields.Details.title)
		} footer: {
			if viewStore.hasAlleysEnabled {
				Text(Strings.League.Editor.Fields.Alley.help)
			}
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func recurrenceSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.League.Properties.recurrence,
				selection: viewStore.binding(\.$recurrence)
			) {
				ForEach(League.Recurrence.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.League.Editor.Fields.Recurrence.help(League.Recurrence.repeating, League.Recurrence.oneTimeEvent))
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func gamesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.League.Properties.numberOfGames,
				selection: viewStore.binding(\.$gamesPerSeries)
			) {
				ForEach(LeagueEditor.GamesPerSeries.allCases) {
					Text(String(describing: $0)).tag($0)
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
				Strings.League.Editor.Fields.NumberOfGames.help(
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
				Strings.League.Editor.Fields.AdditionalPinfall.title,
				isOn: viewStore.binding(\.$hasAdditionalPinfall)
			)
			.toggleStyle(SwitchToggleStyle())

			if viewStore.hasAdditionalPinfall {
				TextField(
					Strings.League.Properties.additionalPinfall,
					text: viewStore.binding(\.$additionalPinfall)
				)
				.keyboardType(.numberPad)
				TextField(
					Strings.League.Properties.additionalGames,
					text: viewStore.binding(\.$additionalGames)
				)
				.keyboardType(.numberPad)
			}
		} footer: {
			Text(Strings.League.Editor.Fields.AdditionalPinfall.help)
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
		case .loadInitialData:
			self = .loadInitialData
		case let .setAlleyPicker(isPresented):
			self = .setAlleyPicker(isPresented: isPresented)
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

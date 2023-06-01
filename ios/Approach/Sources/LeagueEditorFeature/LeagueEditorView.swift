import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct LeagueEditorView: View {
	let store: StoreOf<LeagueEditor>

	struct ViewState: Equatable {
		@BindingState var name: String
		@BindingState var recurrence: League.Recurrence
		@BindingState var numberOfGames: Int?
		@BindingState var additionalPinfall: Int?
		@BindingState var additionalGames: Int?
		@BindingState var excludeFromStatistics: League.ExcludeFromStatistics

		@BindingState var gamesPerSeries: LeagueEditor.GamesPerSeries
		@BindingState var hasAdditionalPinfall: Bool

		let location: Alley.Summary?
		let hasAlleysEnabled: Bool
		let isAlleyPickerPresented: Bool

		let isEditing: Bool

		init(state: LeagueEditor.State) {
			self.name = state.name
			self.recurrence = state.recurrence
			self.numberOfGames = state.numberOfGames
			self.additionalGames = state.additionalGames
			self.additionalPinfall = state.additionalPinfall
			self.excludeFromStatistics = state.excludeFromStatistics

			self.gamesPerSeries = state.gamesPerSeries
			self.hasAdditionalPinfall = state.hasAdditionalPinfall

			self.hasAlleysEnabled = state.hasAlleysEnabled
			self.isAlleyPickerPresented = state.isAlleyPickerPresented
			self.location = state.location

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

	public init(store: StoreOf<LeagueEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeagueEditor.Action.init) { viewStore in
			FormView(store: store.scope(state: \.form, action: /LeagueEditor.Action.InternalAction.form)) {
				detailsSection(viewStore)
				recurrenceSection(viewStore)
				locationSection(viewStore)
				statisticsSection(viewStore)
				gamesSection(viewStore)
				additionalPinfallSection(viewStore)
			}
			.interactiveDismissDisabled(viewStore.isAlleyPickerPresented)
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(Strings.Editor.Fields.Details.name, text: viewStore.binding(\.$name))
		}
	}

	@ViewBuilder private func recurrenceSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		if !viewStore.isEditing {
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
				Text(Strings.League.Editor.Fields.Recurrence.help(League.Recurrence.repeating, League.Recurrence.once))
			}
		}
	}

	@ViewBuilder private func locationSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		// TODO: better show the location section when recurrence is toggled
		if viewStore.hasAlleysEnabled && viewStore.recurrence == .once {
			Section {
				NavigationLink(
					destination: ResourcePickerView(
						store: store.scope(
							state: \.alleyPicker,
							action: /LeagueEditor.Action.InternalAction.alleyPicker
						)
					) {
						Alley.View(alley: $0)
					},
					isActive: viewStore.binding(
						get: \.isAlleyPickerPresented,
						send: ViewAction.setAlleyPicker(isPresented:)
					)
				) {
					LabeledContent(
						Strings.League.Properties.alley,
						value: viewStore.location?.name ?? Strings.none
					)
				}
			} header: {
				Text(Strings.League.Editor.Fields.Alley.title)
			} footer: {
				Text(Strings.League.Editor.Fields.Alley.help)
			}
		}
	}

	private func statisticsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Toggle(
				Strings.League.Editor.Fields.ExcludeFromStatistics.label,
				isOn: viewStore.binding(
					get: { $0.excludeFromStatistics == .exclude },
					send: { ViewAction.set(\.$excludeFromStatistics, $0 ? .exclude : .include) }
				)
			)
		} header: {
			Text(Strings.League.Editor.Fields.ExcludeFromStatistics.title)
		} footer: {
			Text(Strings.League.Editor.Fields.ExcludeFromStatistics.help)
		}
	}

	@ViewBuilder private func gamesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		if !viewStore.isEditing {
			Section {
				Picker(
					Strings.League.Properties.numberOfGames,
					selection: viewStore.binding(\.$gamesPerSeries)
				) {
					ForEach(LeagueEditor.GamesPerSeries.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
				.disabled(viewStore.recurrence == .once)

				if viewStore.gamesPerSeries == .static {
					Stepper(
						"\(viewStore.numberOfGames ?? 1)",
						value: viewStore.binding(
							get: { $0.numberOfGames ?? 1 },
							send: { .binding(.set(\.$numberOfGames, $0)) }
						),
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
		}
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
					text: viewStore.binding(
						get: { String($0.additionalPinfall ?? 0) },
						send: { .binding(.set(\.$additionalPinfall, Int($0))) }
					)
				)
				.keyboardType(.numberPad)

				TextField(
					Strings.League.Properties.additionalGames,
					text: viewStore.binding(
						get: { String($0.additionalGames ?? 0) },
						send: { .binding(.set(\.$additionalGames, Int($0))) }
					)
				)
				.keyboardType(.numberPad)
			}
		} footer: {
			Text(Strings.League.Editor.Fields.AdditionalPinfall.help)
		}
	}
}

extension LeagueEditor.State {
	var view: LeagueEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.name = newValue.name
			self.recurrence = newValue.recurrence
			self.numberOfGames = newValue.numberOfGames
			self.additionalGames = newValue.additionalGames
			self.additionalPinfall = newValue.additionalPinfall
			self.excludeFromStatistics = newValue.excludeFromStatistics
			self.hasAdditionalPinfall = newValue.hasAdditionalPinfall
			self.gamesPerSeries = newValue.gamesPerSeries
		}
	}
}

extension LeagueEditor.Action {
	init(action: LeagueEditorView.ViewAction) {
		switch action {
		case let .setAlleyPicker(isPresented):
			self = .view(.setAlleyPicker(isPresented: isPresented))
		case let.binding(action):
			self = .binding(action.pullback(\LeagueEditor.State.view))
		}
	}
}

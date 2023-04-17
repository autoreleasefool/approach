import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct LeagueEditorView: View {
	let store: StoreOf<LeagueEditor>

	struct ViewState: Equatable {
		@BindingState var league: League.Editable
		@BindingState var gamesPerSeries: LeagueEditor.GamesPerSeries
		@BindingState var hasAdditionalPinfall: Bool

		let selectedAlley: Alley.Summary?
		let hasAlleysEnabled: Bool
		let isAlleyPickerPresented: Bool

		init(state: LeagueEditor.State) {
			self.league = state.base.form.league
			self.gamesPerSeries = state.base.form.gamesPerSeries
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
		case didAppear
		case setAlleyPicker(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<LeagueEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeagueEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: /LeagueEditor.Action.InternalAction.form)) {
				detailsSection(viewStore)
				recurrenceSection(viewStore)
				statisticsSection(viewStore)
				gamesSection(viewStore)
				additionalPinfallSection(viewStore)
			}
			.interactiveDismissDisabled(viewStore.isAlleyPickerPresented)
			.onAppear { viewStore.send(.didAppear) }
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			TextField(Strings.Editor.Fields.Details.name, text: viewStore.binding(\.$league.name))
			if viewStore.hasAlleysEnabled {
				NavigationLink(
					destination: ResourcePickerView(
						store: store.scope(
							state: \.base.form.alleyPicker,
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
				selection: viewStore.binding(\.$league.recurrence)
			) {
				ForEach(League.Recurrence.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} footer: {
			Text(Strings.League.Editor.Fields.Recurrence.help(League.Recurrence.repeating, League.Recurrence.once))
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func statisticsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Toggle(
				Strings.League.Editor.Fields.ExcludeFromStatistics.label,
				isOn: viewStore.binding(
					get: { $0.league.excludeFromStatistics == .exclude },
					send: { ViewAction.set(\.$league.excludeFromStatistics, $0 ? .exclude : .include) }
				)
			)
		} header: {
			Text(Strings.League.Editor.Fields.ExcludeFromStatistics.title)
		} footer: {
			Text(Strings.League.Editor.Fields.ExcludeFromStatistics.help)
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
			.disabled(viewStore.league.recurrence == .once)

			if viewStore.gamesPerSeries == .static {
				Stepper(
					"\(viewStore.league.numberOfGames ?? 0)",
					value: viewStore.binding(
						get: { $0.league.numberOfGames ?? 0 },
						send: { .binding(.set(\.$league.numberOfGames, $0)) }
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
					text: viewStore.binding(
						get: { String($0.league.additionalPinfall ?? 0) },
						send: { .binding(.set(\.$league.additionalPinfall, Int($0))) }
					)
				)
				.keyboardType(.numberPad)
				
				TextField(
					Strings.League.Properties.additionalGames,
					text: viewStore.binding(
						get: { String($0.league.additionalGames ?? 0) },
						send: { .binding(.set(\.$league.additionalGames, Int($0))) }
					)
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
			self.base.form.league = newValue.league
			self.base.form.gamesPerSeries = newValue.gamesPerSeries
			self.base.form.hasAdditionalPinfall = newValue.hasAdditionalPinfall
		}
	}
}

extension LeagueEditor.Action {
	init(action: LeagueEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .setAlleyPicker(isPresented):
			self = .view(.setAlleyPicker(isPresented: isPresented))
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
						bowler: UUID(),
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

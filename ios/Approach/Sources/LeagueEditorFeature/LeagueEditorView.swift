import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct LeagueEditorView: View {
	let store: StoreOf<LeagueEditor>

	struct ViewState: Equatable {
		let name: String
		let recurrence: League.Recurrence
		let numberOfGames: Int?
		let additionalPinfall: Int?
		let additionalGames: Int?
		let excludeFromStatistics: League.ExcludeFromStatistics

		let gamesPerSeries: LeagueEditor.GamesPerSeries
		let hasAdditionalPinfall: Bool

		let shouldShowLocationSection: Bool
		let location: Alley.Summary?
		let hasAlleysEnabled: Bool

		let isEditing: Bool
		let isDismissDisabled: Bool

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
			self.isDismissDisabled = state.alleyPicker != nil
			self.location = state.location
			self.shouldShowLocationSection = state.shouldShowLocationSection

			switch state._form.value {
			case .create: self.isEditing = false
			case .edit: self.isEditing = true
			}
		}
	}

	enum ViewAction {
		case didTapAlley
		case didChangeName(String)
		case didChangeRecurrence(League.Recurrence)
		case didChangeNumberOfGames(Int)
		case didChangeAdditionalPinfall(Int?)
		case didChangeAdditionalGames(Int?)
		case didChangeExcludeFromStatistics(League.ExcludeFromStatistics)
		case didChangeGamesPerSeries(LeagueEditor.GamesPerSeries)
		case didChangeHasAdditionalPinfall(Bool)
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
			.interactiveDismissDisabled(viewStore.isDismissDisabled)
		}
		.navigationDestination(
			store: store.scope(state: \.$alleyPicker, action: { .internal(.alleyPicker($0)) })
		) { store in
			ResourcePickerView(store: store) { alley in
				Alley.View(alley: alley)
			}
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: viewStore.binding(get: \.name, send: ViewAction.didChangeName)
			)
		}
	}

	@ViewBuilder private func recurrenceSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		if !viewStore.isEditing {
			Section {
				Picker(
					Strings.League.Properties.recurrence,
					selection: viewStore.binding(get: \.recurrence, send: ViewAction.didChangeRecurrence)
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
		if viewStore.hasAlleysEnabled && viewStore.shouldShowLocationSection {
			Section {
				Button { viewStore.send(.didTapAlley) } label: {
					LabeledContent(
						Strings.League.Properties.alley,
						value: viewStore.location?.name ?? Strings.none
					)
				}
				.buttonStyle(.navigation)
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
					send: { ViewAction.didChangeExcludeFromStatistics($0 ? .exclude : .include) }
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
					selection: viewStore.binding(get: \.gamesPerSeries, send: ViewAction.didChangeGamesPerSeries)
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
							send: ViewAction.didChangeNumberOfGames
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
				isOn: viewStore.binding(get: \.hasAdditionalPinfall, send: ViewAction.didChangeHasAdditionalPinfall)
			)
			.toggleStyle(SwitchToggleStyle())

			if viewStore.hasAdditionalPinfall {
				TextField(
					Strings.League.Properties.additionalPinfall,
					text: viewStore.binding(
						get: { String($0.additionalPinfall ?? 0) },
						send: { ViewAction.didChangeAdditionalPinfall(Int($0)) }
					)
				)
				.keyboardType(.numberPad)

				TextField(
					Strings.League.Properties.additionalGames,
					text: viewStore.binding(
						get: { String($0.additionalGames ?? 0) },
						send: { ViewAction.didChangeAdditionalGames(Int($0)) }
					)
				)
				.keyboardType(.numberPad)
			}
		} footer: {
			Text(Strings.League.Editor.Fields.AdditionalPinfall.help)
		}
	}
}

extension LeagueEditor.Action {
	init(action: LeagueEditorView.ViewAction) {
		switch action {
		case .didTapAlley:
			self = .view(.didTapAlley)
		case let .didChangeName(name):
			self = .view(.didChangeName(name))
		case let .didChangeRecurrence(recurrence):
			self = .view(.didChangeRecurrence(recurrence))
		case let .didChangeNumberOfGames(numberOfGames):
			self = .view(.didChangeNumberOfGames(numberOfGames))
		case let .didChangeAdditionalPinfall(pinFall):
			self = .view(.didChangeAdditionalPinfall(pinFall))
		case let .didChangeAdditionalGames(games):
			self = .view(.didChangeAdditionalGames(games))
		case let .didChangeExcludeFromStatistics(exclude):
			self = .view(.didChangeExcludeFromStatistics(exclude))
		case let .didChangeGamesPerSeries(gamesPerSeries):
			self = .view(.didChangeGamesPerSeries(gamesPerSeries))
		case let .didChangeHasAdditionalPinfall(hasAdditionalPinfall):
			self = .view(.didChangeHasAdditionalPinfall(hasAdditionalPinfall))
		}
	}
}

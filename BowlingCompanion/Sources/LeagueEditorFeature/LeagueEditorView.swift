import BaseFormFeature
import ComposableArchitecture
import SharedModelsLibrary
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

		init(state: LeagueEditor.State) {
			self.name = state.base.form.name
			self.recurrence = state.base.form.recurrence
			self.gamesPerSeries = state.base.form.gamesPerSeries
			self.numberOfGames = state.base.form.numberOfGames
			self.additionalGames = state.base.form.additionalGames
			self.additionalPinfall = state.base.form.additionalPinfall
			self.hasAdditionalPinfall = state.base.form.hasAdditionalPinfall
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
		Section("Details") {
			TextField("Name", text: viewStore.binding(\.$name))
				.textContentType(.name)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func recurrenceSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Repeat?",
				selection: viewStore.binding(\.$recurrence)
			) {
				ForEach(League.Recurrence.allCases) {
					Text($0.description).tag($0.rawValue)
				}
			}
		} footer: {
			Text(
				"Choose '\(League.Recurrence.repeating)' for leagues that happen semi-frequently, such as once a week, or " +
				"choose '\(League.Recurrence.oneTimeEvent)' for tournaments and one-off events."
			)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func gamesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Number of games",
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
				"Choose '\(LeagueEditor.GamesPerSeries.static)' if you always play the same number of games each series, " +
				"or '\(LeagueEditor.GamesPerSeries.dynamic)' to choose the number of games each time you bowl."
			)
		}
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}

	private func additionalPinfallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Toggle(
				"Include additional pinfall?",
				isOn: viewStore.binding(\.$hasAdditionalPinfall)
			)
			.toggleStyle(SwitchToggleStyle())

			if viewStore.hasAdditionalPinfall {
				TextField(
					"Additional Pinfall",
					text: viewStore.binding(\.$additionalPinfall)
				)
				.keyboardType(.numberPad)
				TextField(
					"Additional Games",
					text: viewStore.binding(\.$additionalGames)
				)
				.keyboardType(.numberPad)
			}
		} header: {
			Text("Additional Games")
		} footer: {
			Text(
				"If you're starting recording partway through the season, you can add missing pinfall " +
				"here to ensure your average in the app matches the average provided by your league."
			)
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
						mode: .create
					),
					reducer: LeagueEditor()
				)
			)
		}
	}
}
#endif

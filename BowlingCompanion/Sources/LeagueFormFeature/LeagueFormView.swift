import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct LeagueFormView: View {
	let store: StoreOf<LeagueForm>

	struct ViewState: Equatable {
		let name: String
		let recurrence: League.Recurrence
		let gamesPerSeries: LeagueForm.GamesPerSeries
		let numberOfGames: Int
		let additionalPinfall: String
		let additionalGames: String
		let hasAdditionalPinfall: Bool
		let isLoading: Bool
		let navigationTitle: String
		let saveButtonDisabled: Bool
		let dismissDisabled: Bool
		let discardButtonEnabled: Bool
		let showDeleteButton: Bool

		init(state: LeagueForm.State) {
			self.name = state.name
			self.recurrence = state.recurrence
			self.gamesPerSeries = state.gamesPerSeries
			self.numberOfGames = state.numberOfGames
			self.additionalGames = state.additionalGames
			self.additionalPinfall = state.additionalPinfall
			self.hasAdditionalPinfall = state.hasAdditionalPinfall
			self.isLoading = state.isLoading
			self.saveButtonDisabled = !state.canSave
			self.dismissDisabled = state.hasChanges || state.isLoading
			self.discardButtonEnabled = state.hasChanges && !state.isLoading

			switch state.mode {
			case .create:
				self.navigationTitle = "Create League"
				self.showDeleteButton = false
			case let .edit(league):
				self.navigationTitle = "Edit \(league.name)"
				self.showDeleteButton = true
			}
		}
	}

	enum ViewAction {
		case nameChange(String)
		case recurrenceChange(League.Recurrence)
		case gamesPerSeriesChange(LeagueForm.GamesPerSeries)
		case numberOfGamesChange(Int)
		case additionalGamesChange(String)
		case additionalPinfallChange(String)
		case setHasAdditionalPinfall(enabled: Bool)
		case saveButtonTapped
		case deleteButtonTapped
		case discardButtonTapped
	}

	public init(store: StoreOf<LeagueForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeagueForm.Action.init) { viewStore in
			Form {
				if viewStore.isLoading {
					ProgressView()
				}

				detailsSection(viewStore)
				recurrenceSection(viewStore)
				gamesSection(viewStore)
				additionalPinfallSection(viewStore)
			}
			.navigationTitle(viewStore.navigationTitle)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button("Save") { viewStore.send(.saveButtonTapped) }
						.disabled(viewStore.saveButtonDisabled)
				}

				if viewStore.discardButtonEnabled {
					ToolbarItem(placement: .navigationBarLeading) {
						Button("Discard") { viewStore.send(.discardButtonTapped) }
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: LeagueForm.Action.alert),
				dismiss: .dismissed
			)
			.interactiveDismissDisabled(viewStore.dismissDisabled)
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			TextField("Name", text: viewStore.binding(get: \.name, send: ViewAction.nameChange))
		}
		.disabled(viewStore.isLoading)
	}

	private func recurrenceSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Repeat?",
				selection: viewStore.binding(get: \.recurrence, send: ViewAction.recurrenceChange)
			) {
				ForEach(League.Recurrence.allCases) {
					Text($0.rawValue).tag($0)
				}
			}
		} footer: {
			Text(
				"Choose '\(League.Recurrence.repeating)' for leagues that happen semi-frequently, such as once a week, or " +
				"choose '\(League.Recurrence.oneTimeEvent)' for tournaments and one-off events."
			)
		}
		.disabled(viewStore.isLoading)
	}

	private func gamesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				"Number of games",
				selection: viewStore.binding(get: \.gamesPerSeries, send: ViewAction.gamesPerSeriesChange)
			) {
				ForEach(LeagueForm.GamesPerSeries.allCases) {
					Text($0.rawValue).tag($0)
				}
			}
			.disabled(viewStore.recurrence == .oneTimeEvent)

			if viewStore.gamesPerSeries == .static {
				Stepper(
					"\(viewStore.numberOfGames)",
					value: viewStore.binding(get: \.numberOfGames, send: ViewAction.numberOfGamesChange),
					in: 1...40
				)
			}
		} footer: {
			Text(
				"Choose '\(LeagueForm.GamesPerSeries.static)' if you always play the same number of games each series, " +
				"or '\(LeagueForm.GamesPerSeries.dynamic)' to choose the number of games each time you bowl."
			)
		}
		.disabled(viewStore.isLoading)
	}

	private func additionalPinfallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Toggle(
				"Include additional pinfall?",
				isOn: viewStore.binding(get: \.hasAdditionalPinfall, send: ViewAction.setHasAdditionalPinfall(enabled:))
			)
			.toggleStyle(SwitchToggleStyle())

			if viewStore.hasAdditionalPinfall {
				TextField(
					"Additional Pinfall",
					text: viewStore.binding(get: \.additionalPinfall, send: ViewAction.additionalPinfallChange)
				)
				TextField(
					"Additional Games",
					text: viewStore.binding(get: \.additionalGames, send: ViewAction.additionalGamesChange)
				)
			}
		} header: {
			Text("Additional Games")
		} footer: {
			Text(
				"If you're starting recording partway through the season, you can add missing pinfall " +
				"here to ensure your average in the app matches the average provided by your league."
			)
		}
		.disabled(viewStore.isLoading)
	}
}

extension LeagueForm.Action {
	init(action: LeagueFormView.ViewAction) {
		switch action {
		case .nameChange(let string):
			self = .nameChange(string)
		case .recurrenceChange(let recurrence):
			self = .recurrenceChange(recurrence)
		case let .gamesPerSeriesChange(gamesPerSeries):
			self = .gamesPerSeriesChange(gamesPerSeries)
		case .numberOfGamesChange(let int):
			self = .numberOfGamesChange(int)
		case .additionalGamesChange(let int):
			self = .additionalGamesChange(int)
		case .additionalPinfallChange(let int):
			self = .additionalPinfallChange(int)
		case .setHasAdditionalPinfall(let enabled):
			self = .setHasAdditionalPinfall(enabled: enabled)
		case .saveButtonTapped:
			self = .saveButtonTapped
		case .deleteButtonTapped:
			self = .deleteButtonTapped
		case .discardButtonTapped:
			self = .discardButtonTapped
		}
	}
}

#if DEBUG
struct LeagueFormViewPreviews: PreviewProvider {
	static var previews: some View {
		LeagueFormView(
			store: .init(
				initialState: .init(
					bowler: .init(id: UUID(), name: "Joseph", createdAt: Date(), lastModifiedAt: Date()),
					mode: .create
				),
				reducer: LeagueForm()
			)
		)
	}
}
#endif

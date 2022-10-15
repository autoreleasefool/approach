import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct LeagueFormView: View {
	let store: StoreOf<LeagueForm>

	struct ViewState: Equatable {
		let name: String
		let recurrence: League.Recurrence
		let numberOfGames: Int
		let additionalPinfall: String
		let additionalGames: String
		let hasAdditionalPinfall: Bool
		let isSaving: Bool
		let navigationTitle: String
		let saveButtonDisabled: Bool

		init(state: LeagueForm.State) {
			self.name = state.name
			self.recurrence = state.recurrence
			self.numberOfGames = state.numberOfGames
			self.additionalGames = state.additionalGames
			self.additionalPinfall = state.additionalPinfall
			self.hasAdditionalPinfall = state.hasAdditionalPinfall
			self.isSaving = state.isSaving
			self.navigationTitle = state.mode == .create ? "Create League" : "Edit League"
			self.saveButtonDisabled = state.isSaving || state.name.isEmpty
		}
	}

	enum ViewAction {
		case nameChange(String)
		case recurrenceChange(League.Recurrence)
		case numberOfGamesChange(Int)
		case additionalGamesChange(String)
		case additionalPinfallChange(String)
		case setHasAdditionalPinfall(enabled: Bool)
		case saveButtonTapped
	}

	public init(store: StoreOf<LeagueForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeagueForm.Action.init) { viewStore in
			Form {
				if viewStore.isSaving {
					ProgressView()
				}

				Section("Details") {
					TextField("Name", text: viewStore.binding(get: \.name, send: ViewAction.nameChange))
					Stepper(
						"\(viewStore.numberOfGames)",
						value: viewStore.binding(get: \.numberOfGames, send: ViewAction.numberOfGamesChange),
						in: 1...40
					)
				}
				.disabled(viewStore.isSaving)

				Section {
					Picker(
						"Repeat?",
						selection: viewStore.binding(get: \.recurrence, send: ViewAction.recurrenceChange)
					) {
						ForEach(League.Recurrence.allCases) {
							Text($0.description).tag($0)
						}
					}
				} footer: {
					Text("Choose 'Repeats' for leagues that happen semi-frequently, such as once a week. Choose 'Once' for tournaments and one-off events that will only be 1 series.")
				}
				.disabled(viewStore.isSaving)

				Section {
					Toggle(
						"Include additional pinfall?",
						isOn: viewStore.binding(get: \.hasAdditionalPinfall, send: ViewAction.setHasAdditionalPinfall(enabled:)))
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
					Text("If you're starting recording partway through the season, you can add missing pinfall here to ensure your average in the app matches the average provided by your league.")
				}
			}
			.navigationTitle(viewStore.navigationTitle)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button("Save") { viewStore.send(.saveButtonTapped) }
						.disabled(viewStore.saveButtonDisabled)
				}
			}
		}
	}
}

extension LeagueForm.Action {
	init(action: LeagueFormView.ViewAction) {
		switch action {
		case .nameChange(let string):
			self = .nameChange(string)
		case .recurrenceChange(let recurrence):
			self = .recurrenceChange(recurrence)
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
		}
	}
}

import ComposableArchitecture
import Foundation
import LeaguesDataProviderInterface
import RegexBuilder
import SharedModelsLibrary

public struct LeagueForm: ReducerProtocol {
	public struct State: Equatable {
		public var bowler: Bowler
		public var mode: Mode
		public var name = ""
		public var recurrence: League.Recurrence = .repeating
		public var gamesPerSeries: GamesPerSeries = .static
		public var numberOfGames = League.DEFAULT_NUMBER_OF_GAMES
		public var additionalPinfall = ""
		public var additionalGames = ""
		public var hasAdditionalPinfall = false
		public var isLoading = false
		public var alert: AlertState<AlertAction>?

		public var hasChanges: Bool {
			self != .init(bowler: bowler, mode: mode)
		}

		public var canSave: Bool {
			!isLoading && hasChanges && !name.isEmpty
		}

		public init(bowler: Bowler, mode: Mode) {
			self.bowler = bowler
			self.mode = mode
			if case let .edit(league) = mode {
				self.name = league.name
				self.recurrence = league.recurrence
				self.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
				self.gamesPerSeries = league.numberOfGames == nil ? .dynamic : .static
				self.additionalGames = "\(league.additionalGames ?? 0)"
				self.additionalPinfall = "\(league.additionalPinfall ?? 0)"
				self.hasAdditionalPinfall = (league.additionalGames ?? 0) > 0
			}
		}
	}

	public enum GamesPerSeries: String, Equatable, Identifiable, CaseIterable, Codable {
		case `static` = "Constant"
		case dynamic = "Always ask me"

		public var id: String { rawValue }
	}

	public enum Mode: Equatable {
		case create
		case edit(League)
	}

	public enum Action: Equatable {
		case nameChange(String)
		case recurrenceChange(League.Recurrence)
		case gamesPerSeriesChange(GamesPerSeries)
		case numberOfGamesChange(Int)
		case additionalPinfallChange(String)
		case additionalGamesChange(String)
		case setHasAdditionalPinfall(enabled: Bool)
		case saveButtonTapped
		case saveLeagueResult(TaskResult<League>)
		case deleteLeagueResult(TaskResult<Bool>)
		case discardButtonTapped
		case deleteButtonTapped
		case alert(AlertAction)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.leaguesDataProvider) var leaguesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .nameChange(name):
				state.name = name
				return .none

			case let .recurrenceChange(recurrence):
				state.recurrence = recurrence
				if state.recurrence == .oneTimeEvent {
					state.gamesPerSeries = .static
				}
				return .none

			case let .gamesPerSeriesChange(gamesPerSeries):
				state.gamesPerSeries = gamesPerSeries
				return .none

			case let .numberOfGamesChange(numberOfGames):
				state.numberOfGames = numberOfGames
				return .none

			case let .additionalGamesChange(additionalGames):
				state.additionalGames = additionalGames.replacing(#/\D+/#, with: "")
				return .none

			case let .additionalPinfallChange(additionalPinfall):
				state.additionalPinfall = additionalPinfall.replacing(#/\D+/#, with: "")
				return .none

			case let .setHasAdditionalPinfall(enabled):
				state.hasAdditionalPinfall = enabled
				return .none

			case .saveButtonTapped:
				guard state.canSave else { return .none }
				state.isLoading = true

				switch state.mode {
				case .create:
					let league = state.league(id: uuid(), createdAt: date(), lastModifiedAt: date())
					return .task {
						return await .saveLeagueResult(TaskResult {
							try await leaguesDataProvider.create(league)
							return league
						})
					}
				case let .edit(original):
					let league = state.league(id: original.id, createdAt: original.createdAt, lastModifiedAt: date())
					return .task {
						return await .saveLeagueResult(.init {
							try await leaguesDataProvider.update(league)
							return league
						})
					}
				}


			case .saveLeagueResult(.success):
				return .none

			case .saveLeagueResult(.failure):
				// TODO: show error to user for failed save to db
				state.isLoading = false
				return .none

			case .deleteButtonTapped:
				state.alert = self.buildDeleteAlert(state: state)
				return .none

			case .alert(.deleteButtonTapped):
				guard case let .edit(league) = state.mode else { return .none }
				state.isLoading = true
				return .task {
					await .deleteLeagueResult(TaskResult {
						try await leaguesDataProvider.delete(league)
						return true
					})
				}

			case .deleteLeagueResult(.success):
				return .none

			case .deleteLeagueResult(.failure):
				// TODO: show error to user for failed delete
				return .none

			case .discardButtonTapped:
				state.alert = self.discardAlert
				return .none

			case .alert(.discardButtonTapped):
				state = .init(bowler: state.bowler, mode: state.mode)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none
			}
		}
	}
}

extension LeagueForm.State {
	func league(id: UUID, createdAt: Date, lastModifiedAt: Date) -> League {
		let numberOfGames = self.gamesPerSeries == .static ? self.numberOfGames : nil
		let additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
		let additionalPinfall: Int?
		if let additionalGames {
			additionalPinfall = hasAdditionalPinfall && additionalGames > 0 ? Int(self.additionalPinfall) : nil
		} else {
			additionalPinfall = nil
		}
		return .init(
			bowlerId: bowler.id,
			id: id,
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			createdAt: createdAt,
			lastModifiedAt: lastModifiedAt
		)
	}
}

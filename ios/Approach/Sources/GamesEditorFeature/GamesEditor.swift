import BowlersRepositoryInterface
import ComposableArchitecture
import Dependencies
import EquatableLibrary
import FeatureActionLibrary
import Foundation
import FramesRepositoryInterface
import GamesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetFeature
import ScoringServiceInterface
import StringsLibrary
import SwiftUI

public struct GamesEditor: Reducer {
	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.frames) var frames
	@Dependency(\.games) var games
	@Dependency(\.scoringService) var scoringService

	public var body: some Reducer<State, Action> {
		Scope(state: \.gamesHeader, action: /Action.internal..Action.InternalAction.gamesHeader) {
			GamesHeader()
		}

		Scope(state: \.ballPicker, action: /Action.internal..Action.InternalAction.ballPicker) {
			BallPicker()
		}

		Scope(state: \.opponentPicker, action: /Action.internal..Action.InternalAction.opponentPicker) {
			ResourcePicker { _ in bowlers.opponents(ordered: .byName) }
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .merge(
						loadBowlers(state: &state),
						loadGameDetails(state: &state)
					)

				case let .didAdjustBackdropSize(newSize):
					state.backdropSize = newSize
					switch state.sheetDetent {
					case .large, .medium:
						state.isScoreSheetVisible = false
					default:
						state.isScoreSheetVisible = true
					}
					return .none

				case let .didChangeDetent(newDetent):
					state.sheetDetent = newDetent
					return .task {
						try await clock.sleep(for: .milliseconds(25))
						return .internal(.adjustBackdrop)
					}

				case .didDismissOpenSheet:
					state.sheet.finishTransition()
					return .none

				case let .setGameDetails(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .gameDetails)
					return .none

				case let .setBallPicker(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .ballPicker)
					return .none

				case let .setGamesSettings(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .settings)
					return .none

				case let .setOpponentPicker(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .opponentPicker)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .bowlersResponse(.success(bowlers)):
					state.elementsRefreshing.remove(.bowlers)
					state.bowlers = .init(uniqueElements: bowlers)
					return .none

				case .bowlersResponse(.failure):
					// TODO: handle failure loading bowler
					state.elementsRefreshing.remove(.bowlers)
					return .none

				case let .framesResponse(.success(frames)):
					guard frames.first?.gameId == state.currentGameId else {
						// TODO: log error that unexpected frames loaded (should be cancelled in flight)
						return .none
					}

					state.currentFrameIndex = frames.firstIndex { $0.hasUntouchedRoll } ?? 0
					state.currentRollIndex = frames[state.currentFrameIndex].firstUntouchedRoll ?? 0

					state.frames = frames
					state.frames![state.currentFrameIndex].guaranteeRollExists(upTo: state.currentRollIndex)
					state._frameEditor = .init(currentRollIndex: state.currentRollIndex, frame: state.frames![state.currentFrameIndex])

					state._rollEditor = .init(
						ballRolled: state.frames![state.currentFrameIndex].rolls[state.currentRollIndex].bowlingBall,
						didFoul: state.frames![state.currentFrameIndex].rolls[state.currentRollIndex].roll.didFoul
					)
					state.elementsRefreshing.remove(.frames)
					return updateScoreSheet(from: state)

				case .framesResponse(.failure):
					// TODO: handle error loading frames
					state.elementsRefreshing.remove(.frames)
					return .none

				case .frameUpdateError:
					// TODO: handle error saving frame
					return .none

				case let .gameResponse(.success(game)):
					guard state.currentGameId == game?.id, let game else { return .none }
					state._gameDetails = .init(game: game)
					state.game = game
					state.elementsRefreshing.remove(.game)
					return .none

				case .gameResponse(.failure):
					// TODO: handle error loading game
					state.elementsRefreshing.remove(.game)
					return .none

				case .gameUpdateError:
					// TODO: handle error saving game
					return .none

				case let .calculatedScore(score):
					state.score = score
					switch state.game?.scoringMethod {
					case .none, .manual:
						return .none
					case .byFrame:
						state.game?.score = score.last?.score ?? 0
						return save(game: state.game)
					}

				case .adjustBackdrop:
					state.willAdjustLaneLayoutAt = date()
					return .none

				case let .opponentPicker(action):
					return reduce(into: &state, opponentPickerAction: action)

				case let .gameDetails(action):
					return reduce(into: &state, gameDetailsAction: action)

				case let .ballPicker(action):
					return reduce(into: &state, ballPickerAction: action)

				case let .frameEditor(action):
					return reduce(into: &state, frameEditorAction: action)

				case let .scoreSheet(action):
					return reduce(into: &state, scoreSheetAction: action)

				case let .rollEditor(action):
					return reduce(into: &state, rollEditorAction: action)

				case let .gamesHeader(action):
					return reduce(into: &state, gamesHeaderAction: action)

				case let .gamesSettings(action):
					return reduce(into: &state, gamesSettingsAction: action)

				case let .gameDetailsHeader(action):
					return reduce(into: &state, gamesDetailsHeaderAction: action)
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.gameDetails, action: /Action.internal..Action.InternalAction.gameDetails) {
			GameDetails()
		}
		.ifLet(\.gameDetailsHeader, action: /Action.internal..Action.InternalAction.gameDetailsHeader) {
			GameDetailsHeader()
		}
		.ifLet(\.gamesSettings, action: /Action.internal..Action.InternalAction.gamesSettings) {
			GamesSettings()
		}
		.ifLet(\.frameEditor, action: /Action.internal..Action.InternalAction.frameEditor) {
			FrameEditor()
		}
		.ifLet(\.rollEditor, action: /Action.internal..Action.InternalAction.rollEditor) {
			RollEditor()
		}
		.ifLet(\.scoreSheet, action: /Action.internal..Action.InternalAction.scoreSheet) {
			ScoreSheet()
		}
	}
}

extension GamesEditor {
	public struct State: Equatable {
		public var sheet: SheetState = .presenting(.gameDetails)
		public var sheetDetent: PresentationDetent = .height(.zero)
		public var willAdjustLaneLayoutAt: Date
		public var backdropSize: CGSize = .zero
		public var isScoreSheetVisible = true

		public var elementsRefreshing: Set<RefreshableElements> = [.bowlers, .frames, .game]
		var isEditable: Bool { elementsRefreshing.isEmpty && game?.locked != .locked }

		// IDs for games being edited (and their corresponding bowlers)
		public var bowlerIds: [Bowler.ID]
		public var bowlerGameIds: [Bowler.ID: [Game.ID]]

		// ID Details for the current entity being edited
		public var currentBowlerId: Bowler.ID
		public var currentGameId: Game.ID
		public var currentFrameIndex: Int = 0
		public var currentRollIndex: Int = 0

		// Loaded details for the current entity being edited
		public var bowlers: IdentifiedArrayOf<Bowler.Summary>?
		public var game: Game.Edit?
		public var frames: [Frame.Edit]?
		public var score: [ScoreStep]?

		var numberOfGames: Int { bowlerGameIds.first!.value.count }
		var currentGameIndex: Int { bowlerGameIds[currentBowlerId]!.firstIndex(of: currentGameId)! }
		var currentBowlerIndex: Int { bowlerIds.firstIndex(of: currentBowlerId)! }

		public var _gameDetails: GameDetails.State?
		public var _frameEditor: FrameEditor.State?
		public var _rollEditor: RollEditor.State?
		public var _ballPicker: BallPicker.State
		public var _opponentPicker: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State

		public init(bowlerIds: [Bowler.ID], bowlerGameIds: [Bowler.ID: [Game.ID]]) {
			precondition(bowlerGameIds.allSatisfy { $0.value.count == bowlerGameIds.first!.value.count })
			self.bowlerIds = bowlerIds
			self.bowlerGameIds = bowlerGameIds

			let currentBowlerId = bowlerIds.first!
			self.currentBowlerId = currentBowlerId
			self.currentGameId = bowlerGameIds[currentBowlerId]!.first!
			self._ballPicker = .init(forBowler: currentBowlerId, selected: nil)
			self._opponentPicker = .init(
				selected: [],
				query: .init(()),
				limit: 1,
				showsCancelHeaderButton: false
			)

			@Dependency(\.date) var date
			self.willAdjustLaneLayoutAt = date()
		}
	}
}

extension GamesEditor {
	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didChangeDetent(PresentationDetent)
			case didAdjustBackdropSize(CGSize)
			case didDismissOpenSheet
			case setGameDetails(isPresented: Bool)
			case setBallPicker(isPresented: Bool)
			case setOpponentPicker(isPresented: Bool)
			case setGamesSettings(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case bowlersResponse(TaskResult<[Bowler.Summary]>)
			case framesResponse(TaskResult<[Frame.Edit]>)
			case gameResponse(TaskResult<Game.Edit?>)
			case frameUpdateError(AlwaysEqual<Error>)
			case gameUpdateError(AlwaysEqual<Error>)

			case calculatedScore([ScoreStep])
			case adjustBackdrop

			case gamesSettings(GamesSettings.Action)
			case gamesHeader(GamesHeader.Action)
			case gameDetailsHeader(GameDetailsHeader.Action)
			case gameDetails(GameDetails.Action)
			case frameEditor(FrameEditor.Action)
			case rollEditor(RollEditor.Action)
			case scoreSheet(ScoreSheet.Action)
			case ballPicker(BallPicker.Action)
			case opponentPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}
}

extension GamesEditor {
	public enum RefreshableElements {
		case bowlers
		case game
		case frames
	}

	enum CancelID { case observation }
}

extension GamesEditor {
	func loadBowlers(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.bowlers)
		return .task { [bowlerIds = state.bowlerIds] in
			await .internal(.bowlersResponse(TaskResult {
				try await bowlers.summaries(forIds: bowlerIds)
			}))
		}
	}

	func loadGameDetails(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.frames)
		state.elementsRefreshing.insert(.game)
		return .merge(
			.task { [gameId = state.currentGameId] in
				return await .internal(.framesResponse(TaskResult {
					try await frames.frames(forGame: gameId) ?? []
				}))
			},
			.task { [gameId = state.currentGameId] in
				await .internal(.gameResponse(TaskResult {
					try await games.edit(gameId)
				}))
			}
		)
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}

	func updateScoreSheet(from state: State) -> Effect<Action> {
		guard let frames = state.frames else { return .none }
		return .task {
			let steps = await scoringService.calculateScoreWithSteps(for: frames.map { $0.rolls })
			return .internal(.calculatedScore(steps))
		}
	}

	func save(frame: Frame.Edit?) -> Effect<Action> {
		guard let frame else { return .none }
		return .run { send in
			do {
				try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
				try await frames.update(frame)
			} catch {
				await send(.internal(.frameUpdateError(.init(error))))
			}
		}.cancellable(id: frame.id, cancelInFlight: true)
	}

	func save(game: Game.Edit?) -> Effect<Action> {
		guard let game else { return .none }
		return .run { send in
			do {
				try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
				try await games.update(game)
			} catch {
				await send(.internal(.gameUpdateError(.init(error))))
			}
		}.cancellable(id: game.id, cancelInFlight: true)
	}
}

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Opponent.title : Strings.Opponent.List.title
	}
}

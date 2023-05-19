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

	public enum RefreshableElements {
		case bowlers
		case game
		case frames
	}

	enum CancelID { case observation }

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

				case let .opponentPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.sheet.hide(.opponentPicker)
						guard state.isEditable && state.gameDetails != nil else { return .none }
						return state.gameDetails!.setMatchPlay(opponent: state._opponentPicker.selectedResources?.first)
							.map { .internal(.gameDetails($0)) }
					}

				case let .ballPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinish:
						state.sheet.hide(.ballPicker)
						return save(frame: state.frames?[state.currentFrameIndex])
					}

				case let .frameEditor(.delegate(delegateAction)):
					switch delegateAction {
					case .didEditFrame:
						return .merge(
							save(frame: state.frames?[state.currentFrameIndex]),
							updateScoreSheet(from: state)
						)
					}

				case let .scoreSheet(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .rollEditor(.delegate(delegateAction)):
					switch delegateAction {
					case .didTapBall:
						state.sheet.transition(to: .ballPicker)
						return .none

					case .didEditRoll:
						return .merge(
							save(frame: state.frames?[state.currentFrameIndex]),
							updateScoreSheet(from: state)
						)
					}

				case let .gamesHeader(.delegate(delegateAction)):
					switch delegateAction {
					case .didCloseEditor:
						// TODO: close the games editor
						return .none

					case .didOpenSettings:
						state.sheet.transition(to: .settings)
						return .none
					}

				case let .gamesSettings(.delegate(delegateAction)):
					switch delegateAction {
					case let .switchedGame(to):
						state.currentGameId = state.bowlerGameIds[state.currentBowlerId]![to]
						return loadGameDetails(state: &state)

					case let .switchedBowler(to):
						state.currentGameId = state.bowlerGameIds[to]![state.currentGameIndex]
						state.currentBowlerId = to
						return loadGameDetails(state: &state)

					case let .movedBowlers(source, destination):
						state.bowlers?.move(fromOffsets: source, toOffset: destination)
						state.bowlerIds.move(fromOffsets: source, toOffset: destination)
						return .none

					case .didFinish:
						state.sheet.hide(.settings)
						return .none
					}

				case let .gameDetails(.delegate(delegateAction)):
					switch delegateAction {
					case .didRequestOpponentPicker:
						state.sheet.transition(to: .opponentPicker)
						return .none

					case .didEditGame:
						return save(game: state.game)

					case .didClearManualScore:
						return updateScoreSheet(from: state)
					}

				case let .gameDetailsHeader(.delegate(delegateAction)):
					switch delegateAction {
					case let .didProceed(next):
						switch next {
						case let .bowler(_, id):
							let gameIndex = state.currentGameIndex
							state.currentBowlerId = id
							state.currentGameId = state.bowlerGameIds[id]![gameIndex]
							return loadGameDetails(state: &state)
						case let .frame(frameIndex):
							state.currentFrameIndex = frameIndex
							state.currentRollIndex = 0
							state.frames?[frameIndex].guaranteeRollExists(upTo: 0)
							return save(frame: state.frames?[frameIndex])
						case let .roll(rollIndex):
							state.currentRollIndex = rollIndex
							state.frames?[state.currentFrameIndex].guaranteeRollExists(upTo: rollIndex)
							return save(frame: state.frames?[state.currentFrameIndex])
						case let .game(_, bowler, game):
							state.currentBowlerId = bowler
							state.currentGameId = game
							return loadGameDetails(state: &state)
						}
					}

				case .gameDetails(.internal), .gameDetails(.view):
					return .none

				case .gameDetailsHeader(.internal), .gameDetailsHeader(.view):
					return .none

				case .scoreSheet(.view), .scoreSheet(.internal):
					return .none

				case .rollEditor(.view), .rollEditor(.internal):
					return .none

				case .frameEditor(.view), .frameEditor(.internal):
					return .none

				case .ballPicker(.view), .ballPicker(.internal):
					return .none

				case .gamesHeader(.view), .gamesHeader(.internal):
					return .none

				case .gamesSettings(.view), .gamesSettings(.internal):
					return .none

				case .opponentPicker(.view), .opponentPicker(.internal):
					return .none
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

	private func loadBowlers(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.bowlers)
		return .task { [bowlerIds = state.bowlerIds] in
			await .internal(.bowlersResponse(TaskResult {
				try await bowlers.summaries(forIds: bowlerIds)
			}))
		}
	}

	private func loadGameDetails(state: inout State) -> Effect<Action> {
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

	private func updateScoreSheet(from state: State) -> Effect<Action> {
		guard let frames = state.frames else { return .none }
		return .task {
			let steps = await scoringService.calculateScoreWithSteps(for: frames.map { $0.rolls })
			return .internal(.calculatedScore(steps))
		}
	}

	private func save(frame: Frame.Edit?) -> Effect<Action> {
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

	private func save(game: Game.Edit?) -> Effect<Action> {
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

// MARK: - GamesHeader

extension GamesEditor.State {
	var gamesHeader: GamesHeader.State {
		get { .init(currentGameIndex: currentGameIndex) }
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set {}
	}
}

// MARK: - GamesSettings

extension GamesEditor.State {
	var gamesSettings: GamesSettings.State? {
		get {
			guard let bowlers else { return nil }
			return .init(
				bowlers: bowlers,
				currentBowlerId: currentBowlerId,
				numberOfGames: numberOfGames,
				gameIndex: currentGameIndex
			)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set { }
	}
}

// MARK: - GameDetailsHeader

extension GamesEditor.State {
	var gameDetailsHeader: GameDetailsHeader.State? {
		get {
			guard let game, let frames else { return nil }
			let next: GameDetailsHeader.State.NextElement?
			if !Frame.Roll.isLast(currentRollIndex) &&
					(Frame.isLast(currentFrameIndex) || !frames[currentFrameIndex].deck(forRoll: currentRollIndex).isFullDeck) {
				next = .roll(rollIndex: currentRollIndex + 1)
			} else if bowlerIds.count > 1 {
				let nextBowlerId = bowlerIds[(currentBowlerIndex + 1) % bowlerIds.count]
				next = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
			} else if !Frame.isLast(currentFrameIndex) {
				next = .frame(frameIndex: currentFrameIndex + 1)
			} else {
				next = nil
				// TODO: figure out when to show next game
			}

			return .init(game: game, next: next)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set { }
	}
}

// MARK: - GameDetails

extension GamesEditor.State {
	var gameDetails: GameDetails.State? {
		get {
			guard let _gameDetails, let game else { return nil }
			var gameDetails = _gameDetails
			gameDetails.game = game
			return gameDetails
		}
		set {
			guard isEditable, let newValue, currentGameId == newValue.game.id else { return }
			_gameDetails = newValue
			game = newValue.game
		}
	}
}

// MARK: - FrameEditor

extension GamesEditor.State {
	var frameEditor: FrameEditor.State? {
		get {
			guard let _frameEditor, let frames else { return nil }
			var frameEditor = _frameEditor
			frameEditor.currentRollIndex = currentRollIndex
			frameEditor.frame = frames[currentFrameIndex]
			return frameEditor
		}
		set {
			guard isEditable else { return }
			_frameEditor = newValue
			guard let newValue else { return }
			self.frames?[self.currentFrameIndex] = newValue.frame
		}
	}
}

// MARK: - BallPicker

extension GamesEditor.State {
	var ballPicker: BallPicker.State {
		get {
			var picker = _ballPicker
			picker.forBowler = currentBowlerId
			picker.selected = frames?[currentFrameIndex].rolls[currentRollIndex].bowlingBall?.id
			return picker
		}
		set {
			guard isEditable else { return }
			_ballPicker = newValue
			frames?[currentFrameIndex].setBowlingBall(newValue.selectedBall?.rolled, forRoll: currentRollIndex)
		}
	}
}

// MARK: - OpponentPicker

extension GamesEditor.State {
	var opponentPicker: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State {
		get {
			var picker = _opponentPicker
			picker.initialSelection = Set([game?.matchPlay?.opponent?.id].compactMap { $0 })
			return picker
		}
		set {
			guard isEditable else { return }
			_opponentPicker = newValue
		}
	}
}

// MARK: - RollEditor

extension GamesEditor.State {
	var rollEditor: RollEditor.State? {
		get {
			guard let _rollEditor, let frames else { return nil }
			var rollEditor = _rollEditor
			let currentRoll = frames[currentFrameIndex].rolls[currentRollIndex]
			rollEditor.ballRolled = currentRoll.bowlingBall
			rollEditor.didFoul = currentRoll.roll.didFoul
			return rollEditor
		}
		set {
			guard isEditable else { return }
			_rollEditor = newValue
			guard let newValue else { return }
			frames?[currentFrameIndex].setDidFoul(newValue.didFoul, forRoll: currentRollIndex)
		}
	}
}

// MARK: - Scoresheet

extension GamesEditor.State {
	var scoreSheet: ScoreSheet.State? {
		get {
			guard let score else { return nil }
			return .init(
				steps: score,
				currentFrameIndex: currentFrameIndex,
				currentRollIndex: currentRollIndex
			)
		}
		set {
			guard let newValue else { return }
			currentRollIndex = newValue.currentRollIndex
			currentFrameIndex = newValue.currentFrameIndex
			frames?[currentFrameIndex].guaranteeRollExists(upTo: currentRollIndex)
		}
	}
}

// MARK: - Sheet

extension GamesEditor.State {
	public enum Sheet: Equatable {
		case gameDetails
		case ballPicker
		case settings
		case opponentPicker

		static let `default`: Self = .gameDetails
	}

	public enum SheetState: Equatable {
		case presenting(Sheet)
		case transitioning(to: Sheet)

		mutating func handle(isPresented: Bool, for sheet: Sheet) {
			switch self {
			case .presenting:
				if isPresented {
					self.transition(to: sheet)
				} else {
					self.transition(to: .default)
				}
			case .transitioning:
				break
			}
		}

		mutating func transition(to: Sheet) {
			switch self {
			case .presenting(to):
				break
			case .presenting, .transitioning:
				self = .transitioning(to: to)
			}
		}

		mutating func hide(_ sheet: Sheet) {
			switch self {
			case .presenting(sheet), .transitioning(to: sheet):
				self.transition(to: .default)
			case .presenting, .transitioning:
				break
			}
		}

		mutating func finishTransition() {
			switch self {
			case let .transitioning(to):
				self = .presenting(to)
			case .presenting:
				break
			}
		}
	}
}

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Opponent.title : Strings.Opponent.List.title
	}
}

import ComposableArchitecture
import FeatureActionLibrary
import FramesRepositoryInterface
import ModelsLibrary
import ScoreSheetFeature
import ScoringServiceInterface
import SwiftUI

public struct GamesEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var detent: PresentationDetent = .height(.zero)
		public var sheet: SheetState = .presenting(.gameDetails)
		public var minimumSheetHeight: CGFloat = .zero
//		public var sheetContentHeight: CGFloat = .zero

		public var bowlers: IdentifiedArrayOf<Bowler.Summary>
		public var bowlerGames: [Bowler.ID: [Game.ID]]
		public var frames: [Frame.Edit]?
		public var scoreSteps: [ScoreStep]?

		public var currentBowlerId: Bowler.ID
		public var currentGameId: Game.ID
		public var currentFrameIndex = 0
		public var currentRollIndex = 0

		public var _frameEditor: FrameEditor.State?

		public init(
			bowlers: IdentifiedArrayOf<Bowler.Summary>,
			bowlerGames: [Bowler.ID: [Game.ID]],
			currentBowler: Bowler.ID,
			currentGame: Game.ID
		) {
			precondition(bowlers[id: currentBowler] != nil)
			precondition(bowlerGames[currentBowler]?.contains(currentGame) == true)
			precondition(bowlerGames.allSatisfy { $0.value.count == bowlerGames.first!.value.count })
			self.bowlers = bowlers
			self.bowlerGames = bowlerGames
			self.currentBowlerId = currentBowler
			self.currentGameId = currentGame
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
//			case didMeasureSheetContentHeight(CGFloat)
			case didMeasureScoreSheetHeight(CGFloat)
			case didDismissOpenSheet
			case setGamePicker(isPresented: Bool)
			case setGameDetails(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case switchToBowler(Bowler.ID)
			case switchToGame(Game.ID)
			case framesResponse(TaskResult<[Frame.Edit]>)
			case calculatedScore([ScoreStep])

			case gameIndicator(GameIndicator.Action)
			case gamePicker(GamePicker.Action)
			case frameEditor(FrameEditor.Action)
			case scoreSheet(ScoreSheet.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	struct CancelObservationID {}

	public init() {}

	@Dependency(\.frames) var frames
	@Dependency(\.scoringService) var scoringService

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.gameIndicator, action: /Action.internal..Action.InternalAction.gameIndicator) {
			GameIndicator()
		}

		Scope(state: \.gamePicker, action: /Action.internal..Action.InternalAction.gamePicker) {
			GamePicker()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return loadGameDetails(for: state.currentGameId)


//				case let .didMeasureSheetContentHeight(newHeight):
//					state.sheetContentHeight = newHeight
//					return .none

				case let .didMeasureScoreSheetHeight(newHeight):
					state.minimumSheetHeight = newHeight
					if state.detent == .height(.zero) {
						state.detent = .height(newHeight)
					}
					return .none

				case .didDismissOpenSheet:
					state.sheet.finishTransition()
					return .none

				case let .setGameDetails(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .gameDetails)
					return .none

				case let .setGamePicker(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .gamePicker)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .switchToBowler(bowlerId):
					let gameIndex = state.bowlerGames[state.currentBowlerId]!.firstIndex(of: state.currentGameId)!
					state.currentBowlerId = bowlerId
					state.currentGameId = state.bowlerGames[bowlerId]![gameIndex]
					return loadGameDetails(for: state.currentGameId)

				case let .switchToGame(gameId):
					precondition(state.bowlerGames[state.currentBowlerId]!.contains(gameId))
					state.currentGameId = gameId
					return loadGameDetails(for: state.currentGameId)

				case let .framesResponse(.success(frames)):
					guard frames.first?.gameId == state.currentGameId else {
						// TODO: log error that unexpected frames loaded (should be cancelled in flight)
						return .none
					}

					// TODO: determine which frame and roll to start with
					state.currentFrameIndex = 0
					state.currentRollIndex = 0

					state.frames = frames
					state.frames![state.currentFrameIndex].guaranteeRollExists(upTo: state.currentRollIndex)
					state.frameEditor = .init(currentRollIndex: state.currentRollIndex, frame: state.frames![state.currentFrameIndex])
					return updateScoreSheet(from: state)

				case .framesResponse(.failure):
					// TODO: handle error loading frames
					return .none

				case let .calculatedScore(steps):
					state.scoreSteps = steps
					return .none

				case let .gamePicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinish:
						state.sheet.hide(.gamePicker)
						return .none
					}

				case let .frameEditor(.delegate(delegateAction)):
					switch delegateAction {
					case .didEditFrame:
						return updateScoreSheet(from: state)
					}

				case let .scoreSheet(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .gameIndicator(.delegate(delegateAction)):
					switch delegateAction {
					case .didRequestGamePicker:
						state.sheet.transition(to: .gamePicker)
						return .none
					}

				case .scoreSheet(.view), .scoreSheet(.internal):
					return .none

				case .frameEditor(.view), .frameEditor(.internal):
					return .none

				case .gamePicker(.view), .gamePicker(.internal):
					return .none

				case .gameIndicator(.view), .gameIndicator(.internal):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.frameEditor, action: /Action.internal..Action.InternalAction.frameEditor) {
			FrameEditor()
		}
		.ifLet(\.scoreSheet, action: /Action.internal..Action.InternalAction.scoreSheet) {
			ScoreSheet()
		}
	}

	private func loadGameDetails(for gameId: Game.ID) -> Effect<Action> {
		return .task {
			await .internal(.framesResponse(TaskResult {
				try await frames.frames(forGame: gameId) ?? []
			}))
		}
		.cancellable(id: CancelObservationID.self, cancelInFlight: true)
	}

	private func updateScoreSheet(from state: State) -> Effect<GamesEditor.Action> {
		guard let frames = state.frames else { return .none }
		return .task {
			let steps = await scoringService.calculateScoreWithSteps(for: frames.map { $0.rolls })
			return .internal(.calculatedScore(steps))
		}
	}
}

extension GamesEditor.State {
	var currentBowlerGames: [Game.ID] {
		bowlerGames[currentBowlerId] ?? []
	}

	// MARK: - GameIndicator

	var gameIndicator: GameIndicator.State {
		get { .init(games: currentBowlerGames, selected: currentGameId) }
		set { self.currentGameId = newValue.selected }
	}

	// MARK: - GamePicker

	var gamePicker: GamePicker.State {
		get { .init(games: currentBowlerGames, selected: currentGameId) }
		set { self.currentGameId = newValue.selected }
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
			_frameEditor = newValue
			guard let newValue else { return }
			self.currentRollIndex = newValue.currentRollIndex
			self.frames?[self.currentFrameIndex].guaranteeRollExists(upTo: currentRollIndex)
			self.frames?[self.currentFrameIndex] = newValue.frame
		}
	}
}

// MARK: - Scoresheet

extension GamesEditor.State {
	var scoreSheet: ScoreSheet.State? {
		get {
			guard let scoreSteps else { return nil }
			return .init(
				steps: scoreSteps,
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
		case gamePicker
		case gameDetails

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

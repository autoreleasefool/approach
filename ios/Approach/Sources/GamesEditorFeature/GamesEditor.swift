import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import Dependencies
import EquatableLibrary
import FeatureActionLibrary
import Foundation
import FramesRepositoryInterface
import GamesRepositoryInterface
import GearRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import ScoreSheetFeature
import ScoringServiceInterface
import StringsLibrary
import SwiftUI

public struct GamesEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var sheetDetent: PresentationDetent = .height(.zero)
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

		public var _frameEditor: FrameEditor.State?
		public var _rollEditor: RollEditor.State?
		@PresentationState public var destination: Destination.State?

		public init(bowlerIds: [Bowler.ID], bowlerGameIds: [Bowler.ID: [Game.ID]]) {
			precondition(bowlerGameIds.allSatisfy { $0.value.count == bowlerGameIds.first!.value.count })
			self.bowlerIds = bowlerIds
			self.bowlerGameIds = bowlerGameIds

			let currentBowlerId = bowlerIds.first!
			self.currentBowlerId = currentBowlerId
			self.currentGameId = bowlerGameIds[currentBowlerId]!.first!

			@Dependency(\.date) var date
			self.willAdjustLaneLayoutAt = date()
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didAppear
			case didAdjustBackdropSize(CGSize)
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case bowlersResponse(TaskResult<[Bowler.Summary]>)
			case framesResponse(TaskResult<[Frame.Edit]>)
			case gameResponse(TaskResult<Game.Edit?>)
			case didUpdateFrame(TaskResult<Never>)
			case didUpdateGame(TaskResult<Never>)
			case didUpdateMatchPlay(TaskResult<Never>)

			case didDismissOpenSheet
			case calculatedScore([ScoreStep])
			case adjustBackdrop

			case destination(PresentationAction<Destination.Action>)
			case gamesHeader(GamesHeader.Action)
			case gameDetailsHeader(GameDetailsHeader.Action)
			case frameEditor(FrameEditor.Action)
			case rollEditor(RollEditor.Action)
			case scoreSheet(ScoreSheet.Action)
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

	@Dependency(\.analytics) var analytics
	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.frames) var frames
	@Dependency(\.games) var games
	@Dependency(\.gear) var gear
	@Dependency(\.matchPlays) var matchPlays
	@Dependency(\.scoring) var scoring

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.gamesHeader, action: /Action.internal..Action.InternalAction.gamesHeader) {
			GamesHeader()
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

				case .binding(\.$sheetDetent):
					return .run { send in
						try await clock.sleep(for: .milliseconds(25))
						await send(.internal(.adjustBackdrop))
					}

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didDismissOpenSheet:
					if let game = state.game {
						state.destination = .gameDetails(.init(game: game))
					}
					return .none

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

				case .didUpdateFrame(.failure):
					// TODO: handle error saving frame
					return .none

				case let .gameResponse(.success(game)):
					guard state.currentGameId == game?.id, let game else { return .none }
					state.destination = .gameDetails(.init(game: game))
					state.game = game
					state.elementsRefreshing.remove(.game)
					return .none

				case .gameResponse(.failure):
					// TODO: handle error loading game
					state.elementsRefreshing.remove(.game)
					return .none

				case .didUpdateGame(.failure):
					// TODO: handle error saving game
					return .none

				case .didUpdateMatchPlay(.failure):
					// TODO: handle error saving match play
					return .none

				case let .calculatedScore(score):
					state.score = score
					switch state.game?.scoringMethod {
					case .none, .manual:
						return .none
					case .byFrame:
						state.game?.score = score.gameScore() ?? 0
						return save(game: state.game)
					}

				case .adjustBackdrop:
					state.willAdjustLaneLayoutAt = date()
					return .none

				case let .destination(.presented(.opponentPicker(action))):
					return reduce(into: &state, opponentPickerAction: action)

				case let .destination(.presented(.gearPicker(action))):
					return reduce(into: &state, gearPickerAction: action)

				case let .destination(.presented(.gameDetails(action))):
					return reduce(into: &state, gameDetailsAction: action)

				case let .destination(.presented(.ballPicker(action))):
					return reduce(into: &state, ballPickerAction: action)

				case let .destination(.presented(.settings(action))):
					return reduce(into: &state, gamesSettingsAction: action)

				case let .frameEditor(action):
					return reduce(into: &state, frameEditorAction: action)

				case let .scoreSheet(action):
					return reduce(into: &state, scoreSheetAction: action)

				case let .rollEditor(action):
					return reduce(into: &state, rollEditorAction: action)

				case let .gamesHeader(action):
					return reduce(into: &state, gamesHeaderAction: action)

				case let .gameDetailsHeader(action):
					return reduce(into: &state, gamesDetailsHeaderAction: action)

				case .destination(.dismiss):
					return .send(.internal(.didDismissOpenSheet))
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.gameDetailsHeader, action: /Action.internal..Action.InternalAction.gameDetailsHeader) {
			GameDetailsHeader()
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
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}
}

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Opponent.title : Strings.Opponent.List.title
	}
}

extension Gear.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Gear.title : Strings.Gear.List.title
	}
}

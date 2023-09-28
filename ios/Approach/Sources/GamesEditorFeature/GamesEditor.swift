import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import Dependencies
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import FramesRepositoryInterface
import GamesRepositoryInterface
import GearRepositoryInterface
import MatchPlaysRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import RecentlyUsedServiceInterface
import ResourcePickerLibrary
import ScoreSheetLibrary
import ScoresRepositoryInterface
import StringsLibrary
import SwiftUI
import ToastLibrary

// swiftlint:disable file_length
// swiftlint:disable:next type_body_length
public struct GamesEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var sheetDetent: PresentationDetent = .height(.zero)
		public var willAdjustLaneLayoutAt: Date
		public var backdropSize: CGSize = .zero
		public var gameDetailsHeaderSize: CGSize = .zero
		public var gameDetailsMinimumContentSize: CGSize = .zero
		public var isScoreSheetVisible = true

		public var didPromptLaneDuplication = false
		public var willShowDuplicateLanesAlert = false

		public var elementsRefreshing: Set<RefreshableElements> = [.bowlers, .frames, .game]
		var isEditable: Bool { elementsRefreshing.isEmpty && game?.locked != .locked }

		// IDs for games being edited (and their corresponding bowlers)
		public var bowlerIds: [Bowler.ID]
		public var bowlerGameIds: [Bowler.ID: [Game.ID]]

		// ID Details for the current entity being edited
		public var _currentBowlerId: Bowler.ID
		public var _currentGameId: Game.ID
		@BindingState public var currentFrame: ScoreSheet.Selection = .init(frameIndex: 0, rollIndex: 0)

		// Should only be modified in `GamesEditor.State.setCurrent`
		public var _nextHeaderElement: GameDetailsHeader.State.NextElement?
		public var didChangeBowler: Bool = false

		public var currentBowlerId: Bowler.ID { _currentBowlerId }
		public var currentGameId: Game.ID { _currentGameId }
		public var currentFrameIndex: Int { currentFrame.frameIndex  }
		public var currentRollIndex: Int { currentFrame.rollIndex }
		public var nextHeaderElement: GameDetailsHeader.State.NextElement? { _nextHeaderElement }
		public var forceNextHeaderElementNil: Bool = false

		// Loaded details for the current entity being edited
		public var bowlers: IdentifiedArrayOf<Bowler.Summary>?
		public var game: Game.Edit?
		public var frames: [Frame.Edit]?
		public var score: ScoredGame?

		var numberOfGames: Int { bowlerGameIds.first!.value.count }
		var currentGameIndex: Int { bowlerGameIds[currentBowlerId]!.firstIndex(of: currentGameId)! }
		var currentBowlerIndex: Int { bowlerIds.firstIndex(of: currentBowlerId)! }

		public var _frameEditor: FrameEditor.State = .init()
		public var _rollEditor: RollEditor.State = .init()
		public var _gamesHeader: GamesHeader.State = .init()
		@PresentationState public var destination: Destination.State?

		public var toast: ToastState<ToastAction>?
		public var errors: Errors<ErrorID>.State = .init()

		public init(
			bowlerIds: [Bowler.ID],
			bowlerGameIds: [Bowler.ID: [Game.ID]],
			initialBowlerId: Bowler.ID?,
			initialGameId: Game.ID?
		) {
			precondition(bowlerGameIds.allSatisfy { $0.value.count == bowlerGameIds.first!.value.count })
			self.bowlerIds = bowlerIds
			self.bowlerGameIds = bowlerGameIds

			let currentBowlerId = initialBowlerId ?? bowlerIds.first!
			self._currentBowlerId = currentBowlerId
			self._currentGameId = initialGameId ?? bowlerGameIds[currentBowlerId]!.first!

			@Dependency(\.date) var date
			self.willAdjustLaneLayoutAt = date()
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didFirstAppear
			case didAdjustBackdropSize(CGSize)
			case didDismissGameDetails
			case didDismissOpenSheet
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case bowlersResponse(TaskResult<[Bowler.Summary]>)
			case framesResponse(TaskResult<[Frame.Edit]>)
			case gameResponse(TaskResult<Game.Edit?>)

			case didUpdateFrame(TaskResult<Frame.Edit>)
			case didUpdateGame(TaskResult<Game.Edit>)
			case didUpdateMatchPlay(TaskResult<MatchPlay.Edit>)
			case didDuplicateLanes(TaskResult<Never>)

			case showDuplicateLanesAlert

			case calculatedScore(ScoredGame)
			case adjustBackdrop

			case toast(ToastAction)
			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
			case gamesHeader(GamesHeader.Action)
			case frameEditor(FrameEditor.Action)
			case rollEditor(RollEditor.Action)
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

	public enum ErrorID: Hashable {
		case outdatedFramesLoaded
		case failedToLoadBowler
		case failedToLoadFrames
		case failedToLoadGame
		case failedToSaveGame
		case failedToSaveFrame
		case failedToSaveMatchPlay
		case failedToSaveLanes
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.frames) var frames
	@Dependency(\.games) var games
	@Dependency(\.gear) var gear
	@Dependency(\.matchPlays) var matchPlays
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.scores) var scores

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		// We explicitly handle this action before all others so that we can guarantee we are on a valid frame/roll
		Reduce<State, Action> { state, action in
			guard case .view(.binding(\.$currentFrame)) = action else { return .none }
			state.setCurrent(rollIndex: state.currentFrame.rollIndex, frameIndex: state.currentFrame.frameIndex)
			let currentFrameIndex = state.currentFrameIndex
			let currentRollIndex = state.currentRollIndex
			state.frames?[currentFrameIndex].guaranteeRollExists(upTo: currentRollIndex)
			return .none
		}

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.gamesHeader, action: /Action.internal..Action.InternalAction.gamesHeader) {
			GamesHeader()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
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

				case .didDismissOpenSheet:
					if let game = state.game {
						state.sheetDetent = .medium
						state.destination = .gameDetails(.init(
							gameId: game.id,
							seriesGames: state.currentBowlerGames,
							nextHeaderElement: state.nextHeaderElement,
							didChangeBowler: state.didChangeBowler
						))
						state.didChangeBowler = false
					}
					return .none

				case .didDismissGameDetails:
					if state.willShowDuplicateLanesAlert {
						state.willShowDuplicateLanesAlert = false
						return .run { send in
							try await clock.sleep(for: .milliseconds(25))
							await send(.internal(.showDuplicateLanesAlert))
						}
					} else {
						return .none
					}

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
				case .showDuplicateLanesAlert:
					state.destination = .duplicateLanesAlert(.duplicateLanes)
					return .none

				case let .bowlersResponse(.success(bowlers)):
					state.elementsRefreshing.remove(.bowlers)
					state.bowlers = .init(uniqueElements: bowlers)
					return .none

				case let .bowlersResponse(.failure(error)):
					state.elementsRefreshing.remove(.bowlers)
					return state.errors
						.enqueue(.failedToLoadBowler, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .framesResponse(.success(frames)):
					guard frames.first?.gameId == state.currentGameId else {
						return state.errors
							.enqueue(
								.outdatedFramesLoaded,
								thrownError: GamesEditorError.outdatedFrames(
									forGame: frames.first?.gameId,
									expectedGame: state.currentGameId
								),
								toastMessage: Strings.Error.Toast.failedToLoad
							)
							.map { .internal(.errors($0)) }
					}

					state.frames = frames

					let newFrameIndex = frames.firstIndex { $0.hasUntouchedRoll }
					let newRollIndex = frames[newFrameIndex ?? 0].firstUntouchedRoll ?? 0
					state.hideNextHeaderIfNecessary(updatingRollIndexTo: newRollIndex, frameIndex: newFrameIndex ?? 0)

					state.frames![state.currentFrameIndex].guaranteeRollExists(upTo: state.currentRollIndex)

					state.elementsRefreshing.remove(.frames)
					return .none

				case let .framesResponse(.failure(error)):
					state.elementsRefreshing.remove(.frames)
					return state.errors
						.enqueue(.failedToLoadFrames, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didUpdateFrame(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveFrame, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .gameResponse(.success(game)):
					guard state.currentGameId == game?.id, let game else { return .none }
					switch state.destination {
					case let .gameDetails(gameDetails):
						var details = gameDetails
						if gameDetails.gameId != state.currentGameId {
							let loadEffect = details.loadGameDetails(forGameId: state.currentGameId, didChangeBowler: state.didChangeBowler)
							state.destination = .gameDetails(details)
							return loadEffect
								.map { .internal(.destination(.presented(.gameDetails($0)))) }
						}
					case .none:
						state.destination = .gameDetails(.init(
							gameId: state.currentGameId,
							seriesGames: state.currentBowlerGames,
							nextHeaderElement: state.nextHeaderElement,
							didChangeBowler: state.didChangeBowler
						))
						state.didChangeBowler = false
					case .sheets, .duplicateLanesAlert:
						if state.currentGameId != game.id {
							state.destination = nil
						}
					}
					state.game = game
					state.elementsRefreshing.remove(.game)
					state.hideNextHeaderIfNecessary()
					return .none

				case let .didDuplicateLanes(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveLanes, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .gameResponse(.failure(error)):
					state.elementsRefreshing.remove(.game)
					return state.errors
						.enqueue(.failedToLoadGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didUpdateGame(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .didUpdateMatchPlay(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveMatchPlay, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case .didUpdateFrame(.success), .didUpdateGame(.success), .didUpdateMatchPlay(.success):
					return .none

				case let .calculatedScore(score):
					state.score = score
					switch state.game?.scoringMethod {
					case .none, .manual:
						return .none
					case .byFrame:
						state.game?.score = score.frames.gameScore() ?? 0
						return save(game: state.game)
					}

				case .adjustBackdrop:
					state.willAdjustLaneLayoutAt = date()
					return .none

				case let .destination(.presented(.gameDetails(action))):
					return reduce(into: &state, gameDetailsAction: action)

				case let .destination(.presented(.sheets(.ballPicker(action)))):
					return reduce(into: &state, ballPickerAction: action)

				case let .destination(.presented(.sheets(.settings(action)))):
					return reduce(into: &state, gamesSettingsAction: action)

				case let .destination(.presented(.sheets(.sharing(action)))):
					return reduce(into: &state, sharingAction: action)

				case let .destination(.presented(.duplicateLanesAlert(action))):
					return reduce(into: &state, duplicateLanesAction: action)

				case let .frameEditor(action):
					return reduce(into: &state, frameEditorAction: action)

				case let .rollEditor(action):
					return reduce(into: &state, rollEditorAction: action)

				case let .gamesHeader(action):
					return reduce(into: &state, gamesHeaderAction: action)

				case let .toast(action):
					return reduce(into: &state, toastAction: action)

				case let .errors(action):
					return reduce(into: &state, errorsAction: action)

				case .destination(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.frameEditor, action: /Action.internal..Action.InternalAction.frameEditor) {
			FrameEditor()
		}
		.ifLet(\.rollEditor, action: /Action.internal..Action.InternalAction.rollEditor) {
			RollEditor()
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		GamesEditorAnalyticsReducer()
	}
}

public struct GamesEditorAnalyticsReducer: Reducer {
	public var body: some ReducerOf<GamesEditor> {
		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.destination(.presented(.gameDetails(.delegate(.didEditMatchPlay(.success(matchPlay))))))):
				if matchPlay == nil {
					return Analytics.MatchPlay.Deleted()
				} else {
					return Analytics.MatchPlay.Created()
				}
			case let .internal(.didUpdateFrame(.success(frame))):
				return Analytics.Game.Updated(gameId: frame.gameId)
			case let .internal(.didUpdateGame(.success(game))):
				return Analytics.Game.Updated(gameId: game.id)
			case let .internal(.didUpdateMatchPlay(.success(matchPlay))):
				return Analytics.MatchPlay.Updated(
					withOpponent: matchPlay.opponent != nil,
					withScore: matchPlay.opponentScore != nil,
					withResult: matchPlay.result?.rawValue ?? ""
				)
			default:
				return nil
			}
		}
	}
}

extension Bowler.Summary {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Opponent.title : Strings.Opponent.List.title
	}
}

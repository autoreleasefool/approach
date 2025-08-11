import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import Dependencies
import EquatablePackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import FramesRepositoryInterface
import GamesRepositoryInterface
import GearRepositoryInterface
import MatchPlaysRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import PreferenceServiceInterface
import RecentlyUsedServiceInterface
import ResourcePickerLibrary
import ScoreSheetLibrary
import ScoresRepositoryInterface
import StoreReviewPackageServiceInterface
import StringsLibrary
import SwiftUI
import ToastLibrary

@Reducer
public struct GamesEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {

		// Sizing

		// migrated
		public var sheetDetent: PresentationDetent = .height(40)
		public var gameDetailsHeaderSize: CGSize = .zero
		public var gameDetailsMinimumContentSize: CGSize = .zero

		// migrated
		public var safeAreaInsets = EdgeInsets()
		public var headerContentSize: CGSize = .zero
		public var rollEditorSize: CGSize = .zero
		public var frameContentSize: CGSize = .zero
		public var sheetContentSize: CGSize = .zero
		public var windowContentSize: CGSize = .zero

		// migrated
		var measuredBackdropSize: CGSize {
			let sheetContentSize = sheetDetent == .large ? .zero : sheetContentSize
			return .init(
				width: windowContentSize.width,
				height: max(
					windowContentSize.height
						- sheetContentSize.height
						- headerContentSize.height
						- safeAreaInsets.bottom
						- CGFloat.largeSpacing,
					0
				)
			)
		}

		// migrated
		var backdropImageHeight: CGFloat {
			max(
				measuredBackdropSize.height
					- (isScoreSheetVisible ? frameContentSize.height : 0)
					- headerContentSize.height
					+ rollEditorSize.height,
				0
			)
		}

		// migrated
		public var isFrameDragHintVisible = false
		public var isScoreSheetVisible = true
		public var shouldRequestAppStoreReview: Bool = false

		// migrated
		public var didPromptLaneDuplication = false
		public var willShowDuplicateLanesAlert = false

		// migrated
		public var elementsRefreshing: Set<RefreshableElements> = [.bowlers, .frames, .game]
		var isEditable: Bool { elementsRefreshing.isEmpty && game?.locked != .locked }

		// migrated
		public var bowlerIds: [Bowler.ID]
		public var bowlerGameIds: [Bowler.ID: [Game.ID]]

		// migrated
		public var _currentBowlerId: Bowler.ID
		public var _currentGameId: Game.ID
		public var currentFrame: ScoreSheet.Selection = .init(frameIndex: 0, rollIndex: 0)

		// Should only be modified in `GamesEditor.State.setCurrent`
		public var _nextHeaderElement: GameDetailsHeader.State.NextElement?
		public var didChangeBowler: Bool = false

		// migrated
		public var currentBowlerId: Bowler.ID { _currentBowlerId }
		public var currentGameId: Game.ID { _currentGameId }
		public var currentFrameIndex: Int { currentFrame.frameIndex  }
		public var currentRollIndex: Int { currentFrame.rollIndex }

		public var nextHeaderElement: GameDetailsHeader.State.NextElement? { _nextHeaderElement }
		public var forceNextHeaderElementNilOrNextGame: Bool = false

		// migrated
		public var lastLoadedGameAt: GameLoadDate?

		// migrated
		public var bowlers: IdentifiedArrayOf<Bowler.Summary>?
		public var game: Game.Edit?
		public var frames: [Frame.Edit]?
		public var score: ScoredGame?

		// migrated
		var manualScore: Int? {
			if let game {
				switch game.scoringMethod {
				case .byFrame:
					nil
				case .manual:
					game.score
				}
			} else {
				nil
			}
		}

		var numberOfGames: Int { bowlerGameIds.first!.value.count }

		// migrated
		var currentGameIndex: Int { bowlerGameIds[currentBowlerId]!.firstIndex(of: currentGameId)! }
		var currentBowlerIndex: Int { bowlerIds.firstIndex(of: currentBowlerId)! }

		// migrated
		public var frameEditor: FrameEditor.State = .init()
		public var rollEditor: RollEditor.State = .init()
		public var gamesHeader: GamesHeader.State = .init()

		// migrated
		@Presents public var destination: Destination.State?
		@Presents public var toast: ToastState<ToastAction>?

		public var errors: Errors<ErrorID>.State = .init()

		// migrated
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
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			// migrated
			case onAppear
			case didFirstAppear
			case didChangeSafeAreaInsets(EdgeInsets)
			case didDismissGameDetails
			case didDismissOpenSheet
			case didDismissFrameDragHint
			case didRequestReview
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			// migrated
			case bowlersResponse(Result<[Bowler.Summary], Error>)
			case framesResponse(Result<[Frame.Edit], Error>)
			case gameResponse(Result<Game.Edit?, Error>)
			case didUpdateFrame(Result<Frame.Edit, Error>)
			case didUpdateGame(Result<Game.Edit, Error>)
			case didUpdateMatchPlay(Result<MatchPlay.Edit, Error>)
			case adjustBackdrop
			case didDuplicateLanes(Result<Never, Error>)
			case showDuplicateLanesAlert
			case calculatedScore(ScoredGame)
			case didCalculateHighestScorePossible(Result<Int, Error>)

			// migrated
			case errors(Errors<ErrorID>.Action)
			case toast(PresentationAction<ToastAction>)
			case destination(PresentationAction<Destination.Action>)
			case gamesHeader(GamesHeader.Action)
			case frameEditor(FrameEditor.Action)
			case rollEditor(RollEditor.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum RefreshableElements {
		case bowlers
		case game
		case frames
	}

	enum CancelID: Sendable { case observation }

	public enum ErrorID: Hashable, Sendable {
		// migrated
		case outdatedFramesLoaded
		case failedToLoadBowler
		case failedToLoadFrames
		case failedToLoadGame
		case failedToSaveGame
		case failedToSaveFrame
		case failedToSaveMatchPlay
		case failedToSaveLanes
		case failedToCalculateHighestScore
	}

	public struct GameLoadDate: Equatable {
		let gameId: Game.ID
		let durationWhenLoaded: TimeInterval
		let loadedAt: Date
	}

	public init() {}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(FramesRepository.self) var frames
	@Dependency(GamesRepository.self) var games
	@Dependency(GearRepository.self) var gear
	@Dependency(MatchPlaysRepository.self) var matchPlays
	@Dependency(PreferenceService.self) var preferences
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(ScoresRepository.self) var scores
	@Dependency(\.storeReview) var storeReview

	public var body: some ReducerOf<Self> {
		BindingReducer()

		// We explicitly handle this action before all others so that we can guarantee we are on a valid frame/roll
		Reduce<State, Action> { state, action in
			guard case .binding(\.currentFrame) = action else { return .none }
			state.setCurrent(rollIndex: state.currentFrame.rollIndex, frameIndex: state.currentFrame.frameIndex)
			let currentFrameIndex = state.currentFrameIndex
			let currentRollIndex = state.currentRollIndex
			state.populateFrames(upTo: currentFrameIndex)
			state.frames?[currentFrameIndex].guaranteeRollExists(upTo: currentRollIndex)
			state.syncFrameEditorSharedState()
			state.syncRollEditorSharedState()
			return save(frame: state.frames?[currentFrameIndex])
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		// migrated
		Scope(state: \.gamesHeader, action: \.internal.gamesHeader) {
			GamesHeader()
		}

		// migrated
		Scope(state: \.frameEditor, action: \.internal.frameEditor) {
			FrameEditor()
		}

		// migrated
		Scope(state: \.rollEditor, action: \.internal.rollEditor) {
			RollEditor()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				// migrated
				case .onAppear:
					state.isFrameDragHintVisible = !(preferences.bool(forKey: .gameDidDismissDragHint) ?? false)
					return .none

				// migrated
				case .didFirstAppear:
					return .merge(
						loadBowlers(state: &state),
						loadGameDetails(state: &state)
					)

				// migrated
				case .didRequestReview:
					state.shouldRequestAppStoreReview = false
					return .run { _ in storeReview.didRequestReview() }

				// migrated
				case let .didChangeSafeAreaInsets(newInsets):
					state.safeAreaInsets = newInsets
					return .send(.internal(.adjustBackdrop), animation: .default)

				// migrated
				case .didDismissFrameDragHint:
					state.isFrameDragHintVisible = false
					return .run { _ in preferences.setBool(forKey: .gameDidDismissDragHint, to: true) }

				// migrated
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
					return .send(.internal(.adjustBackdrop), animation: .default)

				// migrated
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
				}

			case let .internal(internalAction):
				switch internalAction {
				// migrated
				case .adjustBackdrop:
					switch state.sheetDetent {
					case .large, .medium:
						state.isScoreSheetVisible = false
					default:
						state.isScoreSheetVisible = true
					}
					return .none

				// migrated
				case .showDuplicateLanesAlert:
					state.destination = .duplicateLanesAlert(.duplicateLanes)
					return .none

				// migrated
				case let .didCalculateHighestScorePossible(.success(highestScore)):
					return state.presentStrikeOutAlert(withFinalScore: highestScore)

				// migrated
				case let .bowlersResponse(.success(bowlers)):
					state.elementsRefreshing.remove(.bowlers)
					state.bowlers = .init(uniqueElements: bowlers)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return .none

				// migrated
				case let .bowlersResponse(.failure(error)):
					state.elementsRefreshing.remove(.bowlers)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return state.errors
						.enqueue(.failedToLoadBowler, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				// migrated
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

					let newFrameIndex = frames.nextIndexToRecord()
					let newRollIndex = frames[newFrameIndex].firstUntouchedRoll ?? 0
					state.hideNextHeaderIfNecessary(updatingRollIndexTo: newRollIndex, frameIndex: newFrameIndex)

					state.frames![state.currentFrameIndex].guaranteeRollExists(upTo: state.currentRollIndex)

					state.elementsRefreshing.remove(.frames)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return .none

				// migrated
				case let .framesResponse(.failure(error)):
					state.elementsRefreshing.remove(.frames)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return state.errors
						.enqueue(.failedToLoadFrames, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				// migrated
				case let .didUpdateFrame(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveFrame, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				// migrated
				case let .gameResponse(.success(game)):
					guard let game, state.currentGameId == game.id else { return .none }
					let loadGameDetailsEffect: Effect<Action>?
					switch state.destination {
					case let .gameDetails(gameDetails):
						var details = gameDetails
						if gameDetails.gameId != state.currentGameId {
							let loadEffect = details.loadGameDetails(forGameId: state.currentGameId, didChangeBowler: state.didChangeBowler)
							details.syncHeader()
							state.destination = .gameDetails(details)
							loadGameDetailsEffect = loadEffect.map { .internal(.destination(.presented(.gameDetails($0)))) }
						} else {
							loadGameDetailsEffect = nil
						}
					case .none:
						state.destination = .gameDetails(.init(
							gameId: state.currentGameId,
							seriesGames: state.currentBowlerGames,
							nextHeaderElement: state.nextHeaderElement,
							didChangeBowler: state.didChangeBowler
						))
						state.didChangeBowler = false
						loadGameDetailsEffect = nil
					case .sheets, .duplicateLanesAlert:
						// TODO: needs to be migrated? maybe not
						if state.currentGameId != game.id {
							state.destination = nil
						}
						loadGameDetailsEffect = nil
					}
					state.game = game
					if state.lastLoadedGameAt?.gameId != state.currentGameId {
						state.lastLoadedGameAt = .init(gameId: game.id, durationWhenLoaded: game.duration, loadedAt: date())
					}
					state.elementsRefreshing.remove(.game)
					state.hideNextHeaderIfNecessary()
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return loadGameDetailsEffect ?? .none

				// migrated
				case let .didDuplicateLanes(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveLanes, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				// migrated
				case let .gameResponse(.failure(error)):
					state.elementsRefreshing.remove(.game)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return state.errors
						.enqueue(.failedToLoadGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				// migrated
				case let .didUpdateGame(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				// migrated
				case let .didUpdateMatchPlay(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveMatchPlay, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				// migrated
				case let .didCalculateHighestScorePossible(.failure(error)):
					return state.errors
						.enqueue(.failedToCalculateHighestScore, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				// migrated
				case .didUpdateFrame(.success), .didUpdateGame(.success), .didUpdateMatchPlay(.success):
					return .none

				// migrated
				case let .calculatedScore(score):
					guard state.currentGameId == score.id else { return .none }
					state.score = score
					switch state.game?.scoringMethod {
					case .none, .manual:
						return .none
					case .byFrame:
						state.game?.score = score.frames.gameScore() ?? 0
						if let lastLoaded = state.lastLoadedGameAt, lastLoaded.gameId == state.game?.id {
							state.game?.duration += lastLoaded.loadedAt.distance(to: date())
						}
						return save(game: state.game, in: state)
					}

				// migrated
				case let .destination(.presented(.gameDetails(action))):
					return reduce(into: &state, gameDetailsAction: action)

				case let .destination(.presented(.sheets(.ballPicker(action)))):
					return reduce(into: &state, ballPickerAction: action)

				case let .destination(.presented(.sheets(.settings(action)))):
					return reduce(into: &state, gamesSettingsAction: action)

				case let .destination(.presented(.sheets(.sharing(action)))):
					return reduce(into: &state, sharingAction: action)

				// migrated
				case let .destination(.presented(.duplicateLanesAlert(action))):
					return reduce(into: &state, duplicateLanesAction: action)

				// migrated
				case let .frameEditor(action):
					return reduce(into: &state, frameEditorAction: action)

				// migrated
				case let .rollEditor(action):
					return reduce(into: &state, rollEditorAction: action)

				// migrated
				case let .gamesHeader(action):
					return reduce(into: &state, gamesHeaderAction: action)

				// migrated
				case let .errors(action):
					return reduce(into: &state, errorsAction: action)

				// migrated
				case let .toast(.presented(action)):
					return reduce(into: &state, toastAction: action)

				// migrated
				case .toast(.dismiss):
					state.toast = nil
					return .none

				// migrated
				case .destination(.dismiss):
					return .none
				}

			// migrated
			case .binding(\.sheetDetent):
				return .run { send in
					try await clock.sleep(for: .milliseconds(25))
					await send(.internal(.adjustBackdrop), animation: .default)
				}

			// migrated
			case .delegate, .binding:
				return .none
			}
		}
		// migrated
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}
		.ifLet(\.$toast, action: \.internal.toast) {}

		// migrated
		GamesEditorAnalyticsReducer()

		// migrated
		GamesEditorErrorHandlerReducer()

		// migrated
		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

@Reducer
public struct GamesEditorErrorHandlerReducer: Reducer, Sendable {
	public var body: some ReducerOf<GamesEditor> {
		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			// migrated
			case let .internal(.bowlersResponse(.failure(error))),
				let .internal(.framesResponse(.failure(error))),
				let .internal(.gameResponse(.failure(error))),
				let .internal(.didUpdateFrame(.failure(error))),
				let .internal(.didUpdateGame(.failure(error))),
				let .internal(.didUpdateMatchPlay(.failure(error))),
				let .internal(.didDuplicateLanes(.failure(error))),
				let .internal(.didCalculateHighestScorePossible(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

@Reducer
public struct GamesEditorAnalyticsReducer: Reducer, Sendable {
	public var body: some ReducerOf<GamesEditor> {
		AnalyticsReducer<State, Action> { _, action in
			switch action {
			// migrated
			case let .internal(.destination(.presented(.gameDetails(.delegate(.didEditMatchPlay(.success(matchPlay))))))):
				if matchPlay == nil {
					return Analytics.MatchPlay.Deleted()
				} else {
					return Analytics.MatchPlay.Created()
				}

			// migrated
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

		// migrated
		GameAnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didUpdateGame(.success(game))):
				return Analytics.Game.Updated(gameId: game.id)
			case let .internal(.didUpdateFrame(.success(frame))):
				return Analytics.Game.Updated(gameId: frame.gameId)
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

import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import FramesRepositoryInterface
import GamesRepositoryInterface
import ModelsLibrary
import ScoresRepositoryInterface
import StringsLibrary

@Reducer
public struct GamesEditorStateLoading: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlerIds) public var bowlerIds: [Bowler.ID]
		@Shared(.bowlerGameIds) public var bowlerGameIds: [Bowler.ID: [Game.ID]]

		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID
		@Shared(.currentGameId) public var currentGameId: Game.ID
		@Shared(.currentFrame) public var currentFrame: Frame.Selection

		@Shared(.bowlers) public var bowlers: IdentifiedArrayOf<Bowler.Summary>?
		@Shared(.game) public var game: Game.Edit?
		@Shared(.frames) public var frames: [Frame.Edit]?
		@Shared(.score) public var score: ScoredGame?

		@Shared(.gameLastLoadedAt) public var gameLastLoadedAt: GameLoadDate?
		@Shared(.isEditable) public var isEditable: Bool

		public var dataLoading = Set(LoadableData.allCases)
		public var errors: Errors<ErrorID>.State = .init()

		func initialize() -> Effect<GamesEditorStateLoading.Action> {
			.send(.internal(.initialize))
		}

		mutating func updateIsEditable() {
			$isEditable.withLock { $0 = dataLoading.isEmpty && game?.locked != .locked }
		}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate {
			case changeCurrentFrame(Frame.Selection)
		}
		@CasePathable
		public enum Internal {
			case initialize

			case bowlerIdDidChange(Bowler.ID)
			case gameIdDidChange(Game.ID)

			case bowlersResponse(Result<[Bowler.Summary], Error>)
			case framesResponse(Result<[Frame.Edit], Error>)
			case gameResponse(Result<Game.Edit?, Error>)
			case didUpdateScore(ScoredGame)

			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum ErrorID: Hashable, Sendable {
		case failedToLoadGame
		case failedToLoadFrames
		case failedToLoadBowlers
		case outdatedFramesLoaded
	}

	public enum CancelID: Sendable { case observation }

	public enum LoadableData: CaseIterable {
		case bowlers
		case game
		case frames
	}

	@Dependency(\.date) var date
	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(FramesRepository.self) var frames
	@Dependency(GamesRepository.self) var games
	@Dependency(ScoresRepository.self) var scores

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case .initialize:
					return .concatenate(
						loadBowlers(state: &state),
						loadGameDetails(state: &state),
						.merge(
							.publisher {
								state.$currentBowlerId.publisher
									.map { .internal(.bowlerIdDidChange($0)) }
							},
							.publisher {
								state.$currentGameId.publisher
									.map { .internal(.gameIdDidChange($0)) }
							}
						)
					)

				case .bowlerIdDidChange:
					// TODO: do we need this?
					return .none

				case .gameIdDidChange:
					return loadGameDetails(state: &state)

				case let .bowlersResponse(.success(bowlers)):
					state.dataLoading.remove(.bowlers)
					state.$bowlers.withLock {
						$0 = .init(uniqueElements: bowlers)
					}
					// TODO: update isEditable
					return .none

				case var .framesResponse(.success(frames)):
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

					let newFrameIndex = frames.nextIndexToRecord()
					let newRollIndex = frames[newFrameIndex].firstUntouchedRoll ?? 0
					frames[newFrameIndex].guaranteeRollExists(upTo: newRollIndex)
					state.$frames.withLock { $0 = frames }

					state.dataLoading.remove(.frames)
					state.updateIsEditable()
					return .send(.delegate(.changeCurrentFrame(Frame.Selection(frameIndex: newFrameIndex, rollIndex: newRollIndex))))

				case let .gameResponse(.success(game)):
					guard let game, state.currentGameId == game.id else { return .none }
					state.$game.withLock { $0 = game }
					state.$gameLastLoadedAt.withLock {
						if $0?.gameId != game.id {
							$0 = .init(gameId: game.id, durationWhenLoaded: game.duration, loadedAt: date())
						}
					}
					state.dataLoading.remove(.game)
					state.updateIsEditable()
					return .none

				case let .didUpdateScore(score):
					guard state.currentGameId == score.id else { return .none }
					state.$score.withLock { $0 = score }
					switch state.game?.scoringMethod {
					case .none, .manual:
						return .none
					case .byFrame:
						state.$game.withLock {
							$0?.score = score.frames.gameScore() ?? 0
							if let lastLoaded = state.gameLastLoadedAt, lastLoaded.gameId == $0?.id {
								$0?.duration += lastLoaded.loadedAt.distance(to: date())
							}
						}
						return .none
					}

				case let .bowlersResponse(.failure(error)):
					state.dataLoading.remove(.bowlers)
					return state.errors
						.enqueue(.failedToLoadBowlers, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .framesResponse(.failure(error)):
					state.dataLoading.remove(.frames)
					return state.errors
						.enqueue(.failedToLoadFrames, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .gameResponse(.failure(error)):
					state.dataLoading.remove(.game)
					return state.errors
						.enqueue(.failedToLoadGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)), .errors(.view), .errors(.internal):
					return .none
				}
			case .view(.doNothing), .delegate:
				return .none
			}
		}.onChange(of: \.dataLoading) { _, dataLoading in
			Reduce<State, Action> { state, action in
				state.$isEditable.withLock {
					$0 = dataLoading.isEmpty && state.game?.locked != .locked
				}

				return .none
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.bowlersResponse(.failure(error))),
				let .internal(.framesResponse(.failure(error))),
				let .internal(.gameResponse(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}

	fileprivate func loadBowlers(state: inout State) -> Effect<Action> {
		state.dataLoading.insert(.bowlers)
		return .run { [bowlerIds = state.bowlerIds] send in
			await send(.internal(.bowlersResponse(Result {
				try await bowlers.summaries(forIds: bowlerIds)
			})))
		}
	}

	fileprivate func loadGameDetails(state: inout State) -> Effect<Action> {
		state.dataLoading.insert(.frames)
		state.dataLoading.insert(.game)
		return .concatenate(
			.run { [gameId = state.currentGameId] send in
				for try await game in self.games.observe(gameId) {
					await send(.internal(.gameResponse(.success(game))))
					break
				}
			} catch: { error, send in
				await send(.internal(.gameResponse(.failure(error))))
			},
			.merge(
				.run { [gameId = state.currentGameId] send in
					for try await scoredGame in self.scores.observeScore(for: gameId) {
						await send(.internal(.didUpdateScore(scoredGame)))
					}
				},
				.run { [gameId = state.currentGameId] send in
					for try await frames in self.frames.observe(gameId) {
						await send(.internal(.framesResponse(.success(frames))))
						break
					}
				} catch: { error, send in
					await send(.internal(.framesResponse(.failure(error))))
				},
				.run { [gameId = state.currentGameId] send in
					for try await game in self.games.observe(gameId) {
						await send(.internal(.gameResponse(.success(game))))
					}
				} catch: { error, send in
					await send(.internal(.gameResponse(.failure(error))))
				}
			)
		)
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}
}

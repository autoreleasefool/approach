import ComposableArchitecture
import FeatureActionLibrary
import FramesRepositoryInterface
import GamesRepositoryInterface
import ModelsLibrary

@Reducer
public struct GamesEditorStateValidation: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlerIds) var bowlerIds: [Bowler.ID]
		@Shared(.bowlerGameIds) var bowlerGameIds: [Bowler.ID: [Game.ID]]

		@Shared(.currentBowlerId) var currentBowlerId: Bowler.ID
		@Shared(.currentGameId) var currentGameId: Game.ID
		@Shared(.currentFrame) var currentFrame: Frame.Selection

		@Shared(.bowlers) var bowlers: IdentifiedArrayOf<Bowler.Summary>?
		@Shared(.game) var game: Game.Edit?
		@Shared(.frames) var frames: [Frame.Edit]?
		@Shared(.nextHeaderElement) var nextHeaderElement: GameDetailsHeaderNext.State.NextElement?

		var currentGameIndex: Int {
			bowlerGameIds[currentBowlerId]?.firstIndex(of: currentGameId) ?? 0
		}

		var currentBowlerIndex: Int {
			bowlerIds.firstIndex(of: currentBowlerId)!
		}

		func initialize() -> Effect<GamesEditorStateValidation.Action> {
			.send(.internal(.initialize))
		}

		mutating func updateActiveIndices(
			rollIndex newRollIndex: Int? = nil,
			frameIndex newFrameIndex: Int? = nil,
			gameId newGameId: Game.ID? = nil,
			bowlerId newBowlerId: Bowler.ID? = nil
		) -> Effect<GamesEditorStateValidation.Action> {
			guard let frames else { return .none }

			let isGameChanging = (newGameId != nil && newGameId != currentGameId) ||
				(newBowlerId != nil && newBowlerId != currentBowlerId)
			$currentFrame.withLock { $0.update(frameIndex: newFrameIndex, rollIndex: newRollIndex) }
			$currentGameId.withLock { $0 = newGameId ?? $0 }
			$currentBowlerId.withLock { $0 = newBowlerId ?? $0 }

			if !isGameChanging {
				let lastAccessibleRollInFrame = frames[currentFrame.frameIndex].lastAccessibleRollIndex
				if lastAccessibleRollInFrame < currentFrame.rollIndex {
					$currentFrame.withLock { $0.update(rollIndex: lastAccessibleRollInFrame) }
				}
			}

			let forceNextHeaderElementOrNil = game?.scoringMethod == .manual
				|| frames.nextFrameToRecord().hasUntouchedRoll != true
			guard !forceNextHeaderElementOrNil else {
				let numberOfBowlers = bowlerIds.count
				let numberOfGames = bowlerGameIds.first?.value.count ?? 1
				let nextGameIndex = currentGameIndex + 1

				if numberOfBowlers == 1 {
					$nextHeaderElement.withLock {
						$0 = numberOfGames > nextGameIndex ? .game(
							gameIndex: nextGameIndex,
							bowler: currentBowlerId,
							game: bowlerGameIds[currentBowlerId]![nextGameIndex]
						) : nil
					}
				} else {
					let nextBowlerIndex = (currentBowlerIndex + 1) % numberOfBowlers
					let nextBowlerId = bowlerIds[nextBowlerIndex]

					if nextBowlerIndex == 0 {
						$nextHeaderElement.withLock {
							$0 = numberOfGames > nextGameIndex ? .game(
								gameIndex: nextGameIndex,
								bowler: nextBowlerId,
								game: bowlerGameIds[nextBowlerId]![nextGameIndex]
							) : nil
						}
					} else {
						$nextHeaderElement.withLock {
							$0 = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
						}
					}
				}

				return .none
			}

			let isLastRoll = Frame.isLastRoll(currentFrame.rollIndex)
			let isLastFrame = Frame.isLast(currentFrame.frameIndex)
			let arePinsClearedForRoll = frames[currentFrame.frameIndex]
				.deck(forRoll: currentFrame.rollIndex)
				.arePinsCleared

			// If the current roll isn't the last, and there are still pins standing or it's the last frame,
			// just show the next ball to be thrown
			if !isLastRoll && (isLastFrame || !arePinsClearedForRoll) {
				$nextHeaderElement.withLock { $0 = .roll(rollIndex: currentFrame.rollIndex + 1) }
			} else {
				// In this case, the frame is finished

				let numberOfBowlers = bowlerIds.count
				let numberOfGames = bowlerGameIds.first?.value.count ?? 1
				let nextGameIndex = currentGameIndex + 1
				let nextFrameIndex = currentFrame.frameIndex + 1

				// If there's only one bowler, we only need to show either the next frame or next game
				if numberOfBowlers == 1 {
					// If it's the last frame, check if there's another game and go to it next, or show nothing
					if Frame.isLast(currentFrame.frameIndex) {
						$nextHeaderElement.withLock {
							$0 = numberOfGames > nextGameIndex ? .game(
								gameIndex: nextGameIndex,
								bowler: currentBowlerId,
								game: bowlerGameIds[currentBowlerId]![nextGameIndex]
							) : nil
						}
					} else {
						// If it's not the last frame, just show the next frame
						$nextHeaderElement.withLock { $0 = .frame(frameIndex: nextFrameIndex) }
					}
				} else {
					let nextBowlerIndex = (currentBowlerIndex + 1) % numberOfBowlers
					let nextBowlerId = bowlerIds[nextBowlerIndex]

					// If it's the last frame, we should show either the next bowler or, if there are no more bowlers,
					// the next game, or nothing
					if Frame.isLast(currentFrame.frameIndex) {
						// When `nextBowlerIndex` is 0, there are no bowlers following the current one.
						// Go to the next game, or show nothing
						if nextBowlerIndex == 0 {
							$nextHeaderElement.withLock {
								$0 = numberOfGames > nextGameIndex ? .game(
									gameIndex: nextGameIndex,
									bowler: nextBowlerId,
									game: bowlerGameIds[nextBowlerId]![nextGameIndex]
								) : nil
							}
						} else {
							// Otherwise, there's another bowler to show
							$nextHeaderElement.withLock {
								$0 = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
							}
						}
					} else {
						// If it's not the last frame, we should show the next bowler
						// When the next bowler's game is loaded, we'll load the correct frame at that time
						$nextHeaderElement.withLock {
							$0 = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
						}
					}
				}
			}

			return .none
		}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case initialize

			case framesDidChange([Frame.Edit]?)
			case gameDidChange(Game.Edit?)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(FramesRepository.self) var frames
	@Dependency(GamesRepository.self) var games

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case .initialize:
					return .merge(
						.publisher {
							state.$frames.publisher
								.map { .internal(.framesDidChange($0)) }
						},
						.publisher {
							state.$game.publisher
								.map { .internal(.gameDidChange($0)) }
						},
					)

				case let .framesDidChange(frames):
					return state.updateActiveIndices()

				case let .gameDidChange(game):
					return state.updateActiveIndices()
				}

			case .view(.doNothing), .delegate(.doNothing):
				return .none
			}
		}
	}
}

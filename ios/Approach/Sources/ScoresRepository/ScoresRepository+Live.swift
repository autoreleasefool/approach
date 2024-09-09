import Dependencies
import FramesRepositoryInterface
import GamesRepositoryInterface
import ModelsLibrary
import ScoreKeeperLibrary
import ScoresRepositoryInterface

struct SequencedRoll {
	let frameIndex: Int
	let rollIndex: Int
	let roll: Frame.Roll
}

extension ScoresRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			observeScore: { gameId in
				@Dependency(FramesRepository.self) var framesRepository
				@Dependency(GamesRepository.self) var gamesRepository
				return .init { continuation in
					let task = Task {
						do {
							guard let game = try await gamesRepository.findIndex(gameId) else {
								continuation.finish()
								return
							}

							let scoreKeeper = ScoreKeeper()

							for try await frames in framesRepository.observeRolls(gameId) {
								let scoredFrames = scoreKeeper.calculateScore(from: frames)
								let scoredGame = ScoredGame(id: gameId, index: game.index, frames: scoredFrames)
								continuation.yield(scoredGame)
							}
						} catch {
							continuation.finish(throwing: error)
						}
					}

					continuation.onTermination = { _ in task.cancel() }
				}
			},
			highestScorePossible: { gameId in
				@Dependency(FramesRepository.self) var framesRepository
				@Dependency(GamesRepository.self) var gamesRepository

				let scoreKeeper = ScoreKeeper()

				for try await frames in framesRepository.observeRolls(gameId) {
					return scoreKeeper.calculateHighestScorePossible(from: frames)
				}

				return 0
			}
		)
	}
}

import Dependencies
import ModelsLibrary

public struct ScoresRepository: Sendable {
	public var observeScore: @Sendable (Game.ID) -> AsyncThrowingStream<ScoredGame, Error>
	public var highestScorePossible: @Sendable (Game.ID) async throws -> Int

	public init(
		observeScore: @escaping @Sendable (Game.ID) -> AsyncThrowingStream<ScoredGame, Error>,
		highestScorePossible: @escaping @Sendable (Game.ID) async throws -> Int
	) {
		self.observeScore = observeScore
		self.highestScorePossible = highestScorePossible
	}

	public func observeScore(for gameId: Game.ID) -> AsyncThrowingStream<ScoredGame, Error> {
		self.observeScore(gameId)
	}
}

extension ScoresRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			observeScore: { _ in unimplemented("\(Self.self).observeScore") },
			highestScorePossible: { _ in unimplemented("\(Self.self).highestScorePossible") }
		)
	}
}

import Dependencies
import ModelsLibrary

public struct ScoresRepository: Sendable {
	public var observeScore: @Sendable (Game.ID) -> AsyncThrowingStream<ScoredGame, Error>

	public init(
		observeScore: @escaping @Sendable (Game.ID) -> AsyncThrowingStream<ScoredGame, Error>
	) {
		self.observeScore = observeScore
	}

	public func observeScore(for gameId: Game.ID) -> AsyncThrowingStream<ScoredGame, Error> {
		self.observeScore(gameId)
	}
}

extension ScoresRepository: TestDependencyKey {
	public static var testValue = Self(
		observeScore: { _ in unimplemented("\(Self.self).observeScore") }
	)
}

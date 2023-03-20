import Dependencies
import SharedModelsLibrary

public struct ScoringService: Sendable {
	public var calculateScoreForGame: @Sendable (Game.ID) async throws -> Int?
	public var calculateScoreForFrames: @Sendable ([Frame]) async throws -> Int?
	public var calculateScoreForFramesWithSteps: @Sendable ([Frame]) async throws -> [ScoreStep]

	public init(
		calculateScoreForGame: @escaping @Sendable (Game.ID) async throws -> Int?,
		calculateScoreForFrames: @escaping @Sendable ([Frame]) async throws -> Int?,
		calculateScoreForFramesWithSteps: @escaping @Sendable ([Frame]) async throws -> [ScoreStep]
	) {
		self.calculateScoreForGame = calculateScoreForGame
		self.calculateScoreForFrames = calculateScoreForFrames
		self.calculateScoreForFramesWithSteps = calculateScoreForFramesWithSteps
	}

	public func calculateScore(for game: Game.ID) async throws -> Int? {
		try await self.calculateScoreForGame(game)
	}

	public func calculateScore(for frames: [Frame]) async throws -> Int? {
		try await self.calculateScoreForFrames(frames)
	}

	public func calculateScoreWithSteps(for frames: [Frame]) async throws -> [ScoreStep] {
		try await self.calculateScoreForFramesWithSteps(frames)
	}
}

extension ScoringService: TestDependencyKey {
	public static var testValue = Self(
		calculateScoreForGame: { _ in fatalError("\(Self.self).calculateScoreForGame") },
		calculateScoreForFrames: { _ in fatalError("\(Self.self).calculateScoreForFrames") },
		calculateScoreForFramesWithSteps: { _ in fatalError("\(Self.self).calculateScoreForFramesWithSteps") }
	)
}

extension DependencyValues {
	public var scoringService: ScoringService {
		get { self[ScoringService.self] }
		set { self[ScoringService.self] = newValue }
	}
}

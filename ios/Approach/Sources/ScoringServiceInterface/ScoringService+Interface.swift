import Dependencies
import ModelsLibrary

public struct ScoringService: Sendable {
	public var calculateScoreForFrames: @Sendable ([Frame.Summary]) async throws -> Int?
	public var calculateScoreForFramesWithSteps: @Sendable ([Frame.Summary]) async throws -> [ScoreStep]

	public init(
		calculateScoreForFrames: @escaping @Sendable ([Frame.Summary]) async throws -> Int?,
		calculateScoreForFramesWithSteps: @escaping @Sendable ([Frame.Summary]) async throws -> [ScoreStep]
	) {
		self.calculateScoreForFrames = calculateScoreForFrames
		self.calculateScoreForFramesWithSteps = calculateScoreForFramesWithSteps
	}

	public func calculateScore(for frames: [Frame.Summary]) async throws -> Int? {
		try await self.calculateScoreForFrames(frames)
	}

	public func calculateScoreWithSteps(for frames: [Frame.Summary]) async throws -> [ScoreStep] {
		try await self.calculateScoreForFramesWithSteps(frames)
	}
}

extension ScoringService: TestDependencyKey {
	public static var testValue = Self(
		calculateScoreForFrames: { _ in unimplemented("\(Self.self).calculateScoreForFrames") },
		calculateScoreForFramesWithSteps: { _ in unimplemented("\(Self.self).calculateScoreForFramesWithSteps") }
	)
}

extension DependencyValues {
	public var scoringService: ScoringService {
		get { self[ScoringService.self] }
		set { self[ScoringService.self] = newValue }
	}
}

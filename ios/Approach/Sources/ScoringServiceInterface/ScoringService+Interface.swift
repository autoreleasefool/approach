import Dependencies
import ModelsLibrary

public struct ScoringService: Sendable {
	public var calculateScoreForFrames: @Sendable ([[Frame.OrderedRoll]]) async -> Int?
	public var calculateScoreForFramesWithSteps: @Sendable ([[Frame.OrderedRoll]]) async -> [ScoreStep]

	public init(
		calculateScoreForFrames: @escaping @Sendable ([[Frame.OrderedRoll]]) async -> Int?,
		calculateScoreForFramesWithSteps: @escaping @Sendable ([[Frame.OrderedRoll]]) async -> [ScoreStep]
	) {
		self.calculateScoreForFrames = calculateScoreForFrames
		self.calculateScoreForFramesWithSteps = calculateScoreForFramesWithSteps
	}

	public func calculateScore(for frames: [[Frame.OrderedRoll]]) async -> Int? {
		await self.calculateScoreForFrames(frames)
	}

	public func calculateScoreWithSteps(for frames: [[Frame.OrderedRoll]]) async -> [ScoreStep] {
		await self.calculateScoreForFramesWithSteps(frames)
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

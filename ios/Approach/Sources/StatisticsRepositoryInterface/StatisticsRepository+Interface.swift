import Dependencies
import ModelsLibrary
import StatisticsLibrary

public struct StatisticsRepository: Sendable {
	public var loadForBowler: @Sendable (Bowler.ID) async throws -> [any Statistic]

	public init(
		loadForBowler: @escaping @Sendable (Bowler.ID) async throws -> [any Statistic]
	) {
		self.loadForBowler = loadForBowler
	}

	public func load(forBowler: Bowler.ID) async throws -> [any Statistic] {
		try await self.loadForBowler(forBowler)
	}
}

extension StatisticsRepository: TestDependencyKey {
	public static var testValue = Self(
		loadForBowler: { _ in unimplemented("\(Self.self).loadForBowler") }
	)
}

extension DependencyValues {
	public var statistics: StatisticsRepository {
		get { self[StatisticsRepository.self] }
		set { self[StatisticsRepository.self] = newValue }
	}
}

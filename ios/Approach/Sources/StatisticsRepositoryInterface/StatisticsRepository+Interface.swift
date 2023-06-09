import Dependencies
import ModelsLibrary
import StatisticsLibrary

public struct StatisticsRepository: Sendable {
	public var loadSources: @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?
	public var loadStaticValues: @Sendable (TrackableFilter) async throws -> [any Statistic]

	public init(
		loadSources: @escaping @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?,
		loadStaticValues: @escaping @Sendable (TrackableFilter) async throws -> [any Statistic]
	) {
		self.loadSources = loadSources
		self.loadStaticValues = loadStaticValues
	}

	public func load(for filter: TrackableFilter) async throws -> [any Statistic] {
		try await self.loadStaticValues(filter)
	}
}

extension StatisticsRepository: TestDependencyKey {
	public static var testValue = Self(
		loadSources: { _ in unimplemented("\(Self.self).loadSources") },
		loadStaticValues: { _ in unimplemented("\(Self.self).loadStaticValues") }
	)
}

extension DependencyValues {
	public var statistics: StatisticsRepository {
		get { self[StatisticsRepository.self] }
		set { self[StatisticsRepository.self] = newValue }
	}
}

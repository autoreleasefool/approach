import Dependencies
import ModelsLibrary
import StatisticsLibrary

public struct StatisticsRepository: Sendable {
	public var loadSources: @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?
	public var loadStaticValues: @Sendable (TrackableFilter) async throws -> [any Statistic]
	public var loadChart: @Sendable (any GraphableStatistic.Type, TrackableFilter) async throws -> [ChartEntry]

	public init(
		loadSources: @escaping @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?,
		loadStaticValues: @escaping @Sendable (TrackableFilter) async throws -> [any Statistic],
		loadChart: @escaping @Sendable (any GraphableStatistic.Type, TrackableFilter) async throws -> [ChartEntry]
	) {
		self.loadSources = loadSources
		self.loadStaticValues = loadStaticValues
		self.loadChart = loadChart
	}

	public func load(for filter: TrackableFilter) async throws -> [any Statistic] {
		try await self.loadStaticValues(filter)
	}

	public func chart(statistic: any GraphableStatistic.Type, filter: TrackableFilter) async throws -> [ChartEntry] {
		try await self.loadChart(statistic, filter)
	}
}

extension StatisticsRepository: TestDependencyKey {
	public static var testValue = Self(
		loadSources: { _ in unimplemented("\(Self.self).loadSources") },
		loadStaticValues: { _ in unimplemented("\(Self.self).loadStaticValues") },
		loadChart: { _, _ in unimplemented("\(Self.self).loadChart") }
	)
}

extension DependencyValues {
	public var statistics: StatisticsRepository {
		get { self[StatisticsRepository.self] }
		set { self[StatisticsRepository.self] = newValue }
	}
}
